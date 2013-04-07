/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.database.DatabaseConstants;
import com.luckypawsdaycare.database.PetsDAO;
import com.luckypawsdaycare.database.PetsTableColumnNames;
import com.luckypawsdaycare.support.CustomToast;
import com.luckypawsdaycare.support.DateUtilities;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditMyPets extends Activity {
    private final String TAG = "EditMyPets";

    Map<String, String> petData;
    boolean isEditing;

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

        isEditing = this.getIntent().getBooleanExtra("com.luckypawsdaycare.isEditing", false);
        if(isEditing) {
            id = this.getIntent().getIntExtra("com.luckypawsdaycare.petId", -1);
            getOriginalValues();
        }

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
        sexSpinner.setOnItemSelectedListener(sexSelected);
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

    public void getOriginalValues() {
        PetsDAO db = new PetsDAO(this);
        if(id > -1) {
            petData = db.getPetData(id);
            name = petData.get(PetsTableColumnNames.NAME.getString());
            dogCat = Integer.parseInt(petData.get(PetsTableColumnNames.DOG_CAT.getString()));
            sex = Integer.parseInt(petData.get(PetsTableColumnNames.SEX.getString()));
            breed = petData.get(PetsTableColumnNames.BREED.getString());
            birthdate = petData.get(PetsTableColumnNames.BIRTHDAY.getString());
            size = Integer.parseInt(petData.get(PetsTableColumnNames.SIZE.getString()));
            fixed = Integer.parseInt(petData.get(PetsTableColumnNames.FIXED.getString()));
        }
    }

    private void setDisplayValues() {
        if(isEditing && id > -1) {
            if(!TextUtils.isEmpty(name)) {
                nameInput.setText(name);
            }

            switch (dogCat) {
                case DatabaseConstants.DOG:
                    dogCatSpinner.setSelection(1);
                    break;
                case DatabaseConstants.CAT:
                    dogCatSpinner.setSelection(2);
                    break;
                default:
                    dogCatSpinner.setSelection(0);
            }

            switch (sex) {
                case DatabaseConstants.MALE_INT:
                    sexSpinner.setSelection(1);
                    break;
                case DatabaseConstants.FEMALE_INT:
                    sexSpinner.setSelection(2);
                    break;
                default:
                    sexSpinner.setSelection(0);
            }

            if(!TextUtils.isEmpty(breed)) {
                breedInput.setText(breed);
            }
            if(!TextUtils.isEmpty(birthdate)) {
                birthdateInput.setText(birthdate);
                try {
                    Date date = DateUtilities.appDateFormat().parse(birthdate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH);
                    day = cal.get(Calendar.DAY_OF_MONTH);
                } catch (ParseException e) {
                    Log.d(TAG, "Caught ParseException while parsing the birthdate " + birthdate);
                }
            } else {
                Calendar cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                day = cal.get(Calendar.DAY_OF_MONTH);
                month = cal.get(Calendar.MONTH);
            }

            switch(size) {
                case DatabaseConstants.LESS_THAN_TEN_POUNDS:
                    sizeSpinner.setSelection(1);
                    break;
                case DatabaseConstants.TEN_TO_TWENTY_FIVE_POUNDS:
                    sizeSpinner.setSelection(2);
                    break;
                case DatabaseConstants.TWENTY_FIVE_TO_FIFTY_POUNDS:
                    sizeSpinner.setSelection(3);
                    break;
                case DatabaseConstants.FIFTY_TO_SEVENTY_FIVE_POUNDS:
                    sizeSpinner.setSelection(4);
                    break;
                case DatabaseConstants.SEVENTY_FIVE_TO_ONE_HUNDRED_POUNDS:
                    sizeSpinner.setSelection(5);
                    break;
                case DatabaseConstants.MORE_THAN_ONE_HUNDRED_POUNDS:
                    sizeSpinner.setSelection(6);
                    break;
                default:
                    sizeSpinner.setSelection(0);
            }

            switch (fixed) {
                case DatabaseConstants.TRUE:
                    fixedSpinner.setSelection(1);
                    break;
                case DatabaseConstants.FALSE:
                    fixedSpinner.setSelection(2);
                    break;
                default:
                    fixedSpinner.setSelection(0);
            }
        } else {
            Calendar cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            day = cal.get(Calendar.DAY_OF_MONTH);
            month = cal.get(Calendar.MONTH);
        }
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

            String newName = nameInput.getText().toString();
            if(!TextUtils.isEmpty(newName)) {
                updateArgs.put(PetsTableColumnNames.NAME.getString(), newName);
            } else {
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(EditMyPets.this);
                adBuilder.setTitle(R.string.error);
                adBuilder.setIcon(android.R.drawable.ic_dialog_alert);
                adBuilder.setMessage(R.string.need_name);
                adBuilder.setNegativeButton(R.string.ok, cancelSave);
                AlertDialog alert = adBuilder.create();
                alert.show();
                return;
            }

            int newDogCat = dogCatSpinner.getSelectedItemPosition();
            switch(newDogCat) {
                case 1:
                    updateArgs.put(PetsTableColumnNames.DOG_CAT.getString(), Integer.toString(DatabaseConstants.DOG));
                    break;
                case 2:
                    updateArgs.put(PetsTableColumnNames.DOG_CAT.getString(), Integer.toString(DatabaseConstants.CAT));
                    break;
                default:
                    //do nothing
            }

            int newSex = sexSpinner.getSelectedItemPosition();
            switch(newSex) {
                case 1:
                    updateArgs.put(PetsTableColumnNames.SEX.getString(), Integer.toString(DatabaseConstants.MALE_INT));
                    break;
                case 2:
                    updateArgs.put(PetsTableColumnNames.SEX.getString(), Integer.toString(DatabaseConstants.FEMALE_INT));
                    break;
                default:
                    //do nothing
            }

            String newBreed = breedInput.getText().toString();
            if(!TextUtils.isEmpty(newBreed)) {
                updateArgs.put(PetsTableColumnNames.BREED.getString(), newBreed);
            }

            String newBirthdate = birthdateInput.getText().toString();
            if(!TextUtils.isEmpty(newBirthdate)) {
                updateArgs.put(PetsTableColumnNames.BIRTHDAY.getString(), newBirthdate);
            }

            int newSize = sizeSpinner.getSelectedItemPosition();
            switch(newSize) {
                case 1:
                    updateArgs.put(PetsTableColumnNames.SIZE.getString(),
                            Integer.toString(DatabaseConstants.LESS_THAN_TEN_POUNDS));
                    break;
                case 2:
                    updateArgs.put(PetsTableColumnNames.SIZE.getString(),
                            Integer.toString(DatabaseConstants.TEN_TO_TWENTY_FIVE_POUNDS));
                    break;
                case 3:
                    updateArgs.put(PetsTableColumnNames.SIZE.getString(),
                            Integer.toString(DatabaseConstants.TWENTY_FIVE_TO_FIFTY_POUNDS));
                    break;
                case 4:
                    updateArgs.put(PetsTableColumnNames.SIZE.getString(),
                            Integer.toString(DatabaseConstants.FIFTY_TO_SEVENTY_FIVE_POUNDS));
                    break;
                case 5:
                    updateArgs.put(PetsTableColumnNames.SIZE.getString(),
                            Integer.toString(DatabaseConstants.SEVENTY_FIVE_TO_ONE_HUNDRED_POUNDS));
                    break;
                case 6:
                    updateArgs.put(PetsTableColumnNames.SIZE.getString(),
                            Integer.toString(DatabaseConstants.MORE_THAN_ONE_HUNDRED_POUNDS));
                    break;
                default:
                    //Do Nothing
            }

            int newFixed = fixedSpinner.getSelectedItemPosition();
            switch (newFixed) {
                case 1:
                    updateArgs.put(PetsTableColumnNames.FIXED.getString(), Integer.toString(DatabaseConstants.TRUE));
                    break;
                case 2:
                    updateArgs.put(PetsTableColumnNames.FIXED.getString(), Integer.toString(DatabaseConstants.FALSE));
                    break;
                default:
                    //do nothing
            }

            PetsDAO db = new PetsDAO(EditMyPets.this);
            if(isEditing) {
                if(db.updatePetData(id, updateArgs)) {
                    showSuccessToast();
                    Intent display = new Intent(EditMyPets.this, ViewMyPet.class);
                    display.putExtra("com.luckypawsdaycare.petId", id);
                    startActivity(display);
                    EditMyPets.this.finish();
                } else {
                    showErrorToast();
                }
            } else {
                if(db.addPetData(updateArgs)) {
                    //We don't have an id, so we'll just show the list or go back to settings if this was the first time
                    showSuccessToast();
                    EditMyPets.this.finish();
                } else {
                    showErrorToast();
                }
            }
        }
    };

    DialogInterface.OnClickListener cancelSave = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
        }
    };

    Spinner.OnItemSelectedListener sexSelected = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            int newSex = sexSpinner.getSelectedItemPosition();
            switch(newSex) {
                case 1:
                    fixedLabel.setText(R.string.neutered_label);
                    break;
                case 2:
                    fixedLabel.setText(R.string.spayed_label);
                    break;
                default:
                    fixedLabel.setText(R.string.neutered_spayed_label);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            //Do Nothing
        }
    };

    private void showSuccessToast() {
        CustomToast toast = new CustomToast(this, getString(R.string.update_successful));
        toast.show();
    }

    private void showErrorToast() {
        CustomToast toast = new CustomToast(this, getString(R.string.error_info_save));
        toast.show();
    }
}
