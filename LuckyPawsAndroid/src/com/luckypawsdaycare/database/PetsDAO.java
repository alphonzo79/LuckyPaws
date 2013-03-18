/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class PetsDAO extends DatabaseHelper {
    Context mContext;

    public PetsDAO(Context context) {
        super(context);
        this.mContext = context;
    }

    public int countPets() {
        SQLiteDatabase db = getReadableDatabase();
        String stmt = "SELECT count(*) FROM pets;";

        Cursor rs = db.rawQuery(stmt, null);
        int result = -1;
        try {
            rs.moveToFirst();
            result = rs.getInt(0);
        } catch(SQLiteException e) {
            e.printStackTrace();
        } finally {
            rs.close();
            db.close();
        }

        return result;
    }
}
