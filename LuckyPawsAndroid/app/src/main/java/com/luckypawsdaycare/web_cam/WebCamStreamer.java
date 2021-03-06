/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.web_cam;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.activities.WebCamViewScreen;

import java.util.Calendar;
import java.util.TimeZone;

public class WebCamStreamer {
    private final String TAG = "WebCamStreamer";

    private WebCamViewScreen caller;
    private WebCamAsync asyncTask;
    AndroidHttpClient client;
    String userAgent;
    boolean asyncRunning;

    public WebCamStreamer(WebCamViewScreen callingActivity) {
        caller = callingActivity;
        Bitmap defaultImage = BitmapFactory.decodeResource(callingActivity.getResources(), R.drawable.webcam_loading);
        asyncTask = new WebCamAsync(updateImage, defaultImage);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public void beginStream() {
        if(checkWorkingHours()) {
            userAgent = System.getProperty( "http.agent" );
            if(client == null) {
                client = AndroidHttpClient.newInstance(userAgent);
            }

            asyncTask.setKeepGoing(true);
            asyncRunning = true;
            asyncTask.startThread(client);
        } else {
            caller.setDefaultImage();
        }
    }

    public void pauseStream() {
        if(asyncRunning) {
            asyncTask.setKeepGoing(false);
        } //Otherwise do nothing
    }

    public void killStream() {
        //Wait for the async to stop gracefully
        while(asyncTask.isRunning()) {
            try{
                Thread.sleep(100);
            } catch(InterruptedException e) {
                //Do nothing, try again
            }
        }
        if(client != null) {
            client.getConnectionManager().shutdown();
            client.close();
            client = null;
        }
        asyncRunning = false;
    }

    public Handler updateImage = new Handler(){
        @Override
        public void handleMessage (Message msg){
            boolean success = msg.getData().getBoolean("success", false);
            if(success) {
                caller.updateImage(asyncTask.getImage());
            } else {
                caller.setDefaultImage();
            }
        }
    };

    public boolean checkWorkingHours() {
        boolean webCamOn = false;
        //Webcam is available from 7:30 a.m. to 6:30 p.m. M-F and 10 a.m to 4:30 p.m. on weekends
        //Get current local time
        long currentTime = System.currentTimeMillis();
        long lpCurrentTime = convertToEasternTime(currentTime);

        //get the day
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(lpCurrentTime);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        //Compare the time to the appropriate constraint
        switch(day) {
            case Calendar.MONDAY:
            case Calendar.TUESDAY:
            case Calendar.WEDNESDAY:
            case Calendar.THURSDAY:
            case Calendar.FRIDAY:
                //simplest case
                if(hour > WebCamOperatingHours.WEEKDAY_START.getHour() && hour < WebCamOperatingHours.WEEKDAY_END.getHour()) {
                    webCamOn = true;
                } else if(hour == WebCamOperatingHours.WEEKDAY_START.getHour()) {
                    //If we are in the starting hour, make sure we meet the minimum minutes
                    if(minute >= WebCamOperatingHours.WEEKDAY_START.getMinute()) {
                        webCamOn = true;
                    }
                } else if(hour == WebCamOperatingHours.WEEKDAY_END.getHour()) {
                    //if we are in the ending hour, make sure we do not exceed the maximum minutes
                    if(minute <= WebCamOperatingHours.WEEKDAY_END.getMinute()) {
                        webCamOn = true;
                    }
                }
                break;
            case Calendar.SATURDAY:
            case Calendar.SUNDAY:
            default:
                //simplest case
                if(hour > WebCamOperatingHours.WEEKEND_START.getHour() && hour < WebCamOperatingHours.WEEKEND_END.getHour()) {
                    webCamOn = true;
                } else if(hour == WebCamOperatingHours.WEEKEND_START.getHour()) {
                    //If we are in the starting hour, make sure we meet the minimum minutes
                    if(minute >= WebCamOperatingHours.WEEKEND_START.getMinute()) {
                        webCamOn = true;
                    }
                } else if(hour == WebCamOperatingHours.WEEKEND_END.getHour()) {
                    //if we are in the ending hour, make sure we do not exceed the maximum minutes
                    if(minute <= WebCamOperatingHours.WEEKEND_END.getMinute()) {
                        webCamOn = true;
                    }
                }
                break;
        }
        return webCamOn;
    }

    public long convertToEasternTime(long localTimeInMillis) {
        TimeZone myTimeZone = TimeZone.getDefault();
        TimeZone lpTimeZone = TimeZone.getTimeZone("US/Eastern");
        int myOffset = myTimeZone.getOffset(localTimeInMillis);
        int lpOffset = lpTimeZone.getOffset(localTimeInMillis);
        //convert time to Eastern
        int diff = lpOffset - myOffset;
        //If we are west of LP, we will get a ositive value here. To convert the time we will need to add
        //the offset diff from current time
        return localTimeInMillis + diff;
    }

    public boolean isAsyncRunning() {
        return asyncRunning;
    }
}
