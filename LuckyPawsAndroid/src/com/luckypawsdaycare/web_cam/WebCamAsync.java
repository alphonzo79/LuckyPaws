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
import java.io.IOException;
import java.io.InputStream;

public class WebCamAsync implements Runnable{
    private Handler updateHandler;
    private volatile Boolean keepGoing;
    private volatile Boolean running;

    AndroidHttpClient connection;
    HttpHost httpHost;
    BasicHttpContext executionContext;

    private volatile Bitmap image;

    private final String host = "luckypaws.lorexddns.net";
    private final String username = "luckypaws";
    private final String password = "1234";
    private final String imageUrl = "http://luckypaws.lorexddns.net/jpg/image.jpg";

    private final String TAG = "WebCamAsync";

    public WebCamAsync(Handler updateHandler, Bitmap defaultImage) {
        this.updateHandler = updateHandler;
        keepGoing = false;
        image = defaultImage;
        running = false;
    }

    public void setKeepGoing(boolean keepOnGoin) {
       synchronized (keepGoing) {
            keepGoing = keepOnGoin;
       }
    }

    public boolean isKeepGoing() {
        synchronized (keepGoing) {
            return keepGoing;
        }
    }

    public void startThread(AndroidHttpClient connection) {
        this.connection = connection;
        httpHost = new HttpHost(host, 80, "http");

        new Thread(this).start();
    }

    public Bitmap getImage() {
        synchronized (image) {
            return image;
        }
    }

    private void setRunning(boolean isRunning) {
        synchronized (running) {
            running = isRunning;
        }
    }

    public boolean isRunning() {
        synchronized (running) {
            return running;
        }
    }

    @Override
    public void run() {
        Log.d(TAG, "Starting the runnable");
        HttpGet request = new HttpGet(imageUrl);
        request.addHeader("Authorization", "Basic " + Base64.encodeToString((username+":"+password).getBytes(),Base64.NO_WRAP));
        setRunning(true);
        while(isKeepGoing()) {
            try {
                HttpResponse response = connection.execute(httpHost, request, executionContext);
                HttpEntity entity = response.getEntity();

                InputStream is = entity.getContent();
                BufferedInputStream bis = new BufferedInputStream(is);

                //Read in the remote file until we hit a -1 bit
                ByteArrayBuffer buf = new ByteArrayBuffer(50);
                int current = 0;
                while ((current = bis.read()) != -1){
                    buf.append((byte) current);
                }

                byte[] data = buf.toByteArray();

                synchronized (image) {
                    image.recycle();
                    image = BitmapFactory.decodeByteArray(data, 0, data.length);
                }

                bis.close();
                is.close();

                Log.d(TAG, "Built the bitmap, sending the update message");

                Bundle messageData = new Bundle();
                messageData.putBoolean("success", true);
                Message message = new Message();
                message.setData(messageData);
                updateHandler.sendMessage(message);
            } catch (IOException e) {
                Log.d(TAG, "Caught an IO Exception");
                e.printStackTrace();

                Bundle messageData = new Bundle();
                messageData.putBoolean("success", false);
                Message message = new Message();
                message.setData(messageData);
                updateHandler.sendMessage(message);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //Do Nothing
            }
        }
        setRunning(false);
    }
}
