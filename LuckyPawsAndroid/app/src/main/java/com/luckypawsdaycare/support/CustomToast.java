/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.support;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.luckypawsdaycare.R;

public class CustomToast {
    Activity context;
    String text;
    View layout;

    public CustomToast(Activity context, String message) {
        this.context = context;
        this.text = message;
        LayoutInflater inflater = context.getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, (ViewGroup)context.findViewById(R.id.toast_base_layout));
        ((TextView)layout.findViewById(R.id.toast_text)).setText(text);
    }

    public void show() {
        Toast toShow = new Toast(context);
        toShow.setDuration(Toast.LENGTH_LONG);
        toShow.setGravity(Gravity.CENTER, 0, 0);
        toShow.setView(layout);
        toShow.show();
    }
}
