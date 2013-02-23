/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SettingsDAO extends DatabaseHelper {
    Context mContext;

    public SettingsDAO(Context context) {
        super(context);
        mContext = context;
    }

    public boolean getWebCamScreenLockSetting() {
        SQLiteDatabase db = getReadableDatabase();
        String sqlStatement = "SELECT settingValue FROM settings WHERE settingName = 'Keep Webcam Screen On'";

        Cursor result = db.rawQuery(sqlStatement, null);
        result.moveToFirst();

        String setting = result.getString(0);

        result.close();
        db.close();

        return setting.equals(DatabaseConstants.YES);
    }
}
