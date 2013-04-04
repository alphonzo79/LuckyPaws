/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;

import java.util.*;

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

    public Map<String, String> getPetData(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String stmt = "SELECT * FROM pets WHERE " + PetsTableColumnNames.ID.getString() + " = ?;";

        Cursor rs = db.rawQuery(stmt, new String[]{Integer.toString(id)});
        Map<String, String> result = new HashMap<String, String>();
        try {
            rs.moveToFirst();
            int columns = rs.getColumnCount();
            for(int i = 0; i < columns; i++) {
                result.put(rs.getColumnName(i), rs.getString(i));
            }
        } catch(SQLiteException e) {
            e.printStackTrace();
        } finally {
            rs.close();
            db.close();
        }

        return result;
    }

    public boolean addPetData(Map<String, String> args) {
        boolean success = false;

        //Build up the query. We will use an ArrayList to hold the keys so we can guarantee to bind the args in the right order
        List<String> keys = new ArrayList<String>();
        keys.addAll(args.keySet());
        StringBuilder keysSb = new StringBuilder("");
        StringBuilder valsSb = new StringBuilder("");
        for(int i = 0; i < keys.size(); i++) {
            keysSb.append(keys.get(i) + ", ");
            valsSb.append("?, ");
        }
        String keyArgs = keysSb.toString();
        String valueArgs = valsSb.toString();
        //Remove the last comma
        keyArgs = keyArgs.substring(0, keyArgs.length() - 2);
        valueArgs = valueArgs.substring(0, valueArgs.length() - 2);

        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement stmt = db.compileStatement(String.format("INSERT INTO pets (%s) VALUES (%s);", keyArgs, valueArgs));
        for(int i = 0; i < keys.size(); i++) {
            String column = keys.get(i);
            if(column.equals(PetsTableColumnNames.NAME.getString())
                    || column.equals(PetsTableColumnNames.BIRTHDAY.getString())
                    || column.equals(PetsTableColumnNames.BREED.getString())
                    || column.equals(PetsTableColumnNames.COLOR.getString())) {
                stmt.bindString(i + 1, args.get(column));
            } else {
                stmt .bindLong(i + 1, Integer.parseInt(args.get(column)));
            }
        }

        db.beginTransaction();
        try {
            stmt.execute();
            success = true;
            db.setTransactionSuccessful();
        } catch(SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            stmt.clearBindings();
            stmt.close();
        }

        return success;
    }

    public boolean updatePetData(int id, Map<String, String> args) {
        boolean success = false;

        //Build up the query. We will use an ArrayList to hold the keys so we can guarantee to bind the args in the right order
        List<String> keys = new ArrayList<String>();
        keys.addAll(args.keySet());
        StringBuilder sb = new StringBuilder("");
        for(int i = 0; i < keys.size(); i++) {
            sb.append(keys.get(i) + " = ?, ");
        }
        String updateArgs = sb.toString();
        //Remove the last comma
        updateArgs = updateArgs.substring(0, updateArgs.length() - 2);

        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement stmt = db.compileStatement(String.format("UPDATE pets SET %s WHERE " +
                PetsTableColumnNames.ID.getString() + " = %d;", updateArgs, id));
        for(int i = 0; i < keys.size(); i++) {
            String column = keys.get(i);
            if(column.equals(PetsTableColumnNames.NAME.getString())
                    || column.equals(PetsTableColumnNames.BIRTHDAY.getString())
                    || column.equals(PetsTableColumnNames.BREED.getString())
                    || column.equals(PetsTableColumnNames.COLOR.getString())) {
                stmt.bindString(i + 1, args.get(column));
            } else {
                stmt .bindLong(i + 1, Integer.parseInt(args.get(column)));
            }
        }

        db.beginTransaction();
        try {
            stmt.execute();
            success = true;
            db.setTransactionSuccessful();
        } catch(SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            stmt.clearBindings();
            stmt.close();
        }

        return success;
    }

    public Map<String, Integer> getPetsIndex() {
        Map<String, Integer> result = new TreeMap<String, Integer>();

        SQLiteDatabase db = getReadableDatabase();
        String stmt = "SELECT _id, petName FROM pets;";

        Cursor rs = db.rawQuery(stmt, null);
        try {
            rs.moveToFirst();
            do {
                result.put(rs.getString(1), rs.getInt(0));
            } while (rs.moveToNext());
        } catch(SQLiteException e) {
            e.printStackTrace();
        } finally {
            rs.close();
            db.close();
        }

        return result;
    }
}
