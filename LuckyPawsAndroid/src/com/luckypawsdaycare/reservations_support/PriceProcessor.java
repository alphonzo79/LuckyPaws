/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.reservations_support;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.luckypawsdaycare.R;

import java.util.Calendar;

public class PriceProcessor {
    View priceDisplayRoot;
    TextView boardingPriceDisplay;
    LinearLayout bathPriceLayout;
    TextView bathPriceDisplay;
    TextView totalPriceDisplay;

    Calendar dropOffDate;
    Calendar pickUpDate;
    //For drop off and pick up times, 0 refers to morning time frame, 1 refers to afternoon time frame, -1 for unknown
    int dropOffTime;
    int pickUpTime;

    int numDogs;
    int numCats;

    int numBaths;

    public PriceProcessor(View priceDisplayRoot) {
        this.priceDisplayRoot = priceDisplayRoot;
        this.boardingPriceDisplay = (TextView)priceDisplayRoot.findViewById(R.id.boarding_price_value);
        this.bathPriceLayout = (LinearLayout)priceDisplayRoot.findViewById(R.id.bath_layout_root);
        this.bathPriceDisplay = (TextView)priceDisplayRoot.findViewById(R.id.bath_price_value);
        this.totalPriceDisplay = (TextView)priceDisplayRoot.findViewById(R.id.total_price_value);

        boardingPriceDisplay.setText("$0");
        bathPriceDisplay.setText("$0");
        bathPriceLayout.setVisibility(View.GONE);
        totalPriceDisplay.setText("$0");
    }

    public void setDropOffDate(Calendar date) {
        dropOffDate = date;
        figureCost();
    }

    public void setDropOffTime(int time) {
        dropOffTime = time;
        figureCost();
    }

    public void setPickUpDate(Calendar date) {
        pickUpDate = date;
        figureCost();
    }

    public void setPickUpTime(int time) {
        pickUpTime = time;
        figureCost();
    }

    public void setNumDogs(int dogs) {
        numDogs = dogs;
        figureCost();
    }

    public void setNumCats(int cats) {
        numCats = cats;
        figureCost();
    }

    public void changeNumBaths(int diff){
        numBaths += diff;
        figureCost();
    }

    private void figureCost() {
        if(dropOffDate != null && pickUpDate != null && (numCats + numDogs) > 0) {
            //todo
        }
    }
}
