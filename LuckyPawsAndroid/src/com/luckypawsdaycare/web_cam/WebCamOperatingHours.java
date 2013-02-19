/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.web_cam;

public enum WebCamOperatingHours {
    WEEKDAY_START(7, 30), WEEKDAY_END(18, 30), WEEKEND_START(10, 0), WEEKEND_END(16, 30);

    int hour;
    int minute;

    WebCamOperatingHours(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }
}
