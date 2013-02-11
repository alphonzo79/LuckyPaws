/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class WebCamViewScreen extends Activity {
    ImageView webCamView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_cam_viewer);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        findLayoutElements();
    }

    private void findLayoutElements() {
        webCamView = (ImageView)findViewById(R.id.web_cam_pic);
    }
}
