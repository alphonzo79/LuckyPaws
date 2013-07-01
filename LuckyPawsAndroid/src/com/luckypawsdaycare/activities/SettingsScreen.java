/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.database.DatabaseConstants;
import com.luckypawsdaycare.database.PersonalInfoDAO;
import com.luckypawsdaycare.database.PetsDAO;
import com.luckypawsdaycare.database.SettingsDAO;

import java.util.ArrayList;
import java.util.List;

public class SettingsScreen extends ListActivity {
    private final String TAG = "SettingsScreen";
    private final int SCREEN_LOCK_REQUEST = 10;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_screen);
    }

    @Override
    public void onResume() {
        super.onResume();
        prepList();
    }

    private void prepList() {
        SettingsDAO db = new SettingsDAO(this);
        Cursor settingsCursor = db.getVisiblePersistentSettings();
        settingsCursor.moveToFirst();

        List<String> settings = new ArrayList<String>();
        do {
            String settingName = settingsCursor.getString(0);
            String settingValue = settingsCursor.getString(1);
            if(settingValue != null && !settingValue.toLowerCase().equals("null")) {
                settings.add(String.format("%s: %s", settingName, settingValue));
            } else {
                settings.add(settingName);
            }
        } while(settingsCursor.moveToNext());

        settingsCursor.close();

        setListAdapter(new ArrayAdapter<String>(this, R.layout.plain_list_layout, settings));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        String itemText = (String) l.getItemAtPosition(position);

        if(itemText.contains(DatabaseConstants.SETTINGS_SCREEN_LOCK_SETTING)) {
            String value = itemText.split(":")[1].trim();

            String[] valuesArray = new String[]{DatabaseConstants.YES, DatabaseConstants.NO};

            Intent selector = new Intent(this, ValueSelector.class);
            selector.putExtra("selectedValue", value);
            selector.putExtra("valuesArray", valuesArray);
            selector.putExtra("header", "Keep Screen On While Streaming Webcam?");
            startActivityForResult(selector, SCREEN_LOCK_REQUEST);
        } else if(itemText.equals(DatabaseConstants.SETTINGS_PERSONAL_INFO_SETTING)) {
            PersonalInfoDAO db = new PersonalInfoDAO(this);
            if(db.getPersonalInfoIsSet()) {
                Intent intent = new Intent(this, PersonalInfoDisplay.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, PersonalInfoEdit.class);
                startActivity(intent);
            }
        } else if(itemText.equals(DatabaseConstants.SETTINGS_MANAGE_PETS_SETTINGS)) {
            PetsDAO db = new PetsDAO(this);
            if(db.countPets() > 0) {
                Intent showPets = new Intent(this, ListMyPets.class);
                startActivity(showPets);
            } else {
                Intent addPet = new Intent(this, EditMyPets.class);
                startActivity(addPet);
            }
        } else if(itemText.equals(DatabaseConstants.SETTINGS_INFO_ABOUT_SETTING)) {
            Intent info = new Intent(this, InfoAboutScreen.class);
            startActivity(info);
        } else if(itemText.equals(DatabaseConstants.SETTINGS_CONTACT_SETTING)) {
            Intent contact = new Intent(this, ContactScreen.class);
            startActivity(contact);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == SCREEN_LOCK_REQUEST && resultCode == RESULT_OK) {
            String selected = data.getStringExtra("selectedValue");
            if(selected != null) {
                SettingsDAO db = new SettingsDAO(this);
                db.setPersistentSetting(DatabaseConstants.SETTINGS_SCREEN_LOCK_SETTING, selected);
                prepList();
            }
        }
    }
}
