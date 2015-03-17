/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.web_cam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WebCamAsync implements Runnable{
    private Handler updateHandler;
    private volatile Boolean keepGoing;
    private volatile Boolean running;

    AndroidHttpClient connection;
    HttpHost httpHost;
    BasicHttpContext executionContext;

    private volatile Bitmap image;
    private Bitmap defaultImage;

    private final String host = "luckypaws.lorexddns.net";
    private final String username = "luckypaws";
    private final String password = "1234";
//    private final String imageUrl = "http://luckypaws.lorexddns.net";
    private final String imageUrl = "http://luckypaws.lorexddns.net/c.cam?cid=228285271&nocache=";

    private final String TAG = "WebCamAsync";

    public WebCamAsync(Handler updateHandler, Bitmap defaultImage) {
        this.updateHandler = updateHandler;
        keepGoing = false;
        this.defaultImage = defaultImage;
        image = defaultImage;
        running = false;
    }

    public void setKeepGoing(boolean keepOnGoin) {
       synchronized (this) {
            keepGoing = keepOnGoin;
       }
    }

    public boolean isKeepGoing() {
        synchronized (this) {
            return keepGoing;
        }
    }

    public void startThread(AndroidHttpClient connection) {
        this.connection = connection;
        httpHost = new HttpHost(host, 80, "http");
        executionContext = new BasicHttpContext();

        new Thread(this).start();
    }

    public Bitmap getImage() {
        synchronized (this) {
            return image;
        }
    }

    private void setRunning(boolean isRunning) {
        synchronized (this) {
            running = isRunning;
        }
    }

    public boolean isRunning() {
        synchronized (this) {
            return running;
        }
    }

    @Override
    public void run() {
        HttpGet request = new HttpGet(imageUrl + System.currentTimeMillis());
        request.addHeader("Authorization", "Basic " + Base64.encodeToString((username+":"+password).getBytes(),Base64.NO_WRAP));
        setRunning(true);
        while(isKeepGoing()) {
            try {
                HttpResponse response = connection.execute(httpHost, request, executionContext);
                HttpEntity entity = response.getEntity();

                InputStream is = entity.getContent();
                BufferedInputStream bis = new BufferedInputStream(is);

                //Read in the remote file until we hit a -1 bit
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder builder = new StringBuilder();
                String line = reader.readLine();
                while(line != null) {
                    builder.append(line);
                    line = reader.readLine();
                }
                Log.d("JAR", builder.toString());
                ByteArrayBuffer buf = new ByteArrayBuffer(50);
                int current = 0;
                while ((current = bis.read()) != -1){
                    buf.append((byte) current);
                }

                byte[] data = buf.toByteArray();

                synchronized (this) {
                    image.recycle();
                    if(data.length > 0) {
                        image = BitmapFactory.decodeByteArray(data, 0, data.length);
                    } else {
                        image = defaultImage;
                    }
                }

                bis.close();
                is.close();

                Bundle messageData = new Bundle();
                messageData.putBoolean("success", true);
                Message message = new Message();
                message.setData(messageData);
                updateHandler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();

                Bundle messageData = new Bundle();
                messageData.putBoolean("success", false);
                Message message = new Message();
                message.setData(messageData);
                updateHandler.sendMessage(message);
            }

            try {
                Thread.sleep(750);
            } catch (InterruptedException e) {
                //Do Nothing
            }
        }
        setRunning(false);
    }
}
