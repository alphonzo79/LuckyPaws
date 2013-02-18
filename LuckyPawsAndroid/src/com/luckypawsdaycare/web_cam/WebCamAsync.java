/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.web_cam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class WebCamAsync implements Runnable{
    private Handler updateHandler;
    private volatile Boolean keepGoing;
    DefaultHttpClient connection;
    private volatile Bitmap image;
    private final String imageUrl = "http://luckypaws.lorexddns.net/jpg/image.jpg";

    private final String TAG = "WebCamAsync";

    public WebCamAsync(Handler updateHandler) {
        this.updateHandler = updateHandler;
        keepGoing = false;
    }

    public void setKeepGoing(boolean keepOnGoin) {
       synchronized (keepGoing) {
            keepGoing = keepOnGoin;
       }
    }

    private boolean isKeepGoing() {
        synchronized (keepGoing) {
            return keepGoing;
        }
    }

    public void startThread(DefaultHttpClient connection) {
        this.connection = connection;
        new Thread(this).start();
    }

    public Bitmap getImage() {
        synchronized (image) {
            return image;
        }
    }

    @Override
    public void run() {
        Log.d(TAG, "Starting the runnable");
        HttpGet request = new HttpGet(imageUrl);
        while(isKeepGoing()) {
            try {
                HttpResponse response = connection.execute(request);
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
                    image = BitmapFactory.decodeByteArray(data, 0, data.length);
                }

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
    }
}
