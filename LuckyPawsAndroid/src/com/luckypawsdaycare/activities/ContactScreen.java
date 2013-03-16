/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.support.GpsUtility;

public class ContactScreen extends Activity {
    Button emailButton;
    Button directionsButton;
    GpsUtility gpsHelper;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.contact_screen);
        findAndWireElements();
        gpsHelper = new GpsUtility(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        gpsHelper.setUpLocationService();
    }

    @Override
    public void onPause() {
        super.onPause();
        gpsHelper.pauseLocationService();
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
            Location location = gpsHelper.getLocation();
            String lp_location = getResources().getString(R.string.lucky_paws_location_uri);
            String my_location = "";
            String fullUri = lp_location;
            if(location != null) {
                my_location = gpsHelper.getString(location);
                fullUri = String.format("%s&%s", my_location, lp_location);
            }

            //http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fullUri));
            startActivity(mapIntent);
        }
    };
}
