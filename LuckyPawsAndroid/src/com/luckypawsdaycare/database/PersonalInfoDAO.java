/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String stmt = String.format("SELECT count(*) FROM personalInfo WHERE %s = '' AND %s = '' AND %s = '' AND %s = ''" +
                " AND %s = '' AND %s = '' AND %s = '' AND %s = '' AND %s = '' AND (%s = '' OR %s = 0) AND %s = '' AND %s" +
                " = 0 AND %s = '';", PersonalInfoTableColumns.FIRST_NAME.getString(),
                PersonalInfoTableColumns.LAST_NAME.getString(), PersonalInfoTableColumns.EMAIL.getString(),
                PersonalInfoTableColumns.PHONE_ONE.getString(), PersonalInfoTableColumns.PHONE_TWO.getString(),
                PersonalInfoTableColumns.PHONE_THREE.getString(), PersonalInfoTableColumns.STREET.getString(),
                PersonalInfoTableColumns.CITY.getString(), PersonalInfoTableColumns.STATE.getString(),
                PersonalInfoTableColumns.ZIP.getString(), PersonalInfoTableColumns.ZIP.getString(),
                PersonalInfoTableColumns.REFERRAL.getString(), PersonalInfoTableColumns.AGREED.getString(),
                PersonalInfoTableColumns.SIGNED.getString());

        Cursor rs = db.rawQuery(stmt, null);
        boolean result = false;
        try {
            rs.moveToFirst();
            int count = rs.getInt(0);
            result = (count == 0);
        } catch(SQLiteException e) {
            e.printStackTrace();
        } finally {
            rs.close();
            db.close();
        }

        return result;
    }

    public Map<String, String> getPersonalInfo() {
        SQLiteDatabase db = getReadableDatabase();

        String stmt = String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s FROM personalInfo WHERE %s = 1;",
                PersonalInfoTableColumns.FIRST_NAME.getString(), PersonalInfoTableColumns.LAST_NAME.getString(),
                PersonalInfoTableColumns.EMAIL.getString(), PersonalInfoTableColumns.PHONE_ONE.getString(),
                PersonalInfoTableColumns.PHONE_TWO.getString(), PersonalInfoTableColumns.PHONE_THREE.getString(),
                PersonalInfoTableColumns.STREET.getString(), PersonalInfoTableColumns.CITY.getString(),
                PersonalInfoTableColumns.STATE.getString(), PersonalInfoTableColumns.ZIP.getString(),
                PersonalInfoTableColumns.ID.getString());

        Cursor rs = db.rawQuery(stmt, null);
        Map<String, String> result = new HashMap<String, String>(10);
        try{
            rs.moveToFirst();
            for(String column : rs.getColumnNames()) {
                int index = rs.getColumnIndex(column);
                result.put(column, rs.getString(index));
            }
        } catch(SQLiteException e) {
            e.printStackTrace();
        } finally {
            rs.close();
            db.close();
        }
        return result;
    }

    public boolean setPersonalInfo(Map<String, String> updateValues) {
        SQLiteDatabase db = getWritableDatabase();

        //Build up the query. We will also use an ArrayList to hold the keys so we can guarantee to bind the args in the right order
        List<String> keys = new ArrayList<String>();
        keys.addAll(updateValues.keySet());
        StringBuilder sb = new StringBuilder("");
        for(int i = 0; i < keys.size(); i++) {
            sb.append(keys.get(i) + " = ?, ");
        }
        String updateArgs = sb.toString();
        //Remove the last comma
        updateArgs = updateArgs.substring(0, updateArgs.length() - 2);

        SQLiteStatement stmt = db.compileStatement("UPDATE personalInfo SET " + updateArgs + " WHERE " +
                PersonalInfoTableColumns.ID.getString() + " = 1;");
        for(int i = 0; i < keys.size(); i++) {
            stmt.bindString(i + 1, updateValues.get(keys.get(i)));
        }

        boolean result = false;
        db.beginTransaction();
        try {
            stmt.execute();
            result = true;
            db.setTransactionSuccessful();
        } catch(SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            stmt.clearBindings();
            stmt.close();
        }

        return result;
    }
}
