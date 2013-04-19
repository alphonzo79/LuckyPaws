/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.reservations_support;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import com.luckypawsdaycare.R;

import java.util.List;

public class DogSelector extends PetSelector {
    CheckBox exitBathCheckBox;

    public DogSelector(Activity context, LinearLayout rootLayout, List<String> dogNames) {
        super(context, rootLayout, dogNames);
    }

    @Override
    protected void inflateLayoutFindElements() {
        LayoutInflater inflater = activity.getLayoutInflater();
        animalLayout = (LinearLayout)inflater.inflate(R.layout.reservations_dog_layout, animalRoot, true);

        animalSelector = (Spinner)animalLayout.findViewById(R.id.dog_name_spinner);
        customNameField = (EditText)animalLayout.findViewById(R.id.dog_name_input);
        exitBathCheckBox = (CheckBox)animalLayout.findViewById(R.id.bath_checkbox);
    }

    public boolean doExitBath() {
        return exitBathCheckBox.isChecked();
    }
}
