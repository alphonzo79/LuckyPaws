/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.reservations_support;

import android.widget.TextView;

import java.util.Date;

public class PriceProcessor {
    TextView boardingPriceDisplay;
    TextView bathPriceDisplay;
    TextView totalPriceDisplay;

    Date dropOffDate;
    Date pickUpDate;
    //For drop off and pick up times, 0 refers to morning time frame, 1 refers to afternoon time frame, -1 for unknown
    int dropOffTime;
    int pickUpTime;

    public PriceProcessor(TextView boardingPriceDisplay, TextView bathPriceDisplay, TextView totalPriceDisplay) {
        this.boardingPriceDisplay = boardingPriceDisplay;
        this.bathPriceDisplay = bathPriceDisplay;
        this.totalPriceDisplay = totalPriceDisplay;

        boardingPriceDisplay.setText("$0");
        bathPriceDisplay.setText("$0");
        totalPriceDisplay.setText("$0");
    }

    public void setDropOffDate(Date date) {
        dropOffDate = date;
        figureCost();
    }

    public void setDropOffTime(int time) {
        dropOffTime = time;
        figureCost();
    }

    public void setPickUpDate(Date date) {
        pickUpDate = date;
        figureCost();
    }

    public void setPickUpTime(int time) {
        pickUpTime = time;
        figureCost();
    }

    private void figureCost() {
        //todo
    }
}
