/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.database.PersonalInfoDAO;

import java.util.Map;

public class PersonalInfoDisplay extends Activity {
    private final String TAG = "PersonalInfoDisplay";

    Map<String, String> dbValues;

    TextView fullName;
    TextView phone1;
    TextView phone2;
    TextView phone3;
    TextView addressStreet;
    TextView addressCity;
    TextView addressState;
    TextView addressZip;
    TextView eMail;

    Button editButton;

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
        findAndWireElements();
        bindValues();
    }

    private void findAndWireElements() {
        fullName = (TextView)findViewById(R.id.first_name_display);
        phone1 = (TextView)findViewById(R.id.phone_one_display);
        phone2 = (TextView)findViewById(R.id.phone_two_display);
        phone3 = (TextView)findViewById(R.id.phone_three_display);
        addressStreet = (TextView)findViewById(R.id.address_street_display);
        addressCity = (TextView)findViewById(R.id.address_city_display);
        addressState = (TextView)findViewById(R.id.address_state_display);
        addressZip = (TextView)findViewById(R.id.address_zip_display);
        eMail = (TextView)findViewById(R.id.email_display);

        editButton = (Button)findViewById(R.id.edit_button);
        editButton.setOnClickListener(editInfo);
    }

    private void bindValues() {
        String fullNameValue = "";
        String firstNameValue = dbValues.get("firstName");
        if(!TextUtils.isEmpty(firstNameValue)) {
            fullNameValue = fullNameValue.concat(firstNameValue + " ");
        }
        String lastNameValue = dbValues.get("lastName");
        if(!TextUtils.isEmpty(lastNameValue)) {
            fullNameValue = fullNameValue.concat(lastNameValue);
        }
        if(!TextUtils.isEmpty(fullNameValue)) {
            fullName.setText(fullNameValue);
        } else {
            fullName.setText(R.string.not_set);
        }

        String phone1Value = dbValues.get("phone1");
        if(!TextUtils.isEmpty(phone1Value)) {
            phone1.setText(phone1Value);
        } else {
            findViewById(R.id.phone_one_container).setVisibility(View.GONE);
        }

        String phone2Value = dbValues.get("phone2");
        if(!TextUtils.isEmpty(phone2Value)) {
            phone2.setText(phone2Value);
            //Shuffle the display if phone1 is gone
            if(TextUtils.isEmpty(phone1Value)) {
                findViewById(R.id.phone_two_container).setVisibility(View.GONE);
                findViewById(R.id.phone_one_container).setVisibility(View.VISIBLE);
                phone1.setText(phone2Value);
                ((TextView)findViewById(R.id.phone_one_label)).setText(R.string.phone2_label);
            }
        } else {
            findViewById(R.id.phone_two_container).setVisibility(View.GONE);
        }

        String phone3Value = dbValues.get("phone3");
        if(!TextUtils.isEmpty(phone3Value)) {
            phone3.setText(phone3Value);
            //shuffle things if we need to
            if(TextUtils.isEmpty(phone1Value) ^ TextUtils.isEmpty(phone2Value)){
                findViewById(R.id.phone_two_container).setVisibility(View.VISIBLE);
                findViewById(R.id.phone_three_display).setVisibility(View.GONE);
                findViewById(R.id.phone_three_labels).setVisibility(View.GONE);
                phone2.setText(phone3Value);
                ((TextView)findViewById(R.id.phone_two_label)).setText(R.string.phone3_label);
            }
        } else {
            findViewById(R.id.phone_three_labels).setVisibility(View.GONE);
            findViewById(R.id.phone_three_display).setVisibility(View.GONE);
        }

        String addressStreetValue = dbValues.get("addressStreet");
        if(!TextUtils.isEmpty(addressStreetValue)) {
            addressStreet.setText(addressStreetValue);
        } else {
            findViewById(R.id.address_street_labels).setVisibility(View.GONE);
            findViewById(R.id.address_street_display).setVisibility(View.GONE);
        }

        String addressCityValue = dbValues.get("addressCity");
        if(!TextUtils.isEmpty(addressCityValue)) {
            addressCity.setText(addressCityValue);
        } else {
            findViewById(R.id.address_city_container).setVisibility(View.GONE);
        }

        String addressStateValue = dbValues.get("addressState");
        if(!TextUtils.isEmpty(addressStateValue)) {
            addressState.setText(addressStateValue);
            //Shuffle things if we need to
            if(TextUtils.isEmpty(addressCityValue)) {
                findViewById(R.id.address_city_container).setVisibility(View.VISIBLE);
                findViewById(R.id.address_state_container).setVisibility(View.GONE);
                addressCity.setText(addressStateValue);
                ((TextView)findViewById(R.id.address_city_label)).setText(R.string.address_state_label);
            }
        } else {
            findViewById(R.id.address_state_container).setVisibility(View.GONE);
        }

        String addressZipValue = dbValues.get("addressZip");
        if(!TextUtils.isEmpty(addressZipValue)) {
            addressZip.setText(addressZipValue);
            //shuffle things if we need to
            if(TextUtils.isEmpty(addressCityValue) && TextUtils.isEmpty(addressStateValue)) {
                findViewById(R.id.address_city_container).setVisibility(View.VISIBLE);
                findViewById(R.id.address_zip_container).setVisibility(View.GONE);
                addressCity.setText(addressZipValue);
                ((TextView)findViewById(R.id.address_city_label)).setText(R.string.address_zip_label);
            }
        } else {
            findViewById(R.id.address_zip_container).setVisibility(View.GONE);
        }

        String emailValue = dbValues.get("eMail");
        if(!TextUtils.isEmpty(emailValue)) {
            eMail.setText(emailValue);
        } else {
            findViewById(R.id.email_labels).setVisibility(View.GONE);
            findViewById(R.id.email_display).setVisibility(View.GONE);
        }
    }

    Button.OnClickListener editInfo = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent edit = new Intent(PersonalInfoDisplay.this, PersonalInfoEdit.class);
            startActivity(edit);
            PersonalInfoDisplay.this.finish();
        }
    };
}
