/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.luckypawsdaycare.R;

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

        findAndWireElements();
        setDisplayValues();
    }

    private void findAndWireElements() {

    }

    private void setDisplayValues() {

    }

    Button.OnClickListener editPet = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {

        }
    };

    Button.OnClickListener deletePet = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {

        }
    };
}
