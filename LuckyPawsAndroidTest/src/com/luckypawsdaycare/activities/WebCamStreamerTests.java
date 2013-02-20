package com.luckypawsdaycare.activities;

import android.test.ActivityInstrumentationTestCase2;
import com.luckypawsdaycare.web_cam.WebCamStreamer;
import junit.framework.Assert;
import org.junit.Test;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application activities.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.luckypawsdaycare.activities.WebCamStreamerTests \
 * com.luckypawsdaycare.activities/android.test.InstrumentationTestRunner
 */
public class WebCamStreamerTests extends ActivityInstrumentationTestCase2<WebCamViewScreen> {
    WebCamViewScreen activity;

    public WebCamStreamerTests() {
        super("com.luckypawsdaycare.activities", WebCamViewScreen.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }

    //NOTE: This test assumes it is being run in the mountain time zone. It will need to be modified if it is running elsewhere.
    @Test
    public void testConvertToEasternTime() {
        long currentTime = System.currentTimeMillis();
        WebCamStreamer mAut = new WebCamStreamer(activity);
        long easternTime = mAut.convertToEasternTime(currentTime);

        long offset = 60000 * 60 * 2l; //two hours
        long expected = currentTime + offset;

        Assert.assertEquals("The time did not convert to two hours later as expected", expected, easternTime);
    }
}
