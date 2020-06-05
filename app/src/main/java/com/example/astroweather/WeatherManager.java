package com.example.astroweather;

import android.app.Activity;

import java.io.File;

public class WeatherManager extends Thread {
    Activity activity;

    public WeatherManager(Activity activity){
        this.activity = activity;
    }

    @Override
    public void run() {
        File weather = new File(this.activity.getCacheDir(),"Weather");
        String[] locations = weather.list();
        for (String location : locations) {
            String fullFilePath = null;
            try {
                WeatherConnection connection = new WeatherConnection(location, true, this.activity);
                connection.execute();
                if (connection.get() != null) {
                    connection.addLocation(connection.get(), activity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
