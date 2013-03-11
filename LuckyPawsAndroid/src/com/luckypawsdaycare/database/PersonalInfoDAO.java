/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class PersonalInfoDAO extends DatabaseHelper {
    Context context;

    public PersonalInfoDAO(Context context) {
        super(context);
        this.context = context;
    }

    public boolean getPersonalInfoIsSet() {
        SQLiteDatabase db = getReadableDatabase();

        //firstName TEXT, lastName TEXT, eMail TEXT, phone1 TEXT, phone2 TEXT, phone3 TEXT, addressStreet TEXT, addressCity TEXT,
        // addressState TEXT, addressZip TEXT, referral TEXT, agreed INTEGER, signed TEXT
        String stmt = "SELECT count(*) FROM personalInfo WHERE firstName = '' AND lastName = '' AND eMail = '' AND phone1 = ''" +
                " AND phone2 = '' AND phone3 = ''AND addressStreet = '' AND addressCity = '' AND addressState = '' AND" +
                " addressZip = '' AND referral = '' AND agreed = '' AND signed = ''";

        Cursor rs = db.rawQuery(stmt, null);
        boolean result = false;
        try {
            rs.moveToFirst();
            int count = rs.getInt(0);
            result = (count == 1);
        } catch(SQLiteException e) {
            e.printStackTrace();
        }

        return result;
    }
}
