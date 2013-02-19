/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import com.luckypawsdaycare.web_cam.WebCamStreamer;

public class WebCamViewScreen extends Activity {
    ImageView webCamView;
    WebCamStreamer streamer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_cam_viewer);
        streamer = new WebCamStreamer(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        streamer.pauseStream();
        //Wait for the async to close gracefully
        while(streamer.isAsyncRunning()) {
            try{
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //Do nothing
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        findLayoutElements();
        streamer.beginStream();
    }

    private void findLayoutElements() {
        webCamView = (ImageView)findViewById(R.id.web_cam_pic);
    }

    public void updateImage(Bitmap image) {
        webCamView.setImageBitmap(image);
    }

    public void setDefaultImage() {
        webCamView.setImageResource(R.drawable.webcam_off);
    }
}
