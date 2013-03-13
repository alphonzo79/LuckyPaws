/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.database.PersonalInfoDAO;

import java.util.Map;

public class PersonalInfoEdit extends Activity {
    Map<String, String> dbValues;

    TextView firstName;
    TextView lastName;
    TextView phone1;
    TextView phone2;
    TextView phone3;
    TextView addressStreet;
    TextView addressCity;
    TextView addressState;
    TextView addressZip;
    TextView eMail;

    Button saveButton;
    Button cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info_edit);

        PersonalInfoDAO db = new PersonalInfoDAO(this);
        dbValues = db.getPersonalInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        findAndWireElement();
        bindValues();
    }

    private void findAndWireElement() {
        firstName = (TextView)findViewById(R.id.first_name_input);
        lastName = (TextView)findViewById(R.id.last_name_input);
        phone1 = (TextView)findViewById(R.id.phone_one_input);
        phone2 = (TextView)findViewById(R.id.phone_two_input);
        phone3 = (TextView)findViewById(R.id.phone_three_input);
        addressStreet = (TextView)findViewById(R.id.address_street_input);
        addressCity = (TextView)findViewById(R.id.address_city_input);
        addressState = (TextView)findViewById(R.id.address_state_input);
        addressZip = (TextView)findViewById(R.id.address_zip_input);
        eMail = (TextView)findViewById(R.id.email_input);

        saveButton = (Button)findViewById(R.id.save_button);
        saveButton.setOnClickListener(saveInfo);
        cancelButton = (Button)findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelEdit);
    }

    private void bindValues() {
        String firstNameValue = dbValues.get("firstName");
        if(!TextUtils.isEmpty(firstNameValue)) {
            firstName.setText(firstNameValue);
        }

        String lastNameValue = dbValues.get("lastName");
        if(!TextUtils.isEmpty(lastNameValue)) {
            lastName.setText(lastNameValue);
        }

        String phone1Value = dbValues.get("phone1");
        if(!TextUtils.isEmpty(phone1Value)) {
            phone1.setText(phone1Value);
        }

        String phone2Value = dbValues.get("phone2");
        if(!TextUtils.isEmpty(phone2Value)) {
            phone2.setText(phone2Value);
        }

        String phone3Value = dbValues.get("phone3");
        if(!TextUtils.isEmpty(phone3Value)) {
            phone3.setText(phone3Value);
        }

        String addressStreetValue = dbValues.get("addressStreet");
        if(!TextUtils.isEmpty(addressStreetValue)) {
            addressStreet.setText(addressStreetValue);
        }

        String addressCityValue = dbValues.get("addressCity");
        if(!TextUtils.isEmpty(addressCityValue)) {
            addressCity.setText(addressCityValue);
        }

        String addressStateValue = dbValues.get("addressState");
        if(!TextUtils.isEmpty(addressStateValue)) {
            addressState.setText(addressStateValue);
        }

        String addressZipValue = dbValues.get("addressZip");
        if(!TextUtils.isEmpty(addressZipValue)) {
            addressZip.setText(addressZipValue);
        }

        String emailValue = dbValues.get("eMail");
        if(!TextUtils.isEmpty(emailValue)) {
            eMail.setText(emailValue);
        }
    }

    Button.OnClickListener saveInfo = new Button.OnClickListener() {
        public void onClick(View v) {
            String firstNameValue = firstName.getText().toString();
            if(!TextUtils.isEmpty(firstNameValue)) {
                dbValues.put("firstName", firstNameValue);
            } else {
                dbValues.put("firstName", "");
            }

            String lastNameValue = lastName.getText().toString();
            if(!TextUtils.isEmpty(lastNameValue)) {
                dbValues.put("lastName", lastNameValue);
            } else {
                dbValues.put("lastName", "");
            }

            String phone1Value = phone1.getText().toString();
            if(!TextUtils.isEmpty(phone1Value)) {
                dbValues.put("phone1", phone1Value);
            } else {
                dbValues.put("phone1", "");
            }

            String phone2Value = phone2.getText().toString();
            if(!TextUtils.isEmpty(phone2Value)) {
                dbValues.put("phone2", phone2Value);
            } else {
                dbValues.put("phone2", "");
            }

            String phone3Value = phone3.getText().toString();
            if(!TextUtils.isEmpty(phone3Value)) {
                dbValues.put("phone3", phone3Value);
            } else {
                dbValues.put("phone3", "");
            }

            String addressStreetValue = addressStreet.getText().toString();
            if(!TextUtils.isEmpty(addressStreetValue)) {
                dbValues.put("addressStreet", addressStreetValue);
            } else {
                dbValues.put("addressStreet", "");
            }

            String addressCityValue = addressCity.getText().toString();
            if(!TextUtils.isEmpty(addressCityValue)) {
                dbValues.put("addressCity", addressCityValue);
            } else {
                dbValues.put("addressCity", "");
            }

            String addressStateValue = addressState.getText().toString();
            if(!TextUtils.isEmpty(addressStateValue)) {
                dbValues.put("addressState", addressStateValue);
            } else {
                dbValues.put("addressState", "");
            }

            String addressZipValue = addressZip.getText().toString();
            if(!TextUtils.isEmpty(addressZipValue)) {
                dbValues.put("addressZip", addressZipValue);
            } else {
                dbValues.put("addressZip", "");
            }

            String emailValue = eMail.getText().toString();
            if(!TextUtils.isEmpty(emailValue)) {
                dbValues.put("eMail", emailValue);
            } else {
                dbValues.put("eMail", "");
            }

            PersonalInfoDAO db = new PersonalInfoDAO(PersonalInfoEdit.this);
            boolean success = db.setPersonalInfo(dbValues);
            if(!success) {
                Toast warning = Toast.makeText(PersonalInfoEdit.this, R.string.error_info_save, Toast.LENGTH_SHORT);
                warning.setGravity(Gravity.CENTER, 0, 0);
                warning.show();
            } else {
                Intent display = new Intent(PersonalInfoEdit.this, PersonalInfoDisplay.class);
                startActivity(display);
                PersonalInfoEdit.this.finish();
            }
        }
    };

    Button.OnClickListener cancelEdit = new Button.OnClickListener() {
        public void onClick(View v) {
            PersonalInfoEdit.this.finish();
        }
    };
}
