/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.database.SettingsDAO;

import java.util.ArrayList;
import java.util.List;

public class SettingsScreen extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_screen);
    }

    @Override
    public void onResume() {
        super.onResume();
        SettingsDAO db = new SettingsDAO(this);

        List<String> settings = new ArrayList<String>();
        for(int i = 0; i < 6; i++) {
            settings.add("Settings Number " + i);
        }

        setListAdapter(new ArrayAdapter<String>(this, R.layout.plain_list_layout, settings));
    }
}
