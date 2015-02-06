/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.reservations_support;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.luckypawsdaycare.R;

import java.util.List;

public class CatSelector extends PetSelector {

    public CatSelector(Activity context, LinearLayout rootLayout, List<String> catNames,int index) {
        super(context, rootLayout, catNames, index);
    }

    @Override
    protected void inflateLayoutFindElements() {
        LayoutInflater inflater = activity.getLayoutInflater();
        animalLayout = (LinearLayout)inflater.inflate(R.layout.reservations_cat_layout, animalRoot, false);

        animalSelector = (Spinner)animalLayout.findViewById(R.id.cat_name_spinner);
        customNameField = (EditText)animalLayout.findViewById(R.id.cat_name_input);
        nameLabel = (TextView)animalLayout.findViewById(R.id.cat_name_label);

        String catNum = activity.getString(R.string.cat_num) + (index + 1);
        nameLabel.setText(catNum);

        animalRoot.addView(animalLayout);
    }
}
