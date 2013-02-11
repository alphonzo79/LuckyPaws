/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.web_cam;

import android.widget.ImageView;

public class WebCamStreamer {
    private final String imageUrl = "http://luckypaws.lorexddns.net/jpg/image.jpg";
    private final String username = "luckypaws";
    private final String password = "1234";

    private ImageView webCamDisplay;

    public WebCamStreamer(ImageView display) {
        webCamDisplay = display;
    }

    public void beginStream() {

    }

    public void pauseStream() {

    }
}
