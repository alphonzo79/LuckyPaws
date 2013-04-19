/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.reservations_support;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import com.luckypawsdaycare.R;

import java.util.List;

public class CatSelector extends PetSelector {

    public CatSelector(Activity context, LinearLayout rootLayout, List<String> catNames) {
        super(context, rootLayout, catNames);
    }

    @Override
    protected void inflateLayoutFindElements() {
        LayoutInflater inflater = activity.getLayoutInflater();
        animalLayout = (LinearLayout)inflater.inflate(R.layout.reservations_cat_layout, animalRoot, true);

        animalSelector = (Spinner)animalLayout.findViewById(R.id.cat_name_spinner);
        customNameField = (EditText)animalLayout.findViewById(R.id.cat_name_input);
    }
}
