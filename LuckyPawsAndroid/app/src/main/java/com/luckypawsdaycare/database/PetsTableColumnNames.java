/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.database;

public enum PetsTableColumnNames {
//    _id, petName TEXT, dogCat INTEGER, dateOfBirth TEXT, sex INTEGER, size INTEGER, breed TEXT, color TEXT, fixed INTEGER
    ID("_id"),
    NAME("petName"),
    DOG_CAT("dogCat"),
    BIRTHDAY("dateOfBirth"),
    SEX("sex"),
    SIZE("size"),
    BREED("breed"),
    COLOR("color"),
    FIXED("fixed");

    PetsTableColumnNames(String value) {
        this.columnName = value;
    }

    String columnName;

    public String getString() {
        return columnName;
    }
}
