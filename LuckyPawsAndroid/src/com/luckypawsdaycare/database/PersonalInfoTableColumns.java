/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.database;

public enum PersonalInfoTableColumns {
//    _id INTEGER PRIMARY KEY, firstName TEXT, lastName TEXT, eMail TEXT, phone1 TEXT, phone2 TEXT, phone3 TEXT, addressStreet TEXT, addressCity TEXT, addressState TEXT, addressZip INTEGER, referral TEXT, agreed INTEGER, signed TEXT
    ID("_id"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    EMAIL("eMail"),
    PHONE_ONE("phone1"),
    PHONE_TWO("phone2"),
    PHONE_THREE("phone3"),
    STREET("addressStreet"),
    CITY("addressCity"),
    STATE("addressState"),
    ZIP("addressZip"),
    REFERRAL("referral"),
    AGREED("agreed"),
    SIGNED("signed");

    PersonalInfoTableColumns(String value) {
        this.columnName = value;
    }

    String columnName;

    public String getString() {
        return columnName;
    }
}
