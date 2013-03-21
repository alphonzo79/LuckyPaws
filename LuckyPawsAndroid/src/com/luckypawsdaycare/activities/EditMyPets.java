/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.support.DateUtilities;
import com.luckypawsdaycare.support.SimpleSpinnerValue;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditMyPets extends Activity {
    List<SimpleSpinnerValue> dogCatOptions;

    int day;
    int month;
    int year;

    int id;
    String name;
    int dogCat;
    int sex;
    String breed;
    String birthdate;
    int size;
    int fixed;

    EditText nameInput;
    Spinner dogCatSpinner;
    Spinner sexSpinner;
    EditText breedInput;
    TextView birthdateInput;
    Spinner sizeSpinner;
    Spinner fixedSpinner;
    TextView fixedLabel;

    Button save;
    Button cancel;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.my_pets_add_edit);

        findAndWireElements();
        setDisplayValues();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void findAndWireElements() {
        nameInput = (EditText)findViewById(R.id.name_input);
        dogCatSpinner = (Spinner)findViewById(R.id.dog_cat_spinner);
        sexSpinner = (Spinner)findViewById(R.id.sex_spinner);
        breedInput = (EditText)findViewById(R.id.breed_input);
        birthdateInput = (TextView)findViewById(R.id.dob_picker);
        birthdateInput.setOnClickListener(launchDatePicker);
        sizeSpinner = (Spinner)findViewById(R.id.size_spinner);
        fixedSpinner = (Spinner)findViewById(R.id.fixed_spinner);
        fixedLabel = (TextView)findViewById(R.id.fixed_label);

        save = (Button)findViewById(R.id.save_button);
        save.setOnClickListener(saveInfo);
        cancel = (Button)findViewById(R.id.cancel_button);
        cancel.setOnClickListener(cancelClick);
    }

    private void setDisplayValues() {
        //todo if this is an edit intent
        //todo otherwise
        year = 2013;
        day = 1;
        month = 1;
    }

    TextView.OnClickListener launchDatePicker = new TextView.OnClickListener(){
        public void onClick(View v) {
            DatePickerDialog dialog = new DatePickerDialog(EditMyPets.this, dateSet, year, month, day);
            dialog.show();
        }
    };

    DatePickerDialog.OnDateSetListener dateSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            //Month is 0 - 11 for compatibility with Calendar
            EditMyPets.this.year = year;
            EditMyPets.this.month = month;
            EditMyPets.this.day = day;
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            birthdate = DateUtilities.appDateFormat().format(cal.getTime());
            birthdateInput.setText(birthdate);
        }
    };

    Button.OnClickListener cancelClick = new Button.OnClickListener() {
        public void onClick(View v) {
            EditMyPets.this.finish();
        }
    };

    Button.OnClickListener saveInfo = new Button.OnClickListener() {
        public void onClick(View v) {
            Map<String, String> updateArgs = new HashMap<String, String>();

        }
    };
}
