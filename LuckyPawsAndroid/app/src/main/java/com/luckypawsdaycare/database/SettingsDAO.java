/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class SettingsDAO extends DatabaseHelper {
    Context mContext;
    private final String TAG = "SettingsDAO";

    public SettingsDAO(Context context) {
        super(context);
        mContext = context;
    }

    public boolean getPersistentSetting(String settingName) {
        SQLiteDatabase db = getReadableDatabase();
        String sqlStatement = String.format("SELECT %s FROM settings WHERE %s = ?",
                SettingsTableColumnNames.VALUE.getString(), SettingsTableColumnNames.NAME.getString());


        Cursor result = db.rawQuery(sqlStatement, new String[]{settingName});
        result.moveToFirst();

        String setting = result.getString(0);

        result.close();
        db.close();

        return setting.equals(DatabaseConstants.YES);
    }

    public Cursor getVisiblePersistentSettings() {
        SQLiteDatabase db = getReadableDatabase();
        String sqlStatement = "SELECT * FROM settings WHERE " + SettingsTableColumnNames.VISIBLE.getString() + " = "
                + DatabaseConstants.TRUE + ";";

        Cursor result = db.rawQuery(sqlStatement, null);
        result.moveToFirst();

        db.close();
        return result;
    }

    public boolean setPersistentSetting(String settingName, String value) {
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement stmt = db.compileStatement("UPDATE settings SET " + SettingsTableColumnNames.VALUE.getString() +
                " = ? WHERE " + SettingsTableColumnNames.NAME.getString() + " = ?");
        stmt.bindString(1, value);
        stmt.bindString(2, settingName);

        boolean success = false;
        db.beginTransaction();
        try
        {
            stmt.execute();
            db.setTransactionSuccessful();
            success = true;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            db.endTransaction();
            stmt.close();
            db.close();
        }

        return success;

    }
}
