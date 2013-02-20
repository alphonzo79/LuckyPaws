/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.web_cam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.activities.WebCamViewScreen;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.Calendar;
import java.util.TimeZone;

public class WebCamStreamer {
    private final String TAG = "WebCamStreamer";

    private WebCamViewScreen caller;
    private WebCamAsync asyncTask;
    DefaultHttpClient client;
    boolean asyncRunning;

    public WebCamStreamer(WebCamViewScreen callingActivity) {
        caller = callingActivity;
        Bitmap defaultImage = BitmapFactory.decodeResource(callingActivity.getResources(), R.drawable.webcam_off);
        asyncTask = new WebCamAsync(updateImage, defaultImage);
    }

    public void beginStream() {
        Log.d(TAG, "Begin Stream called");
        if(checkWorkingHours()) {
            client = new DefaultHttpClient();

            asyncTask.setKeepGoing(true);
            asyncRunning = true;
            asyncTask.startThread(client);
        } else {
            caller.setDefaultImage();
        }
    }

    public void pauseStream() {
        Log.d(TAG, "pauseStream called");
        if(asyncRunning) {
            asyncTask.setKeepGoing(false);
            //Wait for the async to stop gracefully
            while(asyncTask.isRunning()) {
                try{
                    Thread.sleep(100);
                } catch(InterruptedException e) {
                    //Do nothing, try again
                }
            }
            client.getConnectionManager().shutdown();
            client = null;
            asyncRunning = false;
        } //Otherwise do nothing
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
