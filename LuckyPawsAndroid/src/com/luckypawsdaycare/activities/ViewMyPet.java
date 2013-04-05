/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.database.PetsDAO;

import java.util.Map;

public class ViewMyPet extends Activity {
    Map<String, String> petData;

    int id;
    String name;
    int dogCat;
    int sex;
    String breed;
    String birthdate;
    int size;
    int fixed;

    TextView nameDisplay;
    TextView sexDisplay;
    TextView breedDisplay;
    TextView birthdateDisplay;
    TextView sizeDisplay;
    TextView fixedDisplay;
    TextView fixedLabel;

    Button edit;
    Button delete;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.my_pets_display);

        getPetData();
        findAndWireElements();
        setDisplayValues();
    }

    private void findAndWireElements() {
//todo
    }

    private void setDisplayValues() {
//todo
    }

    private void getPetData() {
        PetsDAO db = new PetsDAO(this);
        //todo
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
            adBuilder.setTitle(name);
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
            if(db.deletePet(id)) {
                Toast warning = Toast.makeText(ViewMyPet.this, R.string.error_pet_delete, Toast.LENGTH_SHORT);
                warning.setGravity(Gravity.CENTER, 0, 0);
                warning.show();
            } else {
                Toast warning = Toast.makeText(ViewMyPet.this, R.string.pet_delete_successful, Toast.LENGTH_SHORT);
                warning.setGravity(Gravity.CENTER, 0, 0);
                warning.show();
                ViewMyPet.this.finish();
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
