/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.reservations_support;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.luckypawsdaycare.R;

import java.util.ArrayList;
import java.util.List;

public abstract class PetSelector {
    Activity activity;
    ViewGroup animalRoot;
    List<String> animalNames;
    int index;

    int customSelected;

    LinearLayout animalLayout;
    TextView nameLabel;
    Spinner animalSelector;
    EditText customNameField;

    public PetSelector(Activity context, LinearLayout rootLayout, List<String> petNames, int index) {
        activity = context;
        animalRoot = rootLayout;

        animalNames = new ArrayList<String>();
        animalNames.add(activity.getString(R.string.choose_pet));
        animalNames.addAll(petNames);
        animalNames.add(activity.getString(R.string.custom_pet_name));

        customSelected = animalNames.size() - 1;

        this.index = index;

        inflateLayoutFindElements();
        setUpSpinner();
    }

    protected abstract void inflateLayoutFindElements();

    private void setUpSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, animalNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        animalSelector.setAdapter(adapter);
        animalSelector.setSelection(0);

        animalSelector.setOnItemSelectedListener(animalNameSelected);
    }

    Spinner.OnItemSelectedListener animalNameSelected = new Spinner.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            if(position == customSelected && customNameField.getVisibility() != View.VISIBLE) {
                customNameField.setVisibility(View.VISIBLE);
            } else {
                if(customNameField.getVisibility() == View.VISIBLE) {
                    customNameField.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            //Do Nothing
        }
    };

    public void detach() {
        animalRoot.removeViewAt(index);
    }

    public String getPetName() {
        String retVal = null;
        int index = animalSelector.getSelectedItemPosition();

        if(index == customSelected) {
            String foundEntry = customNameField.getText().toString();
            if(!TextUtils.isEmpty(foundEntry)) {
                retVal = foundEntry;
            }
        } else if(index != 0) { //0 is "Choose Pet"
            retVal = animalNames.get(index);
        } //All other cases return null

        return retVal;
    }
}
