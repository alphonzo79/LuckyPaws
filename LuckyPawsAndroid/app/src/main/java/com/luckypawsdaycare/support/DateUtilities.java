/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.support;

import java.text.SimpleDateFormat;

public class DateUtilities {
    public static SimpleDateFormat appDateFormat() {
        return new SimpleDateFormat("MMM dd, yyyy");
    }
    public static SimpleDateFormat reservationRequestDateFormat() {
        return new SimpleDateFormat("MM/dd/yyyy");
    }
}
