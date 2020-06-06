package com.example.astroweather;

// Copyright 2019 Oath Inc. Licensed under the terms of the zLib license see https://opensource.org/licenses/Zlib for terms.

import android.app.Activity;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherConnection extends AsyncTask <Void, Void, String> {
    OkHttpClient client = new OkHttpClient();

    final String appId = Secret.getAppId();
    final String consumerKey = Secret.getConsumerKey();
    final String consumerSecret = Secret.getConsumerSecret();
    final String url = "https://weather-ydn-yql.media.yahoo.com/forecastrss";
    String authorizationLine;

    String location = "lodz";
    Boolean isMetric = true;
    Activity activity = null;

    private String getResponse(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", authorizationLine)
                .header("X-Yahoo-App-Id", appId)
                .header("Content-Type", "application/json")
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        String response = "";
        String requestURL = url + "?location=" + location + "&format=json";
        try {
            if (isMetric)
                requestURL += "&u=c";
            //Thread.sleep(2000);
            Thread.sleep(10);
            response = getResponse(requestURL);
            System.out.println(requestURL);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        System.out.println(response);
        return response;
    }

    public WeatherConnection(String location, boolean isMetric, Activity activity) throws Exception {
        this.isMetric = isMetric;
        this.activity = activity;
        this.location = location.toLowerCase();
        long timestamp = new Date().getTime() / 1000;
        byte[] nonce = new byte[32];
        Random rand = new Random();
        rand.nextBytes(nonce);
        //System.out.println("oauth " + new String(nonce).replaceAll("\\W", ""));
        String oauthNonce = "w2gC09BO"; //new String(nonce).replaceAll("\\W", "");

        List<String> parameters = new ArrayList<>();
        parameters.add("oauth_consumer_key=" + consumerKey);
        parameters.add("oauth_nonce=" + oauthNonce);
        parameters.add("oauth_signature_method=HMAC-SHA1");
        parameters.add("oauth_timestamp=" + timestamp);
        parameters.add("oauth_version=1.0");
        // Make sure value is encoded
        parameters.add("location=" + URLEncoder.encode(this.location, "UTF-8"));
        parameters.add("format=json");
        //System.out.println(this.location + " " + isMetric);
        if(this.isMetric)
            parameters.add("u=c");
        Collections.sort(parameters);

        StringBuffer parametersList = new StringBuffer();
        for (int i = 0; i < parameters.size(); i++) {
            parametersList.append(((i > 0) ? "&" : "") + parameters.get(i));
        }

        String signatureString = "GET&" +
                URLEncoder.encode(url, "UTF-8") + "&" +
                URLEncoder.encode(parametersList.toString(), "UTF-8");

        String signature = null;
        try {
            SecretKeySpec signingKey = new SecretKeySpec((consumerSecret + "&").getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHMAC = mac.doFinal(signatureString.getBytes());
            Encoder encoder = Base64.getEncoder();
            signature = encoder.encodeToString(rawHMAC);
        } catch (Exception e) {
            System.err.println("Unable to append signature");
            System.exit(0);
        }

        authorizationLine = "OAuth " +
                "oauth_consumer_key=\"" + consumerKey + "\", " +
                "oauth_nonce=\"" + oauthNonce + "\", " +
                "oauth_timestamp=\"" + timestamp + "\", " +
                "oauth_signature_method=\"HMAC-SHA1\", " +
                "oauth_signature=\"" + signature + "\", " +
                "oauth_version=\"1.0\"";
    }



    public String addLocation(String json, Activity activity) throws Exception {
        JSONObject object = new JSONObject(json);
        JSONObject locationObject = object.getJSONObject("location");
        String city_name = locationObject.get("city").toString();
        if (isMetric)
            object.put("unit", "metric");
        else
            object.put("unit", "imperial");

        String filename = city_name.replaceAll("\\s","_");
        PrintWriter out = new PrintWriter(new FileWriter(activity.getCacheDir().toString() + "/Weather/" + filename));
        out.write(object.toString());
        out.close();
        return city_name;
    }

    public String updateFile(String filename, String jsonContent, Activity activity) throws Exception {
        JSONObject object = new JSONObject(jsonContent);
        JSONObject locationObject = object.getJSONObject("location");
        String location_name = locationObject.get("city").toString();
        String filepath = activity.getCacheDir().toString() + "/Weather/" + filename;
        File f = new File(filepath);
        if (f.exists()) {
            PrintWriter out = new PrintWriter(new FileWriter(filepath));
            out.write(object.toString());
            out.close();
            return location_name;
        }
        throw new RuntimeException("File " + filepath + " does not exists");
    }

}