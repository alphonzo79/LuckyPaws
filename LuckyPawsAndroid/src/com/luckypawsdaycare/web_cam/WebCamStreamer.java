/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.web_cam;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.luckypawsdaycare.activities.WebCamViewScreen;
import org.apache.http.impl.client.DefaultHttpClient;

public class WebCamStreamer {
    private final String TAG = "WebCamStreamer";

    private WebCamViewScreen caller;
    private WebCamAsync asyncTask;
    DefaultHttpClient client;

    public WebCamStreamer(WebCamViewScreen callingActivity) {
        caller = callingActivity;
        asyncTask = new WebCamAsync(updateImage);
    }

    public void beginStream() {
        Log.d(TAG, "Begin Stream called");
        client = new DefaultHttpClient();

        asyncTask.setKeepGoing(true);
        asyncTask.startThread(client);
    }

    public void pauseStream() {
        Log.d(TAG, "pauseStream called");
        asyncTask.setKeepGoing(false);
        client.getConnectionManager().shutdown();
        client = null;
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
}
