/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.database.DatabaseConstants;
import com.luckypawsdaycare.database.SettingsDAO;
import com.luckypawsdaycare.web_cam.WebCamStreamer;

public class WebCamViewScreen extends Activity {
    ImageView webCamView;
    WebCamStreamer streamer;
    boolean lockScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_cam_viewer);
        streamer = new WebCamStreamer(this);

        findLayoutElements();
    }

    @Override
    public void onPause() {
        super.onPause();
        streamer.pauseStream();

        if(lockScreen) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        streamer.beginStream();

        SettingsDAO db = new SettingsDAO(this);
        lockScreen = db.getPersistentSetting(DatabaseConstants.SETTINGS_SCREEN_LOCK_SETTING);
        if(lockScreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        streamer.killStream();
        //Wait for the async to close gracefully before stopping the activity
        while(streamer.isAsyncRunning()) {
            try{
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //Do nothing
            }
        }
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
