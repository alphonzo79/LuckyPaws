/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.luckypawsdaycare.R;

public class ContactScreen extends Activity {
    Button emailButton;
    Button directionsButton;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.contact_screen);
        findAndWireElements();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void findAndWireElements() {
        emailButton = (Button)findViewById(R.id.email_button);
        emailButton.setOnClickListener(sendEmailIntent);

        directionsButton = (Button)findViewById(R.id.directions_button);
        directionsButton.setOnClickListener(launchDrivingDirections);
    }

    Button.OnClickListener sendEmailIntent = new Button.OnClickListener(){
        public void onClick(View v) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {getResources().getString(R.string.lucky_paws_email)});
            startActivity(Intent.createChooser(emailIntent, "Send E-mail"));
        }
    };

    Button.OnClickListener launchDrivingDirections = new Button.OnClickListener(){
        public void onClick(View v) {
            String lp_location = "geo:0,0?q=" + getString(R.string.lucky_paws_location_uri);
            lp_location = lp_location + " (" + getString(R.string.app_name) + ")";
            //geo:0,0?q=37.423156,-122.084917 (" + name + ")

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(lp_location));
            startActivity(mapIntent);
        }
    };
}
