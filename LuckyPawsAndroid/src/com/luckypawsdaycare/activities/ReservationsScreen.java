/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.database.DatabaseConstants;
import com.luckypawsdaycare.database.PersonalInfoDAO;
import com.luckypawsdaycare.database.PersonalInfoTableColumns;
import com.luckypawsdaycare.database.PetsDAO;
import com.luckypawsdaycare.reservations_support.CatSelector;
import com.luckypawsdaycare.reservations_support.DogSelector;
import com.luckypawsdaycare.reservations_support.PetSelector;
import com.luckypawsdaycare.reservations_support.PriceProcessor;
import com.luckypawsdaycare.support.CustomToast;
import com.luckypawsdaycare.support.DateUtilities;

import java.text.SimpleDateFormat;
import java.util.*;

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
    LinearLayout pricingRoot;
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

        priceProcessor = new PriceProcessor(pricingRoot);
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
        dropOffTimeDisplay.setOnItemSelectedListener(dropOffTimeSet);
        pickupTimeDisplay = (Spinner)findViewById(R.id.pick_up_time_picker);
        pickupTimeDisplay.setOnItemSelectedListener(pickUpTimeSet);
        ownerFirstName = (EditText)findViewById(R.id.first_name_input);
        ownerLastName = (EditText)findViewById(R.id.last_name_input);
        phoneNummber = (EditText)findViewById(R.id.phone_input);
        streetAddress = (EditText)findViewById(R.id.address_street_input);
        city = (EditText)findViewById(R.id.address_city_input);
        state = (EditText)findViewById(R.id.address_state_input);
        zip = (EditText)findViewById(R.id.address_zip_input);
        email = (EditText)findViewById(R.id.email_input);
        numDogs = (Spinner)findViewById(R.id.num_dogs_picker);
        numDogs.setOnItemSelectedListener(numDogsSet);
        numCats = (Spinner)findViewById(R.id.num_cats_picker);
        numCats.setOnItemSelectedListener(numCatsSet);
        dogsDetailRoot = (LinearLayout)findViewById(R.id.dogs_root_layout);
        catsDetailRoot = (LinearLayout)findViewById(R.id.cats_root_layout);
        pricingRoot = (LinearLayout)findViewById(R.id.price_display_layout);
        comments = (EditText)findViewById(R.id.comments_input);
        submitButton = (Button)findViewById(R.id.submit_button);
        submitButton.setOnClickListener(submitReservation);
        cancelButton = (Button)findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelClick);
    }

    private void populateElements() {
        String value = personalInfo.get(PersonalInfoTableColumns.FIRST_NAME.getString());
        if(!TextUtils.isEmpty(value)){
            ownerFirstName.setText(value);
        }

        value = personalInfo.get(PersonalInfoTableColumns.LAST_NAME.getString());
        if(!TextUtils.isEmpty(value)){
            ownerLastName.setText(value);
        }

        //Prefer the mobile number, then home if there is not one, then work
        value = personalInfo.get(PersonalInfoTableColumns.PHONE_TWO.getString());
        if(!TextUtils.isEmpty(value)){
            phoneNummber.setText(value);
        } else {
            value = personalInfo.get(PersonalInfoTableColumns.PHONE_ONE.getString());
            if(!TextUtils.isEmpty(value)){
                phoneNummber.setText(value);
            } else {
                value = personalInfo.get(PersonalInfoTableColumns.PHONE_THREE.getString());
                if(!TextUtils.isEmpty(value)){
                    phoneNummber.setText(value);
                }
            }
        }

        value = personalInfo.get(PersonalInfoTableColumns.STREET.getString());
        if(!TextUtils.isEmpty(value)){
            streetAddress.setText(value);
        }

        value = personalInfo.get(PersonalInfoTableColumns.CITY.getString());
        if(!TextUtils.isEmpty(value)){
            city.setText(value);
        }

        value = personalInfo.get(PersonalInfoTableColumns.STATE.getString());
        if(!TextUtils.isEmpty(value)){
            state.setText(value);
        }

        value = personalInfo.get(PersonalInfoTableColumns.ZIP.getString());
        if(!TextUtils.isEmpty(value)){
            zip.setText(value);
        }

        value = personalInfo.get(PersonalInfoTableColumns.EMAIL.getString());
        if(!TextUtils.isEmpty(value)){
            email.setText(value);
        }
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

            //todo holidays, Thanksgiving and Christmas
            int dow = dropOffDate.get(Calendar.DAY_OF_WEEK);
            if(dow == Calendar.SATURDAY || dow == Calendar.SUNDAY) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReservationsScreen.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.time_frames_weekend));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dropOffTimeDisplay.setAdapter(adapter);
                dropOffTimeDisplay.setSelection(0);
            } else {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReservationsScreen.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.time_frames));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dropOffTimeDisplay.setAdapter(adapter);
                dropOffTimeDisplay.setSelection(0);
            }

            priceProcessor.setDropOffDate(dropOffDate);
        }
    };

    View.OnClickListener launchPickUpDatePicker = new View.OnClickListener(){
        public void onClick(View v) {
            int year;
            int month;
            int day;
            if(pickUpDate == null) {
                pickUpDate = Calendar.getInstance();
                //Now, because pickup must be after dropoff, we'll advance it one more day
                pickUpDate.add(Calendar.DAY_OF_MONTH, 1);
            }
            //First, advance Pickup date to match drop-off date if dropoff date has been set
            if(dropOffDate != null) {
                pickUpDate.setTime(dropOffDate.getTime());
                //Now, because pickup must be after dropoff, we'll advance it one more day
                pickUpDate.add(Calendar.DAY_OF_MONTH, 1);
            }

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

            //todo holidays, Thanksgiving and Christmas
            int dow = pickUpDate.get(Calendar.DAY_OF_WEEK);
            if(dow == Calendar.SATURDAY || dow == Calendar.SUNDAY) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReservationsScreen.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.time_frames_weekend));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                pickupTimeDisplay.setAdapter(adapter);
                pickupTimeDisplay.setSelection(0);
            } else {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReservationsScreen.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.time_frames));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                pickupTimeDisplay.setAdapter(adapter);
                pickupTimeDisplay.setSelection(0);
            }

            priceProcessor.setPickUpDate(pickUpDate);
        }
    };

    Spinner.OnItemSelectedListener dropOffTimeSet = new Spinner.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            switch(position) {
                case 1:
                    priceProcessor.setDropOffTime(0);
                    break;
                case 2:
                    priceProcessor.setDropOffTime(1);
                    break;
                default:
                    priceProcessor.setDropOffTime(-1);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            //Do Nothing
        }
    };

    Spinner.OnItemSelectedListener pickUpTimeSet = new Spinner.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            switch(position) {
                case 1:
                    priceProcessor.setPickUpTime(0);
                    break;
                case 2:
                    priceProcessor.setPickUpTime(1);
                    break;
                default:
                    priceProcessor.setPickUpTime(-1);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            //Do Nothing
        }
    };

    Spinner.OnItemSelectedListener numDogsSet = new Spinner.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            int count = dogSelectors.size();
            int diff = count - position;
            //possibility samples: count == 2; position == 2; diff == 0 -- Do nothing
                //count == 1; position == 4; diff == -3 -- We need to add three selectors, add until diff == 0
                //count == 3; position == 1; diff == 2 -- We needto remove two selectors, remove until diff == 0
            //We can avoid an if evaluation and just go for two while loops since our goal is diff == 0
            while(diff < 0) {
                //add selectors and increment
                int index = dogSelectors.size();
                dogSelectors.add(new DogSelector(ReservationsScreen.this, dogsDetailRoot, dogs, index, priceProcessor));
                diff++;
            }
            while(diff > 0) {
                //remove selectors and decrement
                int last = dogSelectors.size() - 1;
                dogSelectors.get(last).detach();
                dogSelectors.remove(last);
                diff--;
            }

            priceProcessor.setNumDogs(dogSelectors.size());
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            //Do Nothing
        }
    };

    Spinner.OnItemSelectedListener numCatsSet = new Spinner.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            int count = catSelectors.size();
            int diff = count - position;
            //See reasoning in numDogsSet
            while(diff < 0) {
                //add selectors and increment
                int index = catSelectors.size();
                catSelectors.add(new CatSelector(ReservationsScreen.this, catsDetailRoot, cats, index));
                diff++;
            }
            while(diff > 0) {
                //remove selectors and decrement
                int last = catSelectors.size() - 1;
                catSelectors.get(last).detach();
                catSelectors.remove(last);
                diff--;
            }

            priceProcessor.setNumCats(catSelectors.size());
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            //Do Nothing
        }
    };

    Button.OnClickListener cancelClick = new Button.OnClickListener() {
        public void onClick(View v) {
            ReservationsScreen.this.finish();
        }
    };

    Button.OnClickListener submitReservation = new Button.OnClickListener() {
        public void onClick(View v) {
            if(validateInputs()) {
                Log.d("LuckyPaws", "Passed validation");
                try {
                    Map<String, String> formValues = new HashMap<String, String>();
                    SimpleDateFormat sdf = DateUtilities.reservationRequestDateFormat();
                    formValues.put("inDate", sdf.format(dropOffDate.getTime()));
                    formValues.put("outDate", sdf.format(pickUpDate.getTime()));

                    String inTime = "";
                    int dow = dropOffDate.get(Calendar.DAY_OF_WEEK);
                    if(dow == Calendar.SATURDAY || dow == Calendar.SUNDAY) {
                        switch(dropOffTime) {
                            case 0:
                                inTime = getResources().getString(R.string.am_weekday_form_data);
                                break;
                            case 1:
                                inTime = getResources().getString(R.string.pm_weekday_form_data);
                                break;
                            default:
                                break;
                        }
                    } else {
                        switch(dropOffTime) {
                            case 0:
                                inTime = getResources().getString(R.string.am_weekend_form_data);
                                break;
                            case 1:
                                inTime = getResources().getString(R.string.pm_weekend_form_data);
                                break;
                            default:
                                break;
                        }
                    }
                    formValues.put("inTime", inTime);

                    String outTime = "";
                    dow = pickUpDate.get(Calendar.DAY_OF_WEEK);
                    if(dow == Calendar.SATURDAY || dow == Calendar.SUNDAY) {
                        switch(pickUpTime) {
                            case 0:
                                outTime = getResources().getString(R.string.am_weekday_form_data);
                                break;
                            case 1:
                                outTime = getResources().getString(R.string.pm_weekday_form_data);
                                break;
                            default:
                                break;
                        }
                    } else {
                        switch(pickUpTime) {
                            case 0:
                                outTime = getResources().getString(R.string.am_weekend_form_data);
                                break;
                            case 1:
                                outTime = getResources().getString(R.string.pm_weekend_form_data);
                                break;
                            default:
                                break;
                        }
                    }
                    formValues.put("outTime", outTime);

                    formValues.put("firstName", ownerFirstName.getText().toString());
                    formValues.put("lastName", ownerLastName.getText().toString());

                    String phone = phoneNummber.getText().toString().replace("-", "");
                    formValues.put("phone1a", phone.substring(0, 3));
                    formValues.put("phone1b", phone.substring(3, 6));
                    formValues.put("phone1c", phone.substring(6));

                    formValues.put("email", email.getText().toString());

                    //todo
//                    <input type="hidden" name="firstName" value="Karen" />
//                    <input type="hidden" name="lastName" value="Davis" />
//                    <input type="hidden" name="phone1a" value="919" />
//                    <input type="hidden" name="phone1b" value="123" />
//                    <input type="hidden" name="phone1c" value="4567" />
//                    <input type="hidden" name="email" value="karen@myluckypaws.com" />
//                    <input type="hidden" name="address" value="123 Main Stree" />
//                    <input type="hidden" name="city" value="Selma" />
//                    <input type="hidden" name="state" value="NC" />
//                    <input type="hidden" name="zip" value="27576" />
//                    <input type="hidden" name="numDogs" value="2" />
//                    <input type="hidden" name="numCats" value="1" />
//                    <input type="hidden" name="play" value="1" />
//                    <input type="hidden" name="dogName[1]" value="Bugg" />
//                    <input type="hidden" name="dogName[2]" value="Lugzz" />
//                    <input type="hidden" name="dogBreed[1]" value="Dalmation" />
//                    <input type="hidden" name="dogBreed[2]" value="Pit Bull" />
//                    <input type="hidden" name="bath[1]" value="Yes" />
//                    <input type="hidden" name="bath[2]" value="No" />
//                    <input type="hidden" name="catName[1]" value="Kitty" />
//                    <input type='hidden' name='price' value='475.7' />
//                    <input type="text" name="notes" value="" size=40 />
                } catch (Exception e) {
                    e.printStackTrace();
                    CustomToast toast = new CustomToast(ReservationsScreen.this, getString(R.string.error_info_save));
                    toast.show();
                }
            }
        }
    };

    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder("");

        //Drop off date, pick up date, drop off time and pick up time need to be populated
        if(dropOffDate == null || dropOffDate.get(Calendar.DAY_OF_MONTH) < Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
            appendErrorMessage(errorMessage, "• You must select a valid drop off date.");
        }
        if(dropOffTime < 0) {
            appendErrorMessage(errorMessage, "• You must select a valid drop off time frame.");
        }
        if(pickUpDate == null || pickUpDate.before(Calendar.getInstance())) {
            appendErrorMessage(errorMessage, "• You must select a valid pick up date.");
        }
        if(pickUpTime < 0) {
            appendErrorMessage(errorMessage, "• You must select a valid pick up time frame.");
        }

        //Check dates -- Drop off must be before pickup, Can't be the same date
        if(dropOffDate != null && pickUpDate != null) {
            if(pickUpDate.before(dropOffDate) || dropOffDate.equals(pickUpDate) || dropOffDate.get(Calendar.DAY_OF_MONTH) == pickUpDate.get(Calendar.DAY_OF_MONTH)) {
                appendErrorMessage(errorMessage, "• Your pick up date must be after your drop off date.");
            }
        }

        //Personal Info must be populated
        if(TextUtils.isEmpty(ownerFirstName.getText().toString()) || TextUtils.isEmpty(ownerLastName.getText().toString())){
            appendErrorMessage(errorMessage, "• You must enter your first and last name.");
        }
        if(!TextUtils.isEmpty(phoneNummber.getText().toString())) {
            String phone = phoneNummber.getText().toString().replace("-", "");
            if(phone.length() < 10) {
                appendErrorMessage(errorMessage, "• You must provide your full 10-digit contact phone number.");
            }
        } else {
            appendErrorMessage(errorMessage, "• You must enter your phone number.");
        }
        if(TextUtils.isEmpty(streetAddress.getText().toString()) ||
                TextUtils.isEmpty(city.getText().toString()) ||
                TextUtils.isEmpty(state.getText().toString()) ||
                TextUtils.isEmpty(zip.getText().toString())) {
            appendErrorMessage(errorMessage, "• You must provide your full address.");
        }
        if(TextUtils.isEmpty(email.getText().toString()) || !email.getText().toString().contains("@")) {
            appendErrorMessage(errorMessage, "• You must provide a valid e-mail address.");
        }

        //We need to have animals to board, and we need the info for each animal
        int numDogsToBoard = numDogs.getSelectedItemPosition();
        int numCatsToBoard = numCats.getSelectedItemPosition();
        if(numDogsToBoard + numCatsToBoard < 1) {
            appendErrorMessage(errorMessage, "• You must provide at least one dog or cat to board.");
        }
        boolean allAnimalsInput = true;
        if(numDogsToBoard > 0) {
            for(PetSelector pet : dogSelectors) {
                if(TextUtils.isEmpty(pet.getPetName())) {
                    allAnimalsInput = false;
                }
            }
        }
        if(numCatsToBoard > 0) {
            for(PetSelector pet : catSelectors) {
                if(TextUtils.isEmpty(pet.getPetName())) {
                    allAnimalsInput = false;
                }
            }
        }
        if(!allAnimalsInput) {
            appendErrorMessage(errorMessage, "• You must provide information about each of the animals scheduled.");
        }

        String errorMessageString = errorMessage.toString();
        if(TextUtils.isEmpty(errorMessageString)) {
            return true;
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.error);
            dialog.setIcon(android.R.drawable.ic_dialog_alert);
            dialog.setMessage(errorMessageString);
            dialog.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialog.show();
            return false;
        }
    }

    private void appendErrorMessage(StringBuilder builder, String messagePortion) {
        if(builder.length() > 1) {
            builder.append("\n");
        }
        builder.append(messagePortion);
    }
}
