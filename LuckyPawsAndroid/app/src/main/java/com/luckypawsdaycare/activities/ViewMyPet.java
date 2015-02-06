/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.database.DatabaseConstants;
import com.luckypawsdaycare.database.PetsDAO;
import com.luckypawsdaycare.database.PetsTableColumnNames;
import com.luckypawsdaycare.support.CustomToast;

import java.util.Map;

public class ViewMyPet extends Activity {
    Map<String, String> petData;

    int id;

    TextView nameDisplay;
    TextView sexDisplay;
    TextView breedDisplay;
    TextView birthdateDisplay;
    TextView dogCatDisplay;
    TextView sizeDisplay;
    TextView fixedDisplay;
    TextView fixedLabel;

    Button edit;
    Button delete;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.my_pets_display);

        id = this.getIntent().getIntExtra("com.luckypawsdaycare.petId", -1);
    }

    public void onResume() {
        super.onResume();

        PetsDAO db = new PetsDAO(this);
        petData = db.getPetData(id);

        findAndWireElements();
        setDisplayValues();
    }

    private void findAndWireElements() {
        nameDisplay = (TextView)findViewById(R.id.name_value);
        sexDisplay = (TextView)findViewById(R.id.sex_value);
        breedDisplay = (TextView)findViewById(R.id.breed_value);
        birthdateDisplay = (TextView)findViewById(R.id.dob_value);
        dogCatDisplay = (TextView)findViewById(R.id.dog_cat_value);
        sizeDisplay = (TextView)findViewById(R.id.size_value);
        fixedDisplay = (TextView)findViewById(R.id.fixed_value);
        fixedLabel = (TextView)findViewById(R.id.fixed_label);

        edit = (Button)findViewById(R.id.edit_button);
        edit.setOnClickListener(editPet);
        delete = (Button)findViewById(R.id.delete_button);
        delete.setOnClickListener(deletePet);
    }

    private void setDisplayValues() {
        String name = petData.get(PetsTableColumnNames.NAME.getString());
        if(!TextUtils.isEmpty(name)) {
            nameDisplay.setText(name);
        } else {
            findViewById(R.id.pet_name_labels).setVisibility(View.GONE);
            nameDisplay.setVisibility(View.GONE);
        }

        String sex = petData.get(PetsTableColumnNames.SEX.getString());
        if(!TextUtils.isEmpty(sex)) {
            int sexInt = Integer.parseInt(sex);
            if(sexInt == DatabaseConstants.MALE_INT) {
                sexDisplay.setText(R.string.male);
                fixedLabel.setText(R.string.neutered_label);
            } else if(sexInt == DatabaseConstants.FEMALE_INT) {
                sexDisplay.setText(R.string.female);
                fixedLabel.setText(R.string.spayed_label);
            } else{
                findViewById(R.id.fixed_container).setVisibility(View.GONE);
                ((TextView)findViewById(R.id.sex_label)).setText(R.string.neutered_spayed_label);
                sex = null; //so the fixed logic can get an easy evaluation
            }
        } else {
            findViewById(R.id.fixed_container).setVisibility(View.GONE); //prep for shifting the values
            ((TextView)findViewById(R.id.sex_label)).setText(R.string.neutered_spayed_label);
        }

        String fixed = petData.get(PetsTableColumnNames.FIXED.getString());
        if(!TextUtils.isEmpty(fixed)) {
            int fixedInt = Integer.parseInt(fixed);
            if(!TextUtils.isEmpty(sex)) { //normal condition
                if(fixedInt == DatabaseConstants.FIXED) {
                    fixedDisplay.setText(R.string.yes);
                } else {
                    fixedDisplay.setText(R.string.no);
                }
            } else { //We need to shift
                if(fixedInt == DatabaseConstants.FIXED) {
                    sexDisplay.setText(R.string.yes);
                } else {
                    sexDisplay.setText(R.string.no);
                }
            }
        } else {
            if(!TextUtils.isEmpty(sex)) { //normal condition, only hide fixed
                findViewById(R.id.fixed_container).setVisibility(View.GONE);
            } else { //sex display was left visible for us, hide that one instead
                findViewById(R.id.sex_container).setVisibility(View.GONE);
            }
        }

        String breed = petData.get(PetsTableColumnNames.BREED.getString());
        if(!TextUtils.isEmpty(breed)) {
            breedDisplay.setText(breed);
        } else {
            findViewById(R.id.breed_labels).setVisibility(View.GONE);
            breedDisplay.setVisibility(View.GONE);
        }

        String birthdate = petData.get(PetsTableColumnNames.BIRTHDAY.getString());
        if(!TextUtils.isEmpty(birthdate)) {
            birthdateDisplay.setText(birthdate);
        } else {
            findViewById(R.id.dob_labels).setVisibility(View.GONE);
            birthdateDisplay.setVisibility(View.GONE);
        }

        String dogCat = petData.get(PetsTableColumnNames.DOG_CAT.getString());
        if(!TextUtils.isEmpty(dogCat)) {
            int dogCatInt = Integer.parseInt(dogCat);
            if(dogCatInt == DatabaseConstants.DOG) {
                dogCatDisplay.setText(R.string.dog);
            } else if(dogCatInt == DatabaseConstants.CAT) {
                dogCatDisplay.setText(R.string.cat);
            } else {
                //hide size and prep for shifting values
                findViewById(R.id.size_container).setVisibility(View.GONE);
                ((TextView)findViewById(R.id.dog_cat_label)).setText(R.string.size_label);
                dogCat = null;
            }
        } else {
            //hide size and prep for shifting values
            findViewById(R.id.size_container).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.dog_cat_label)).setText(R.string.size_label);
        }

        String size = petData.get(PetsTableColumnNames.SIZE.getString());
        String sizeString = null;
        if(!TextUtils.isEmpty(size)) {
            int sizeInt = Integer.parseInt(size);
            if(sizeInt == DatabaseConstants.LESS_THAN_TEN_POUNDS) {
                sizeString = getResources().getString(R.string.less_than_ten_pounds);
            } else if(sizeInt == DatabaseConstants.TEN_TO_TWENTY_FIVE_POUNDS) {
                sizeString = getResources().getString(R.string.ten_to_twenty_five_pounds);
            } else if(sizeInt == DatabaseConstants.TWENTY_FIVE_TO_FIFTY_POUNDS) {
                sizeString = getResources().getString(R.string.twenty_five_to_fifty_pounds);
            } else if(sizeInt == DatabaseConstants.FIFTY_TO_SEVENTY_FIVE_POUNDS) {
                sizeString = getResources().getString(R.string.fifty_to_seventy_five_pounds);
            } else if(sizeInt == DatabaseConstants.SEVENTY_FIVE_TO_ONE_HUNDRED_POUNDS) {
                sizeString = getResources().getString(R.string.seventy_five_to_one_hundred_pounds);
            } else if(sizeInt == DatabaseConstants.MORE_THAN_ONE_HUNDRED_POUNDS) {
                sizeString = getResources().getString(R.string.more_than_one_hundred_pounds);
            }
        }

        if(!TextUtils.isEmpty(sizeString)) {
            //Now, do we have something valid, and where do we put it?
            if(!TextUtils.isEmpty(dogCat)) {
                sizeDisplay.setText(sizeString);
            } else {
                dogCatDisplay.setText(sizeString);
            }
        } else {
            //Either hide this one, or if dog/cat was empty we already hid it and left that display for this, so hide dog/cat instead
            if(!TextUtils.isEmpty(dogCat)) {
                findViewById(R.id.size_container).setVisibility(View.GONE);
            } else {
                findViewById(R.id.dog_cat_container).setVisibility(View.GONE);
            }
        }
    }

    Button.OnClickListener editPet = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent editPetIntent = new Intent(ViewMyPet.this, EditMyPets.class);
            editPetIntent.putExtra("com.luckypawsdaycare.isEditing", true);
            editPetIntent.putExtra("com.luckypawsdaycare.petId", id);
            startActivity(editPetIntent);
        }
    };

    Button.OnClickListener deletePet = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            AlertDialog.Builder adBuilder = new AlertDialog.Builder(ViewMyPet.this);
            adBuilder.setTitle(petData.get(PetsTableColumnNames.NAME.getString()));
            adBuilder.setIcon(android.R.drawable.ic_dialog_alert);
            adBuilder.setMessage(R.string.confirm_delete_message);
            adBuilder.setPositiveButton(R.string.yes, confirmDelete);
            adBuilder.setNegativeButton(R.string.no, cancelDelete);
            AlertDialog alert = adBuilder.create();
            alert.show();
        }
    };

    DialogInterface.OnClickListener confirmDelete = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            PetsDAO db = new PetsDAO(ViewMyPet.this);
            if(db.deletePet(ViewMyPet.this.id)) {
                CustomToast toast = new CustomToast(ViewMyPet.this, getString(R.string.pet_delete_successful));
                toast.show();
                ViewMyPet.this.finish();
            } else {
                CustomToast toast = new CustomToast(ViewMyPet.this, getString(R.string.error_pet_delete));
                toast.show();
            }
        }
    };

    DialogInterface.OnClickListener cancelDelete = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
        }
    };
}
