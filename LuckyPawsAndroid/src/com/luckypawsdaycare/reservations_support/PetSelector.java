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
    PetSelectorListener listener;
    ViewGroup animalRoot;
    List<String> animalNames;
    int index;

    String currentSelected;
    ArrayAdapter<String> adapter;

    int customSelected;

    LinearLayout animalLayout;
    TextView nameLabel;
    Spinner animalSelector;
    EditText customNameField;

    public PetSelector(Activity context, LinearLayout rootLayout, List<String> petNames, int index) {
        activity = context;
        if(activity instanceof PetSelectorListener) {
            listener = (PetSelectorListener)activity;
        } else {
            throw new IllegalStateException("The activity that hosts this pet selector must implement the PestSelectorListener Interface");
        }

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
        adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, animalNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        animalSelector.setAdapter(adapter);
        animalSelector.setSelection(0);

        animalSelector.setOnItemSelectedListener(animalNameSelected);
    }

    public void removePetName(String petName) {
        if(!petName.equals(currentSelected)) { //This way we don't remove it from the one that triggered the removal
            if(animalNames.remove(petName)){
                adapter.notifyDataSetChanged();
                //Reset the selected index to take into account the removed name
                int index = adapter.getPosition(currentSelected);
                animalSelector.setSelection(index);
                animalSelector.invalidate();
            }
        }

        customSelected = animalNames.size() - 1;
    }

    public void addPetName(String petName, int index) {
        if(!animalNames.contains(petName)) {
            animalNames.add(index + 1, petName);
            //Reset the selected index to take into account the removed name
            int currIndex = adapter.getPosition(currentSelected);
            animalSelector.setSelection(currIndex);
            animalSelector.invalidate();
        }
        customSelected = animalNames.size() - 1;
    }

    Spinner.OnItemSelectedListener animalNameSelected = new Spinner.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            if(!TextUtils.isEmpty(currentSelected) && !currentSelected.equals("custom") && !currentSelected.equals("choose")) {
                listener.addPetToSelectors(currentSelected);
            }

            int selectedIndex = animalSelector.getSelectedItemPosition();
            currentSelected = animalNames.get(selectedIndex);
            if(position == customSelected) {
                currentSelected = "custom";
                if(customNameField.getVisibility() != View.VISIBLE){
                    customNameField.setVisibility(View.VISIBLE);
                }
            } else {
                if(customNameField.getVisibility() == View.VISIBLE) {
                    customNameField.setVisibility(View.GONE);
                }

                if(position == 0) {
                    currentSelected = "choose";
                }
            }

            if(!currentSelected.equals("custom") && !currentSelected.equals("choose")) {
                listener.removePetFromSelectors(currentSelected);
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

    public interface PetSelectorListener {
        public void removePetFromSelectors(String petName);
        public void addPetToSelectors(String petName);
    }
}
