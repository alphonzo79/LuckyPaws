/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.database.PersonalInfoDAO;

import java.util.Map;

public class PersonalInfoDisplay extends Activity {
    Map<String, String> dbValues;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info_display);

        PersonalInfoDAO db = new PersonalInfoDAO(this);
        dbValues = db.getPersonalInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void findAndWireElements() {
        //todo
    }

    private void bindValues() {
        //todo
    }

    Button.OnClickListener editInfo = new Button.OnClickListener() {
        public void onClick(View v) {
            //todo
        }
    };
}
