package com.luckypawsdaycare.reservations_support;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PriceCheckAsync extends AsyncTask<URL, Void, String> {
    private final String urlRoot = ""; //todo
    private final String host = ""; //todo

    private PriceCheckListener listener;
    private AndroidHttpClient client;
    private String userAgent;
    private HttpHost httpHost;
    private BasicHttpContext executionContext;

    public PriceCheckAsync(PriceCheckListener listener) {
        this.listener = listener;
        userAgent = System.getProperty("http.agent");
        if(client == null) {
            client = AndroidHttpClient.newInstance(userAgent);
        }
        httpHost = new HttpHost(host, 80, "http");
        executionContext = new BasicHttpContext();
    }

    @Override
    protected String doInBackground(URL... params) {
        HttpGet request = new HttpGet(urlRoot + params[0]);
        HttpResponse response = null;
        String result = "";
        try {
            response = client.execute(httpHost, request, executionContext);
            HttpEntity entity = response.getEntity();
            //todo Get the string
        } catch (IOException e) {
            Log.e("LuckyPaws", "Caught IOException while trying to get pricing");
            e.printStackTrace();
        }
        client.getConnectionManager().shutdown();
        return result;
    }

    @Override
    protected void onPostExecute(String result){
        if(!TextUtils.isEmpty(result)){
            Map<String, Float> prices = new HashMap<String, Float>();
            //todo parse string
            listener.handleFoundPrice(prices);
        }
    }

    public interface PriceCheckListener {
        public void handleFoundPrice(Map<String, Float> prices);
    }
}
