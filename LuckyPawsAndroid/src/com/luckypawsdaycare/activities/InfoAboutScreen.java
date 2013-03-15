/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.luckypawsdaycare.R;

public class InfoAboutScreen extends Activity {
    Button closeButton;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.info_about_screen);

        closeButton = (Button)findViewById(R.id.exit_button);
        closeButton.setOnClickListener(exit);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    Button.OnClickListener exit = new Button.OnClickListener() {
        public void onClick(View v) {
            InfoAboutScreen.this.finish();
        }
    };
}
