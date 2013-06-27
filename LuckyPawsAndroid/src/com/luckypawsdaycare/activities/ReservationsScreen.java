/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.database.DatabaseConstants;
import com.luckypawsdaycare.database.PersonalInfoDAO;
import com.luckypawsdaycare.database.PersonalInfoTableColumns;
import com.luckypawsdaycare.database.PetsDAO;
import com.luckypawsdaycare.reservations_support.*;
import com.luckypawsdaycare.support.CustomToast;
import com.luckypawsdaycare.support.DateUtilities;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.*;

public class ReservationsScreen extends Activity implements PetSelector.PetSelectorListener {
    Map<String, String> personalInfo;
    List<String> dogs;
    List<String> cats;
    List<DogSelector> dogSelectors;
    List<CatSelector> catSelectors;
    PriceProcessor priceProcessor;

    DateTimeHelper inDateChecker;
    DateTimeHelper outDateChecker;

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

        inDateChecker = new DateTimeHelper(new DateTimeHelper.DateTimeCheckListener() {
            @Override
            public void updateStrings(List<String> displayStrings) {
                ArrayList<String> availableTimes = new ArrayList<String>();
                availableTimes.add(getString(R.string.time_hint));
                availableTimes.addAll(displayStrings);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReservationsScreen.this, android.R.layout.simple_spinner_item, availableTimes);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dropOffTimeDisplay.setAdapter(adapter);
                dropOffTimeDisplay.setSelection(0);
            }

            @Override
            public void closedDate() {
                CustomToast toast = new CustomToast(ReservationsScreen.this, String.format("%s %s. \n\n%s", getString(R.string.closed_that_day), getString(R.string.drop_off_string), getString(R.string.please_choose_another_date)));
                toast.show();
                dropOffDateDisplay.setText("");
                dropOffTimeDisplay.setSelection(0);
            }
        }, DateTimeHelper.InDateOutDate.IN);

        outDateChecker = new DateTimeHelper(new DateTimeHelper.DateTimeCheckListener() {
            @Override
            public void updateStrings(List<String> displayStrings) {
                ArrayList<String> availableTimes = new ArrayList<String>();
                availableTimes.add(getString(R.string.time_hint));
                availableTimes.addAll(displayStrings);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReservationsScreen.this, android.R.layout.simple_spinner_item, availableTimes);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                pickupTimeDisplay.setAdapter(adapter);
                pickupTimeDisplay.setSelection(0);
            }

            @Override
            public void closedDate() {
                CustomToast toast = new CustomToast(ReservationsScreen.this, String.format("%s %s. \n\n%s", getString(R.string.closed_that_day), getString(R.string.pick_up_string), getString(R.string.please_choose_another_date)));
                toast.show();
                pickUpDateDisplay.setText("");
                pickupTimeDisplay.setSelection(0);
            }
        }, DateTimeHelper.InDateOutDate.OUT);
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
        pricingRoot = (LinearLayout)findViewById(R.id.reservations_root);
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

    public void removePetFromSelectors(String petName) {
        if(cats.contains(petName)) {
            for(PetSelector catSelector : catSelectors) {
                catSelector.removePetName(petName);
            }
        } else if (dogs.contains(petName)) {
            for(PetSelector dogSelector : dogSelectors) {
                dogSelector.removePetName(petName);
            }
        }
    }

    public void addPetToSelectors(String petName) {
        if(cats.contains(petName)) {
            int index = cats.indexOf(petName);
            for(PetSelector catSelector : catSelectors) {
                catSelector.addPetName(petName, index);
            }
        } else if (dogs.contains(petName)) {
            int index = dogs.indexOf(petName);
            for(PetSelector dogSelector : dogSelectors) {
                dogSelector.addPetName(petName, index);
            }
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

            inDateChecker.checkAvailableTimes(dropOffDate);

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

            outDateChecker.checkAvailableTimes(pickUpDate);

            priceProcessor.setPickUpDate(pickUpDate);
        }
    };

    Spinner.OnItemSelectedListener dropOffTimeSet = new Spinner.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            String selected = (String)adapterView.getAdapter().getItem(position);
            Log.d("LuckyPaws", "Selected time: " + selected);
            priceProcessor.setDropOffTime(inDateChecker.getParamString(selected));
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            //Do Nothing
        }
    };

    Spinner.OnItemSelectedListener pickUpTimeSet = new Spinner.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            String selected = (String)adapterView.getAdapter().getItem(position);
            Log.d("LuckyPaws", "Selected time: " + selected);
            priceProcessor.setPickUpTime(outDateChecker.getParamString(selected));
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

                //Don't include pets that are already selected in other lists
                List<String> tempList = new ArrayList<String>(dogs);
                for(PetSelector selector : dogSelectors) {
                    tempList.remove(selector.getPetName());
                }
                dogSelectors.add(new DogSelector(ReservationsScreen.this, dogsDetailRoot, tempList, index, priceProcessor));
                diff++;
            }
            while(diff > 0) {
                //remove selectors and decrement
                int last = dogSelectors.size() - 1;
                String name = dogSelectors.get(last).getPetName();
                dogSelectors.get(last).detach();
                dogSelectors.remove(last);

                //Add the name back into the other selectors
                if(dogs.contains(name)) { //This way we don't add "choose" or "Custom"
                    addPetToSelectors(name);
                }
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

                //Don't include pets that are already selected in other lists
                List<String> tempList = new ArrayList<String>(cats);
                for(PetSelector selector : catSelectors) {
                    tempList.remove(selector.getPetName());
                }
                catSelectors.add(new CatSelector(ReservationsScreen.this, catsDetailRoot, tempList, index));
                diff++;
            }
            while(diff > 0) {
                //remove selectors and decrement
                int last = catSelectors.size() - 1;
                String name = catSelectors.get(last).getPetName();
                catSelectors.get(last).detach();
                catSelectors.remove(last);

                //Add the name back into the other selectors
                if(cats.contains(name)) { //This way we don't add "choose" or "Custom"
                    addPetToSelectors(name);
                }
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
                    List<NameValuePair> formValues = new ArrayList<NameValuePair>();
                    SimpleDateFormat sdf = DateUtilities.reservationRequestDateFormat();
                    formValues.add(new BasicNameValuePair("inDate", sdf.format(dropOffDate.getTime())));
                    formValues.add(new BasicNameValuePair("outDate", sdf.format(pickUpDate.getTime())));

                    String inTime = inDateChecker.getParamString(dropOffTimeDisplay.getSelectedItem().toString());
                    formValues.add(new BasicNameValuePair("inTime", inTime));

                    String outTime = outDateChecker.getParamString(pickupTimeDisplay.getSelectedItem().toString());
                    formValues.add(new BasicNameValuePair("outTime", outTime));

                    formValues.add(new BasicNameValuePair("firstName", ownerFirstName.getText().toString()));
                    formValues.add(new BasicNameValuePair("lastName", ownerLastName.getText().toString()));

                    String phone = phoneNummber.getText().toString().replace("-", "");
                    formValues.add(new BasicNameValuePair("phone1a", phone.substring(0, 3)));
                    formValues.add(new BasicNameValuePair("phone1b", phone.substring(3, 6)));
                    formValues.add(new BasicNameValuePair("phone1c", phone.substring(6)));

                    formValues.add(new BasicNameValuePair("email", email.getText().toString()));

                    formValues.add(new BasicNameValuePair("address", streetAddress.getText().toString()));
                    formValues.add(new BasicNameValuePair("city", city.getText().toString()));
                    formValues.add(new BasicNameValuePair("state", state.getText().toString()));
                    formValues.add(new BasicNameValuePair("zip", zip.getText().toString()));

                    formValues.add(new BasicNameValuePair("numDogs", Integer.toString(dogSelectors.size())));
                    formValues.add(new BasicNameValuePair("numCats", Integer.toString(catSelectors.size())));

                    formValues.add(new BasicNameValuePair("play", "1")); //Hard coded -- every reservation has this

                    for(int i = 0; i < dogSelectors.size(); i++) {
                        String name = dogSelectors.get(i).getPetName();
                        formValues.add(new BasicNameValuePair("dogName[" + (i + 1) + "]", name));

                        PetsDAO db = new PetsDAO(ReservationsScreen.this);
                        String breed = db.getPetBreed(name);
                        formValues.add(new BasicNameValuePair("dogBreed[" + (i + 1) + "]", breed));

                        boolean doBath = dogSelectors.get(i).doExitBath();
                        String bathString = "";
                        if(doBath) {
                            bathString = "Yes";
                        } else {
                            bathString = "No";
                        }
                        formValues.add(new BasicNameValuePair("bath[" + (i + 1) + "]", bathString));
                    }

                    for(int i = 0; i < catSelectors.size(); i++) {
                        String name = catSelectors.get(i).getPetName();
                        formValues.add(new BasicNameValuePair("catName[" + (i + 1) + "]", name));

                        PetsDAO db = new PetsDAO(ReservationsScreen.this);
                        String breed = db.getPetBreed(name);
                        formValues.add(new BasicNameValuePair("catBreed[" + (i + 1) + "]", breed));
                    }

                    formValues.add(new BasicNameValuePair("price", priceProcessor.getPrice()));

                    formValues.add(new BasicNameValuePair("notes", "From Android App --- " + comments.getText().toString()));

                    ReservationProcessor async = new ReservationProcessor(formValues, new ReservationProcessor.ReservationProcessorListener() {
                        @Override
                        public void onSuccess() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ReservationsScreen.this).setCancelable(false);
                            builder = builder.setTitle(getString(R.string.reservation_success_header));
                            builder = builder.setMessage(getString(R.string.reservation_success));
                            builder = builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    ReservationsScreen.this.finish();
                                }
                            });
                            builder.show();
                        }

                        @Override
                        public void onFailure() {
                            CustomToast toast = new CustomToast(ReservationsScreen.this, getString(R.string.reservation_error));
                            toast.show();
                        }
                    });
                    async.makeReservation();
                } catch (Exception e) {
                    e.printStackTrace();
                    CustomToast toast = new CustomToast(ReservationsScreen.this, getString(R.string.reservation_error));
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
