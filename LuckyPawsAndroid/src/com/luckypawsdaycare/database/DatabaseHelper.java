/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import com.luckypawsdaycare.R;

import java.sql.SQLException;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "luckyPawsDB";
    private static final int VERSION = 1;

    Context mContext;
    private final int SETTINGS_TABLE_COLUMN_COUNT = 3;
    private final int PERSONAL_INFO_TABLE_COLUMN_COUNT = 14;

    private final String TAG = "DatabaseHelper";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate. Start populating tables");
        //create tables
        sqLiteDatabase.beginTransaction();
        try {
            createTables(sqLiteDatabase);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, "Error creating tables and debug data: " + e.toString());
        } finally {
            sqLiteDatabase.endTransaction();
        }

        Log.d(TAG, "onCreate. Tables successfully created. Populating them now");
        //populate tables
        sqLiteDatabase.beginTransaction();
        try
        {
            populatePersistentSettings(sqLiteDatabase);
            populatePersonalInfo(sqLiteDatabase);
            sqLiteDatabase.setTransactionSuccessful();
        }
        catch (SQLException e)
        {
            Log.e(TAG, "Error populating tables" + e.toString());
        }
        catch (IllegalStateException e)
        {
            Log.e(TAG, "Error populating tables" + e.toString());
        }
        finally
        {
            sqLiteDatabase.endTransaction();
        }
        Log.d(TAG, "Finished onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //TODO when the time comes
    }

    /**
     * Method used to initially create the tables for the database
     */
    private void createTables(SQLiteDatabase db) throws SQLException
    {
        db.execSQL(mContext.getString(R.string.create_settings_table));
        db.execSQL(mContext.getString(R.string.create_personal_info_table));
        db.execSQL(mContext.getString(R.string.create_pets_table));
    }

    /**
     * Method used to seed data in the settings table
     * @throws IllegalStateException If the number of values don't match the number of columns in the table, we will throw
     * @throws SQLException
     */
    private void populatePersistentSettings(SQLiteDatabase db) throws IllegalStateException, SQLException
    {
        Log.d(TAG, "Starting populatePersistentSettings");
        SQLiteStatement sqlStatement;

        //First, get the values and parse them into individual lines, then parse the values,
        //create the sql statement, then execute it
        String[] settingsLines = parseResourceByLine(R.string.settings_default_values);

        for (int i = 0; i < settingsLines.length; i++)
        {
            String[] rowData = parseResourceByDelimiter(settingsLines[i], SETTINGS_TABLE_COLUMN_COUNT);

            //There are three columns to fill in this table
            if (rowData.length == SETTINGS_TABLE_COLUMN_COUNT)
            {
                sqlStatement = db.compileStatement(String.format("INSERT INTO settings (%s, %s, %s) VALUES (?, ?, ?)",
                        SettingsTableColumnNames.NAME.getString(), SettingsTableColumnNames.VALUE.getString(),
                        SettingsTableColumnNames.VISIBLE.getString()));
                sqlStatement.bindString(1, rowData[0]);
                sqlStatement.bindString(2, rowData[1]);
                sqlStatement.bindLong(3, Integer.parseInt(rowData[2]));

                sqlStatement.execute();
                sqlStatement.clearBindings();
                sqlStatement.close();
            }
            else
            {
                Log.d(TAG, "Error in populatePersistentSettings");
                throw new IllegalStateException("There were not 3 values to populate the settings table with. Values were" + rowData.toString());
            }
        }
    }

    /**
     * Method used to seed values in the object table
     * @throws SQLException
     */
    private void populatePersonalInfo(SQLiteDatabase db) throws SQLException
    {
        Log.d(TAG, "Starting populatePersonalInfo");
        SQLiteStatement sqlStatement;
//firstName TEXT, lastName TEXT, eMail TEXT, phone1 TEXT, phone2 TEXT, phone3 TEXT, addressStreet TEXT, addressCity TEXT, addressState TEXT, addressZip INTEGER,
// referral TEXT, agreed INTEGER, signed TEXT
        sqlStatement = db.compileStatement(String.format("INSERT INTO personalInfo (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s," +
                " %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", PersonalInfoTableColumns.ID.getString(),
                PersonalInfoTableColumns.FIRST_NAME.getString(), PersonalInfoTableColumns.LAST_NAME.getString(),
                PersonalInfoTableColumns.EMAIL.getString(), PersonalInfoTableColumns.PHONE_ONE.getString(),
                PersonalInfoTableColumns.PHONE_TWO.getString(), PersonalInfoTableColumns.PHONE_THREE.getString(),
                PersonalInfoTableColumns.STREET.getString(), PersonalInfoTableColumns.CITY.getString(),
                PersonalInfoTableColumns.STATE.getString(), PersonalInfoTableColumns.ZIP.getString(),
                PersonalInfoTableColumns.REFERRAL.getString(), PersonalInfoTableColumns.AGREED.getString(),
                PersonalInfoTableColumns.SIGNED.getString()));
        sqlStatement.bindLong(1, 1); //id
        sqlStatement.bindString(2, ""); //firstName
        sqlStatement.bindString(3, ""); //lastName
        sqlStatement.bindString(4, ""); //eMail
        sqlStatement.bindString(5, ""); //phone1
        sqlStatement.bindString(6, ""); //phone2
        sqlStatement.bindString(7, ""); //phone3
        sqlStatement.bindString(8, ""); //addressStreet
        sqlStatement.bindString(9, ""); //addressCity
        sqlStatement.bindString(10, ""); //addressState
        sqlStatement.bindLong(11, 0); //addressZip
        sqlStatement.bindString(12, ""); //referral
        sqlStatement.bindLong(13, 0); //agreed
        sqlStatement.bindString(14, ""); //signed

        sqlStatement.execute();
        sqlStatement.clearBindings();
        sqlStatement.close();
    }

    /**
     * We have initial database values saved as string resources with one line representing one row in the database.
     * We have put in a \n character at the end of each line, and the system has given us a space to represent the
     * line break. We need to parse out to individual lines so we can establish default values row by row.
     *
     * @param resource The resource that we want to parse
     * @return String[] with each line from the resource in each array index
     */
    protected String[] parseResourceByLine(int resource)
    {
        String[] retVal;

        String fullText = mContext.getString(resource);
        retVal = fullText.split("\n");

        return retVal;
    }

    /**
     * Once we have an array of parsed lines returned by parseResourceByLine, we need to split it into individual
     * values to populate the database with. We have used semicolons as our delimiter.
     *
     * @param resource The String[] that was returned to us from parseResourceByLine
     * @return String[][]: Each index represents one line from the resource. Each index then contains a String[] that contains the individual values from that line
     */
    protected String[] parseResourceByDelimiter(String resource, int limit)
    {
        String[] retVal = resource.split(";", limit); //The limit parameter will ensure that any semicolons included in the log notes will not force unwanted splits

        return retVal;
    }
}
