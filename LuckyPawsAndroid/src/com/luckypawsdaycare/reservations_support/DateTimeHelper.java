package com.luckypawsdaycare.reservations_support;

import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DateTimeHelper {
    Map<String, String> availableTimeStrings;//<displayString, paramString
    DateTimeCheckListener listener;
    DateTimeCheckAsync asyncWorker;
    InDateOutDate inOutEnum;

    public DateTimeHelper(DateTimeCheckListener listener, InDateOutDate inOrOut) {
        this.listener = listener;
        this.inOutEnum = inOrOut;
        this.asyncWorker = new DateTimeCheckAsync(listener);
    }

    public void checkAvailableTimes(Calendar date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateString = sdf.format(date.getTime());
        asyncWorker.checkDate(String.format("%s=%s", inOutEnum.getArgPrefix(), dateString));
    }

    public String getParamString(String displayString) {
        if(availableTimeStrings != null && availableTimeStrings.containsKey(displayString)) {
            return availableTimeStrings.get(displayString);
        } else {
            return "";
        }
    }

    public interface DateTimeCheckListener {
        public void updateStrings(List<String> displayStrings);
        public void closedDate();
    }

    public enum InDateOutDate {
        IN("inDate"), OUT("outDate");

        private String argPrefix;

        private InDateOutDate(String argPrefix) {
            this.argPrefix = argPrefix;
        }

        public String getArgPrefix() {
            return argPrefix;
        }
    }

    private class DateTimeCheckAsync implements Runnable {
        private final String urlRoot = "http://www.luckypawsdaycare.com/processMobile.php?";
        private final String host = "www.luckypawsdaycare.com";

        private DateTimeCheckListener listener;
        private AndroidHttpClient client;
        private String userAgent;
        private HttpHost httpHost;
        private BasicHttpContext executionContext;

        private String argument;

        public DateTimeCheckAsync(DateTimeCheckListener listener) {
            this.listener = listener;
            userAgent = System.getProperty("http.agent");
            if(client == null) {
                client = AndroidHttpClient.newInstance(userAgent);
            }
            httpHost = new HttpHost(host, 80, "http");
            executionContext = new BasicHttpContext();
        }

        public void checkDate(String date) {
            this.argument = date;
            new Thread(this).start();
        }

        @Override
        public void run() {
            HttpGet request = new HttpGet(urlRoot + argument);
            HttpResponse response = null;
            String result = "";
            try {
                if(client == null) {
                    client = AndroidHttpClient.newInstance(userAgent);
                }

                response = client.execute(httpHost, request, executionContext);
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);
                Log.d("LuckyPaws", "DateTimeCheck Result: " + result);
            } catch (IOException e) {
                Log.e("LuckyPaws", "Caught IOException while trying to get Date/Time info");
                e.printStackTrace();
            }
            client.getConnectionManager().shutdown();
            client.close();
            client = null;

            if(!TextUtils.isEmpty(result)){
                boolean success = true;
                Map<String, String> times = new HashMap<String, String>();
                String[] divSplit = result.split("<div>");
                boolean innerBroke = false;
                for(String segment : divSplit) {
                    if(segment.contains(inOutEnum.getArgPrefix())) {
                        String[] ampersandSplit = segment.split("&");

                        for(String innerSegment : ampersandSplit) {
                            if(segment.contains("closed")) {
                                failureHandler.sendEmptyMessage(0);
                                success = false;
                                innerBroke = true;
                                break;
                            }

                            String[] periodSplit = innerSegment.split("\\.");
                            if(periodSplit.length == 2) {
                                String encoded = innerSegment.replace(" ", "%20");
                                times.put(periodSplit[1], encoded);
                            }
                        }
                    }
                    if(innerBroke) {
                        break;
                    }
                }

                Log.d("LuckyPaws", "Times Map Return: " + times.toString());

                if(success == true) {
                    if(times instanceof Serializable){
                        Message message = Message.obtain();
                        message.getData().putSerializable("timeMap", (Serializable)times);
                        successHandler.sendMessage(message);
                    }
                }
            }
        }
    }

    Handler successHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            availableTimeStrings = (Map<String, String>)message.getData().getSerializable("timeMap");
            String[] results = new String[availableTimeStrings.size()];
            for(String key :availableTimeStrings.keySet()) {
                String value = availableTimeStrings.get(key);
                //Make sure we return these in the order we want
                if(value.contains("AM")) {
                    results[0] = key;
                } else if(value.contains("PM")) {
                    results[1] = key;
                } else if(value.contains("UK")) {
                    results[2] = key;
                }
            }
            listener.updateStrings(new ArrayList<String>(Arrays.asList(results)));
        }
    };

    Handler failureHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            listener.closedDate();
        }
    };
}
