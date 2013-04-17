/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.database.DatabaseConstants;
import com.luckypawsdaycare.database.PersonalInfoDAO;
import com.luckypawsdaycare.database.PetsDAO;
import com.luckypawsdaycare.reservations_support.CatSelector;
import com.luckypawsdaycare.reservations_support.DogSelector;
import com.luckypawsdaycare.reservations_support.PriceProcessor;
import com.luckypawsdaycare.support.DateUtilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ReservationsScreen extends Activity {
    Map<String, String> personalInfo;
    List<String> dogs;
    List<String> cats;
    List<DogSelector> dogSelectors;
    List<CatSelector> catSelectors;
    PriceProcessor priceProcessor;

    TextView dropOffDateDisplay;
    TextView pickUpDateDisplay;
    Spinner dropOffTimeDisplay;
    Spinner pickupTimeDisplay;
    EditText ownerFirstName;
    EditText ownerLastName;
    EditText phoneNummber;
    EditText streetAddress;
    EditText city;
    EditText state;
    EditText zip;
    EditText email;
    Spinner numDogs;
    Spinner numCats;
    LinearLayout dogsDetailRoot;
    LinearLayout catsDetailRoot;
    TextView boardingPriceDisplay;
    TextView bathPriceDisplay;
    TextView totalPriceDisplay;
    EditText comments;
    Button submitButton;
    Button cancelButton;

    Calendar dropOffDate;
    Calendar pickUpDate;
    //for drop off and pick up time, use 0 for a morning time frame and 1 for an evening time frame. -1 for Unknown
    int dropOffTime;
    int pickUpTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservations_screen);

        gatherInfo();
        findAndWireElements();
        populateElements();

        priceProcessor = new PriceProcessor(boardingPriceDisplay, bathPriceDisplay, totalPriceDisplay);
    }

    private void gatherInfo() {
        //Personal Information
        PersonalInfoDAO db = new PersonalInfoDAO(this);
        personalInfo = db.getPersonalInfo();

        //Animals
        PetsDAO petsDb = new PetsDAO(this);
        Map<String, Integer> allPets = petsDb.getAllNamesAndAnimal();
        dogs = new ArrayList<String>();
        dogSelectors = new ArrayList<DogSelector>();
        cats = new ArrayList<String>();
        catSelectors = new ArrayList<CatSelector>();
        for(String pet : allPets.keySet()) {
            int dogCat = allPets.get(pet);
            if(dogCat == DatabaseConstants.DOG) {
                dogs.add(pet);
            } else if(dogCat == DatabaseConstants.CAT) {
                cats.add(pet);
            }
        }
    }

    private void findAndWireElements() {
        dropOffDateDisplay = (TextView)findViewById(R.id.drop_off_date_picker);
        dropOffDateDisplay.setOnClickListener(launchDropOffDatePicker);
        pickUpDateDisplay = (TextView)findViewById(R.id.pick_up_date_picker);
        pickUpDateDisplay.setOnClickListener(launchPickUpDatePicker);
        dropOffTimeDisplay = (Spinner)findViewById(R.id.drop_off_time_picker);
        //todo onclick
        pickupTimeDisplay = (Spinner)findViewById(R.id.pick_up_time_picker);
        ownerFirstName = (EditText)findViewById(R.id.first_name_input);
        ownerLastName = (EditText)findViewById(R.id.last_name_input);
        phoneNummber = (EditText)findViewById(R.id.phone_input);
        streetAddress = (EditText)findViewById(R.id.address_street_input);
        city = (EditText)findViewById(R.id.address_city_input);
        state = (EditText)findViewById(R.id.address_state_input);
        zip = (EditText)findViewById(R.id.address_zip_input);
        email = (EditText)findViewById(R.id.email_input);
        numDogs = (Spinner)findViewById(R.id.num_dogs_picker);
        numCats = (Spinner)findViewById(R.id.num_cats_picker);
        dogsDetailRoot = (LinearLayout)findViewById(R.id.dogs_root_layout);
        catsDetailRoot = (LinearLayout)findViewById(R.id.cats_root_layout);
        boardingPriceDisplay = (TextView)findViewById(R.id.boarding_price_value);
        bathPriceDisplay = (TextView)findViewById(R.id.bath_price_value);
        totalPriceDisplay = (TextView)findViewById(R.id.total_price_value);
        comments = (EditText)findViewById(R.id.comments_input);
        submitButton = (Button)findViewById(R.id.submit_button);
        cancelButton = (Button)findViewById(R.id.cancel_button);
    }

    private void populateElements() {
//        dropOffDateDisplay = (TextView)findViewById(R.id.drop_off_date_picker);
//        pickUpDateDisplay = (TextView)findViewById(R.id.pick_up_date_picker);
//        dropOffTimeDisplay = (Spinner)findViewById(R.id.drop_off_time_picker);
//        pickupTimeDisplay = (Spinner)findViewById(R.id.pick_up_time_picker);
//        ownerFirstName = (EditText)findViewById(R.id.first_name_input);
//        ownerLastName = (EditText)findViewById(R.id.last_name_input);
//        phoneNummber = (EditText)findViewById(R.id.phone_input);
//        streetAddress = (EditText)findViewById(R.id.address_street_input);
//        city = (EditText)findViewById(R.id.address_city_input);
//        state = (EditText)findViewById(R.id.address_state_input);
//        zip = (EditText)findViewById(R.id.address_zip_input);
//        email = (EditText)findViewById(R.id.email_input);
//        numDogs = (Spinner)findViewById(R.id.num_dogs_picker);
//        numCats = (Spinner)findViewById(R.id.num_cats_picker);
//        dogsDetailRoot = (LinearLayout)findViewById(R.id.dogs_root_layout);
//        catsDetailRoot = (LinearLayout)findViewById(R.id.cats_root_layout);
//        boardingPriceDisplay = (TextView)findViewById(R.id.boarding_price_value);
//        bathPriceDisplay = (TextView)findViewById(R.id.bath_price_value);
//        totalPriceDisplay = (TextView)findViewById(R.id.total_price_value);
//        comments = (EditText)findViewById(R.id.comments_input);
//        submitButton = (Button)findViewById(R.id.submit_button);
//        cancelButton = (Button)findViewById(R.id.cancel_button);
    }

    View.OnClickListener launchDropOffDatePicker = new View.OnClickListener(){
        public void onClick(View v) {
            int year;
            int month;
            int day;
            if(dropOffDate == null) {
                dropOffDate = Calendar.getInstance();
            }
            year = dropOffDate.get(Calendar.YEAR);
            month = dropOffDate.get(Calendar.MONTH);
            day = dropOffDate.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(ReservationsScreen.this, dropOffDateSet, year, month, day);
            dialog.show();
        }
    };

    DatePickerDialog.OnDateSetListener dropOffDateSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            //Month is 0 - 11 for compatibility with Calendar
            dropOffDate.set(year, month, day);
            dropOffDateDisplay.setText(DateUtilities.appDateFormat().format(dropOffDate.getTime()));
        }
    };

    View.OnClickListener launchPickUpDatePicker = new View.OnClickListener(){
        public void onClick(View v) {
            int year;
            int month;
            int day;
            if(pickUpDate == null) {
                pickUpDate = Calendar.getInstance();
            }
            //First, advance Pickup date to match drop-off date if dropoff date has been set
            if(dropOffDate != null) {
                pickUpDate.setTime(dropOffDate.getTime());
            }
            //Now, because pickup must be after dropoff, we'll advance it one more day
            pickUpDate.add(Calendar.DAY_OF_MONTH, 1);

            year = pickUpDate.get(Calendar.YEAR);
            month = pickUpDate.get(Calendar.MONTH);
            day = pickUpDate.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(ReservationsScreen.this, pickUpDateSet, year, month, day);
            dialog.show();
        }
    };

    DatePickerDialog.OnDateSetListener pickUpDateSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            //Month is 0 - 11 for compatibility with Calendar
            pickUpDate.set(year, month, day);
            pickUpDateDisplay.setText(DateUtilities.appDateFormat().format(pickUpDate.getTime()));
        }
    };

    Button.OnClickListener cancelClick = new Button.OnClickListener() {
        public void onClick(View v) {
            ReservationsScreen.this.finish();
        }
    };

    Button.OnClickListener submitReservation = new Button.OnClickListener() {
        public void onClick(View v) {
            //todo
        }
    };
}
