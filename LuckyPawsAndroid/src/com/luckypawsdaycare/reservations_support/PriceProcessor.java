/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.reservations_support;

import android.widget.TextView;

import java.util.Calendar;

public class PriceProcessor {
    TextView boardingPriceDisplay;
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

    public PriceProcessor(TextView boardingPriceDisplay, TextView bathPriceDisplay, TextView totalPriceDisplay) {
        this.boardingPriceDisplay = boardingPriceDisplay;
        this.bathPriceDisplay = bathPriceDisplay;
        this.totalPriceDisplay = totalPriceDisplay;

        boardingPriceDisplay.setText("$0");
        bathPriceDisplay.setText("$0");
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
        //todo
    }
}