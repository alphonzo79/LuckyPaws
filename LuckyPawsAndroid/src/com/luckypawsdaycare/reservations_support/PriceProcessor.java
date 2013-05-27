/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.reservations_support;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.luckypawsdaycare.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PriceProcessor {
    View priceDisplayRoot;
    TextView boardingPriceDisplay;
    LinearLayout halfDayPriceLayout;
    TextView halfDayDisplay;
    LinearLayout bathPriceLayout;
    TextView bathPriceDisplay;
    TextView totalPriceDisplay;
    TextView bathFinePrint;

    Calendar dropOffDate;
    Calendar pickUpDate;
    //For drop off and pick up times, 0 refers to morning time frame, 1 refers to afternoon time frame, -1 for unknown
    int dropOffTime;
    int pickUpTime;

    int numDogs;
    int numCats;

    int numBaths;

    String price;

    public PriceProcessor(View priceDisplayRoot) {
        this.priceDisplayRoot = priceDisplayRoot;
        this.boardingPriceDisplay = (TextView)priceDisplayRoot.findViewById(R.id.boarding_price_value);
        this.halfDayPriceLayout = (LinearLayout)priceDisplayRoot.findViewById(R.id.half_day_price_layout_root);
        this.halfDayDisplay = (TextView)priceDisplayRoot.findViewById(R.id.half_day_price_value);
        this.bathPriceLayout = (LinearLayout)priceDisplayRoot.findViewById(R.id.bath_layout_root);
        this.bathPriceDisplay = (TextView)priceDisplayRoot.findViewById(R.id.bath_price_value);
        this.totalPriceDisplay = (TextView)priceDisplayRoot.findViewById(R.id.total_price_value);
        this.bathFinePrint = (TextView)priceDisplayRoot.findViewById(R.id.bath_price_disclosure);

        boardingPriceDisplay.setText("$0");
        halfDayDisplay.setText("$0");
        halfDayPriceLayout.setVisibility(View.GONE);
        bathPriceDisplay.setText("$0");
        bathPriceLayout.setVisibility(View.GONE);
        totalPriceDisplay.setText("$0");
        bathFinePrint.setVisibility(View.GONE);
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
        Log.d("LuckyPaws", "NumBaths: " + numBaths);
        figureCost();
    }

    private void figureCost() {
        String priceCheckArgs = "";

        if(dropOffDate != null && pickUpDate != null && (numCats + numDogs) > 0) {
            StringBuilder sb = new StringBuilder();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            sb.append("inDate=" + sdf.format(dropOffDate.getTime()));
            sb.append("&outDate=" + sdf.format(pickUpDate.getTime()));
            sb.append("&numDogs=" + numDogs);
            sb.append("&numCats=" + numCats);

            if(dropOffTime >= 0) {
                sb.append("&inTime=");
                switch (dropOffTime) {
                    case 0:
                        sb.append("AM.7-11%20AM");
                        break;
                    case 1:
                        sb.append("PM.3-7%20PM");
                        break;
                    default:
                }
            }

            if(pickUpTime >= 0) {
                sb.append("&outTime=");
                switch (pickUpTime) {
                    case 0:
                        sb.append("AM.7-11%20AM");
                        break;
                    case 1:
                        sb.append("PM.3-7%20PM");
                        break;
                    default:
                }
            }

            priceCheckArgs = sb.toString();
        }

        if(!TextUtils.isEmpty(priceCheckArgs)) {
            PriceCheckAsync priceChecker = new PriceCheckAsync(priceCheckArgs, new PriceCheckAsync.PriceCheckListener() {
                @Override
                public void handleFoundPrice(Map<String, String> prices) {
                    if(prices instanceof Serializable) {
                        Message message = Message.obtain();
                        message.getData().putSerializable("priceMap", (Serializable)prices);
                        priceMapHandler.sendMessage(message);
                    }
                }
            });

            priceChecker.getPrice();
        } else {
            Map<String, String> prices = new HashMap<String, String>();
            updatePricesInUi(prices);
        }
    }

    private String getPriceString(String rawString) {
        String result = "$" + rawString;
        int decimalIndex = result.indexOf('.');
        if(decimalIndex > result.length() - 3) {
            //We only have one digit after the decimal
            result = result + "0";
        }
        return result;
    }

    Handler priceMapHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            Map<String, String> prices = (Map<String, String>)message.getData().getSerializable("priceMap");
            updatePricesInUi(prices);
        }
    };

    private void updatePricesInUi(Map<String, String> prices) {
        String boarding = prices.get("boarding");
        if(!TextUtils.isEmpty(boarding)) {
            boardingPriceDisplay.setText(getPriceString(boarding));
        } else {
            boardingPriceDisplay.setText("$0");
        }

        String half = prices.get("half");
        if(!TextUtils.isEmpty(half)) {
            halfDayPriceLayout.setVisibility(View.VISIBLE);
            halfDayDisplay.setText(getPriceString(half));
        } else {
            halfDayPriceLayout.setVisibility(View.GONE);
            halfDayDisplay.setText(getPriceString("$0"));
        }

        String bath = prices.get("bath");
        if(!TextUtils.isEmpty(bath)) {
            bathPriceLayout.setVisibility(View.VISIBLE);
            bathPriceDisplay.setText(getPriceString(bath));
        } else {
            bathPriceLayout.setVisibility(View.GONE);
            bathPriceDisplay.setText("$0");
        }

        if(numBaths > 0) {
            bathFinePrint.setVisibility(View.VISIBLE);
        } else {
            bathFinePrint.setVisibility(View.GONE);
        }

        String total = prices.get("total");
        if(!TextUtils.isEmpty(total)) {
            totalPriceDisplay.setText(getPriceString(total));
            price = total;
        } else {
            totalPriceDisplay.setText("$0");
            price = "0";
        }
    }

    public String getPrice() {
        return price;
    }
}
