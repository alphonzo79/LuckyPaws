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
import android.widget.EditText;
import android.widget.Toast;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.database.PersonalInfoDAO;
import com.luckypawsdaycare.database.PersonalInfoTableColumns;

import java.util.Map;

public class PersonalInfoEdit extends Activity {
    Map<String, String> dbValues;

    EditText firstName;
    EditText lastName;
    EditText phone1;
    EditText phone2;
    EditText phone3;
    EditText addressStreet;
    EditText addressCity;
    EditText addressState;
    EditText addressZip;
    EditText eMail;

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
        firstName = (EditText)findViewById(R.id.first_name_input);
        lastName = (EditText)findViewById(R.id.last_name_input);
        phone1 = (EditText)findViewById(R.id.phone_one_input);
        phone2 = (EditText)findViewById(R.id.phone_two_input);
        phone3 = (EditText)findViewById(R.id.phone_three_input);
        addressStreet = (EditText)findViewById(R.id.address_street_input);
        addressCity = (EditText)findViewById(R.id.address_city_input);
        addressState = (EditText)findViewById(R.id.address_state_input);
        addressZip = (EditText)findViewById(R.id.address_zip_input);
        eMail = (EditText)findViewById(R.id.email_input);

        saveButton = (Button)findViewById(R.id.save_button);
        saveButton.setOnClickListener(saveInfo);
        cancelButton = (Button)findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelEdit);
    }

    private void bindValues() {
        String firstNameValue = dbValues.get(PersonalInfoTableColumns.FIRST_NAME.getString());
        if(!TextUtils.isEmpty(firstNameValue)) {
            firstName.setText(firstNameValue);
        }

        String lastNameValue = dbValues.get(PersonalInfoTableColumns.LAST_NAME.getString());
        if(!TextUtils.isEmpty(lastNameValue)) {
            lastName.setText(lastNameValue);
        }

        String phone1Value = dbValues.get(PersonalInfoTableColumns.PHONE_ONE.getString());
        if(!TextUtils.isEmpty(phone1Value)) {
            phone1.setText(phone1Value);
        }

        String phone2Value = dbValues.get(PersonalInfoTableColumns.PHONE_TWO.getString());
        if(!TextUtils.isEmpty(phone2Value)) {
            phone2.setText(phone2Value);
        }

        String phone3Value = dbValues.get(PersonalInfoTableColumns.PHONE_THREE.getString());
        if(!TextUtils.isEmpty(phone3Value)) {
            phone3.setText(phone3Value);
        }

        String addressStreetValue = dbValues.get(PersonalInfoTableColumns.STREET.getString());
        if(!TextUtils.isEmpty(addressStreetValue)) {
            addressStreet.setText(addressStreetValue);
        }

        String addressCityValue = dbValues.get(PersonalInfoTableColumns.CITY.getString());
        if(!TextUtils.isEmpty(addressCityValue)) {
            addressCity.setText(addressCityValue);
        }

        String addressStateValue = dbValues.get(PersonalInfoTableColumns.STATE.getString());
        if(!TextUtils.isEmpty(addressStateValue)) {
            addressState.setText(addressStateValue);
        }

        String addressZipValue = dbValues.get(PersonalInfoTableColumns.ZIP.getString());
        if(!TextUtils.isEmpty(addressZipValue) && !addressZipValue.equals("0")) {
            addressZip.setText(addressZipValue);
        }

        String emailValue = dbValues.get(PersonalInfoTableColumns.EMAIL.getString());
        if(!TextUtils.isEmpty(emailValue)) {
            eMail.setText(emailValue);
        }
    }

    Button.OnClickListener saveInfo = new Button.OnClickListener() {
        public void onClick(View v) {
            String firstNameValue = firstName.getText().toString();
            if(!TextUtils.isEmpty(firstNameValue)) {
                dbValues.put(PersonalInfoTableColumns.FIRST_NAME.getString(), firstNameValue);
            } else {
                dbValues.put(PersonalInfoTableColumns.FIRST_NAME.getString(), "");
            }

            String lastNameValue = lastName.getText().toString();
            if(!TextUtils.isEmpty(lastNameValue)) {
                dbValues.put(PersonalInfoTableColumns.LAST_NAME.getString(), lastNameValue);
            } else {
                dbValues.put(PersonalInfoTableColumns.LAST_NAME.getString(), "");
            }

            String phone1Value = phone1.getText().toString();
            if(!TextUtils.isEmpty(phone1Value)) {
                dbValues.put(PersonalInfoTableColumns.PHONE_ONE.getString(), phone1Value);
            } else {
                dbValues.put(PersonalInfoTableColumns.PHONE_ONE.getString(), "");
            }

            String phone2Value = phone2.getText().toString();
            if(!TextUtils.isEmpty(phone2Value)) {
                dbValues.put(PersonalInfoTableColumns.PHONE_TWO.getString(), phone2Value);
            } else {
                dbValues.put(PersonalInfoTableColumns.PHONE_TWO.getString(), "");
            }

            String phone3Value = phone3.getText().toString();
            if(!TextUtils.isEmpty(phone3Value)) {
                dbValues.put(PersonalInfoTableColumns.PHONE_THREE.getString(), phone3Value);
            } else {
                dbValues.put(PersonalInfoTableColumns.PHONE_THREE.getString(), "");
            }

            String addressStreetValue = addressStreet.getText().toString();
            if(!TextUtils.isEmpty(addressStreetValue)) {
                dbValues.put(PersonalInfoTableColumns.STREET.getString(), addressStreetValue);
            } else {
                dbValues.put(PersonalInfoTableColumns.STREET.getString(), "");
            }

            String addressCityValue = addressCity.getText().toString();
            if(!TextUtils.isEmpty(addressCityValue)) {
                dbValues.put(PersonalInfoTableColumns.CITY.getString(), addressCityValue);
            } else {
                dbValues.put(PersonalInfoTableColumns.CITY.getString(), "");
            }

            String addressStateValue = addressState.getText().toString();
            if(!TextUtils.isEmpty(addressStateValue)) {
                dbValues.put(PersonalInfoTableColumns.STATE.getString(), addressStateValue);
            } else {
                dbValues.put(PersonalInfoTableColumns.STATE.getString(), "");
            }

            String addressZipValue = addressZip.getText().toString();
            if(!TextUtils.isEmpty(addressZipValue) && !addressZipValue.equals("0")) {
                dbValues.put(PersonalInfoTableColumns.ZIP.getString(), addressZipValue);
            } else {
                dbValues.put(PersonalInfoTableColumns.ZIP.getString(), "");
            }

            String emailValue = eMail.getText().toString();
            if(!TextUtils.isEmpty(emailValue)) {
                dbValues.put(PersonalInfoTableColumns.EMAIL.getString(), emailValue);
            } else {
                dbValues.put(PersonalInfoTableColumns.EMAIL.getString(), "");
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
