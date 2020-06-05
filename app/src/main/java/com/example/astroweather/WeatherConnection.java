package com.example.astroweather;

// Copyright 2019 Oath Inc. Licensed under the terms of the zLib license see https://opensource.org/licenses/Zlib for terms.

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
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

/*import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;*/

public class WeatherConnection extends AsyncTask {
    OkHttpClient client = new OkHttpClient();

    final String appId = Secret.getAppId();
    final String consumerKey = Secret.getConsumerKey();
    final String consumerSecret = Secret.getConsumerSecret();
    final String url = "https://weather-ydn-yql.media.yahoo.com/forecastrss";
    String authorizationLine;

    String location = "lodz";
    Boolean isCelsius = true;

    public String getResponse(String url) throws IOException {
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
    protected String doInBackground(Object[] objects) {

        String response = "";
        try {
            if (isCelsius)
                response = getResponse(url + "?location=" + location + "&format=json&u=c");
            else
                response = getResponse(url + "?location=" + location + "&format=json");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        System.out.println(response);
        return response;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public WeatherConnection(String location, boolean isCelsius) throws Exception {
        this.isCelsius = isCelsius;
        //this.mainActivity = mainActivity;
        this.location = location.toLowerCase();
        long timestamp = new Date().getTime() / 1000;
        byte[] nonce = new byte[32];
        Random rand = new Random();
        rand.nextBytes(nonce);
        System.out.println("oauth " + new String(nonce).replaceAll("\\W", ""));
        String oauthNonce = "w2gC09BO"; //new String(nonce).replaceAll("\\W", "");

        List<String> parameters = new ArrayList<>();
        parameters.add("oauth_consumer_key=" + consumerKey);
        parameters.add("oauth_nonce=" + oauthNonce);
        parameters.add("oauth_signature_method=HMAC-SHA1");
        parameters.add("oauth_timestamp=" + timestamp);
        parameters.add("oauth_version=1.0");
        // Make sure value is encoded
        parameters.add("location=" + URLEncoder.encode(location, "UTF-8"));
        parameters.add("format=json");
        if(this.isCelsius)
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
}