package com.luckypawsdaycare.activities;

import android.os.Bundle;
import com.crashlytics.android.Crashlytics;

public class MainScreen extends MainScreenParent {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Crashlytics.start(this);
    }
}
