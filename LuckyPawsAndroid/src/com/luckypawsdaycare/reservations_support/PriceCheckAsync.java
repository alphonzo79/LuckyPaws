package com.luckypawsdaycare.reservations_support;

import android.annotation.TargetApi;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PriceCheckAsync implements Runnable {
    private final String urlRoot = "http://www.luckypawsdaycare.com/processMobile.php?";
    private final String host = "www.luckypawsdaycare.com";

    private PriceCheckListener listener;
    private AndroidHttpClient client;
    private String userAgent;
    private HttpHost httpHost;
    private BasicHttpContext executionContext;

    private String arguments;

    @TargetApi(Build.VERSION_CODES.FROYO)
    public PriceCheckAsync(String arguments, PriceCheckListener listener) {
        this.arguments = StringEscapeUtils.escapeHtml4(arguments);

        this.listener = listener;
        userAgent = System.getProperty("http.agent");
        if(client == null) {
            client = AndroidHttpClient.newInstance(userAgent);
        }
        httpHost = new HttpHost(host, 80, "http");
        executionContext = new BasicHttpContext();
    }

    public void getPrice() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        HttpGet request = new HttpGet(urlRoot + arguments);
        HttpResponse response = null;
        String result = "";
        try {
            response = client.execute(httpHost, request, executionContext);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
            Log.d("LuckyPaws", "Price Check Result: " + result);
        } catch (IOException e) {
            Log.e("LuckyPaws", "Caught IOException while trying to get pricing");
            e.printStackTrace();
        }
        client.getConnectionManager().shutdown();
        client.close();
        client = null;

        if(!TextUtils.isEmpty(result)){
            Map<String, String> prices = new HashMap<String, String>();
            String[] splitString = result.split(";");
            for(String segment : splitString) {
                String[] secondSplit = segment.split(":");
                if(secondSplit.length == 2) {
                    prices.put(secondSplit[0], secondSplit[1]);
                }
            }
            Log.d("LuckyPaws", "Price Check Map Return: `" + prices.toString());
            listener.handleFoundPrice(prices);
        }
    }

    public interface PriceCheckListener {
        public void handleFoundPrice(Map<String, String> prices);
    }
}
