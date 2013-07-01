/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.reservations_support;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.*;
import com.luckypawsdaycare.R;

import java.util.List;

public class DogSelector extends PetSelector {
    CheckBox exitBathCheckBox;
    PriceProcessor priceProcessor;

    public DogSelector(Activity context, LinearLayout rootLayout, List<String> dogNames, int index, PriceProcessor priceProcessor) {
        super(context, rootLayout, dogNames, index);
        this.priceProcessor = priceProcessor;
    }

    @Override
    protected void inflateLayoutFindElements() {
        LayoutInflater inflater = activity.getLayoutInflater();
        animalLayout = (LinearLayout)inflater.inflate(R.layout.reservations_dog_layout, animalRoot, false);

        animalSelector = (Spinner)animalLayout.findViewById(R.id.dog_name_spinner);
        customNameField = (EditText)animalLayout.findViewById(R.id.dog_name_input);
        exitBathCheckBox = (CheckBox)animalLayout.findViewById(R.id.bath_checkbox);
        exitBathCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked) {
                    priceProcessor.changeNumBaths(1);
                } else {
                    priceProcessor.changeNumBaths(-1);
                }
            }
        });

        nameLabel = (TextView)animalLayout.findViewById(R.id.dog_name_label);
        String dogNum = activity.getString(R.string.dog_num) + (index + 1);
        nameLabel.setText(dogNum);

        animalRoot.addView(animalLayout);
    }

    public boolean doExitBath() {
        return exitBathCheckBox.isChecked();
    }

    CheckBox.OnCheckedChangeListener checkChanged = new CheckBox.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            if(checked) {
                priceProcessor.changeNumBaths(1);
            } else {
                priceProcessor.changeNumBaths(-1);
            }
        }
    };
}
