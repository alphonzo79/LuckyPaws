/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.Activity;
import android.os.Bundle;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.database.PetsDAO;

import java.util.Map;

public class ListMyPets extends Activity {
    public final String TAG = "ListMyPets";

    Map<String, Integer> petsData;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        setContentView(R.layout.my_pets_index);

        PetsDAO db = new PetsDAO(this);
        petsData = db.getPetsIndex();

        //TODO continue here. List adapter
    }
}
