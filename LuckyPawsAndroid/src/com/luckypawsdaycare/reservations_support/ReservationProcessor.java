/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.reservations_support;

import android.annotation.TargetApi;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.BasicHttpContext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public class ReservationProcessor implements Runnable {
    List<NameValuePair> valueMap;

    private final String urlRoot = "http://www.luckypawsdaycare.com/makeReservation.php";
    private final String host = "www.luckypawsdaycare.com";
    private AndroidHttpClient client;
    private String userAgent;
    private HttpHost httpHost;
    private BasicHttpContext executionContext;

    ReservationProcessorListener listener;

    @TargetApi(Build.VERSION_CODES.FROYO)
    public ReservationProcessor(List<NameValuePair> formValues, ReservationProcessorListener listener) {
        this.valueMap = formValues;
        this.listener = listener;

        userAgent = System.getProperty("http.agent");
        if(client == null) {
            client = AndroidHttpClient.newInstance(userAgent);
        }
        httpHost = new HttpHost(host, 80, "http");
        executionContext = new BasicHttpContext();
    }

    public void makeReservation() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        HttpPost request = new HttpPost(urlRoot);
        HttpResponse response = null;
        try {
            request.setEntity(new UrlEncodedFormEntity(valueMap));
            response = client.execute(httpHost, request, executionContext);
            int code = response.getStatusLine().getStatusCode();
            if(code == 200) {
                reservationSuccess.sendEmptyMessage(0);
            } else {
                reservationFailure.sendEmptyMessage(0);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            reservationFailure.sendEmptyMessage(0);
        } catch (IOException e) {
            e.printStackTrace();
            reservationFailure.sendEmptyMessage(0);
        }
        client.getConnectionManager().shutdown();
        client.close();
        client = null;
    }

    Handler reservationSuccess = new Handler() {
        @Override
        public void handleMessage(Message message) {
            listener.onSuccess();
        }
    };

    Handler reservationFailure = new Handler() {
        @Override
        public void handleMessage(Message message) {
            listener.onFailure();
        }
    };

    public interface ReservationProcessorListener {
        public void onSuccess();
        public void onFailure();
    }
}
