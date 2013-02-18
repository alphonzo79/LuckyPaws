/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.web_cam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.luckypawsdaycare.activities.R;
import com.luckypawsdaycare.activities.WebCamViewScreen;
import org.apache.http.impl.client.DefaultHttpClient;

public class WebCamStreamer {
    private final String TAG = "WebCamStreamer";

    private WebCamViewScreen caller;
    private WebCamAsync asyncTask;
    DefaultHttpClient client;
    boolean asyncRunning;

    public WebCamStreamer(WebCamViewScreen callingActivity) {
        caller = callingActivity;
        Bitmap defaultImage = BitmapFactory.decodeResource(callingActivity.getResources(), R.drawable.webcam_off);
        asyncTask = new WebCamAsync(updateImage, defaultImage);
    }

    public void beginStream() {
        Log.d(TAG, "Begin Stream called");
        if(checkWorkingHours()) {
            client = new DefaultHttpClient();

            asyncTask.setKeepGoing(true);
            asyncRunning = true;
            asyncTask.startThread(client);
        } // otherwise do nothing. Keep the default image up.
    }

    public void pauseStream() {
        Log.d(TAG, "pauseStream called");
        if(asyncRunning) {
            asyncTask.setKeepGoing(false);
            client.getConnectionManager().shutdown();
            client = null;
            asyncRunning = false;
        } //Otherwise do nothing
    }

    public Handler updateImage = new Handler(){
        @Override
        public void handleMessage (Message msg){
            boolean success = msg.getData().getBoolean("success", false);
            if(success) {
                caller.updateImage(asyncTask.getImage());
            } else {
                caller.setDefaultImage();
            }
        }
    };

    public boolean checkWorkingHours() {
        //todo
        return true;
    }
}
