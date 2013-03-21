/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.database;

public enum SettingsTableColumnNames {
    //settingName TEXT PRIMARY KEY, settingValue TEXT, visible INTEGER
    NAME("settingName"),
    VALUE("settingValue"),
    VISIBLE("visible");

    SettingsTableColumnNames(String value) {
        this.columnName = value;
    }

    String columnName;

    public String getString() {
        return columnName;
    }
}
