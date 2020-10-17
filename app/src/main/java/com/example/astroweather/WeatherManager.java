package com.example.astroweather;

import java.io.File;

public class WeatherManager extends Thread {
    private MainActivity activity;
    private boolean isMetric;

    public WeatherManager(MainActivity activity, boolean isMetric){
        this.activity = activity;
        this.isMetric = isMetric;
    }

    @Override
    public void run() {
        File weather = new File(activity.getCacheDir(),"Weather");
        try {
            String[] locations = weather.list();
            for (String location : locations) {
                WeatherConnection connection = new WeatherConnection(location, isMetric, activity);
                connection.execute();
                if (connection.get() != null) {
                    connection.updateFile(location, connection.get(), activity);
                }
                activity.updateDataFromFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
