/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Spinner;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.support.SimpleSpinnerAdapter;
import com.luckypawsdaycare.support.SimpleSpinnerValue;

import java.util.ArrayList;
import java.util.List;

public class EditMyPets extends Activity {
    List<SimpleSpinnerValue> dogCatOptions;

    Spinner dogCatSpinner;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.my_pets_add_edit);

        setDisplayValues();
        //findAndWireSpinners();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void findAndWireSpinners() {
        dogCatSpinner = (Spinner)findViewById(R.id.dog_cat_spinner);
        dogCatSpinner.setAdapter(new SimpleSpinnerAdapter(this, dogCatOptions));
    }

    private void setDisplayValues() {
        setSpinnerValues();
    }

    private void setSpinnerValues() {
        String[] options = getResources().getStringArray(R.array.dog_cat_spinner_entries);
        dogCatOptions = new ArrayList<SimpleSpinnerValue>();
        for(String option : options) {
            dogCatOptions.add(new SimpleSpinnerValue(option, false));
        }
    }
}
