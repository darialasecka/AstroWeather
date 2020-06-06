package com.example.astroweather;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private Double lat;
    private Double lon;
    private Thread timer;

    private SunFragment sun_fragment;
    private MoonFragment moon_fragment;
    //private MoonFragment weather_fragment;

    private String update_time;
    private int refresh_time;
    private Thread update_info;
    private boolean isMetric;
    private boolean shouldUpdate = false;

    private File weather = null;
    private ViewPager view_pager;
    private ViewPagerAdapter adapter;


    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public void createWeatherData() {
        weather = new File(getCacheDir(),"Weather");
        //System.out.println(weather.getAbsolutePath());
        if (!weather.exists())
            weather.mkdirs();
        String[] locations = weather.list();

        for(String location : locations) {
            String path = null;
            try{
                path = weather.getPath() + "/" + location;
                adapter.addWeatherFragment(path);
                view_pager.setAdapter(adapter);
            } catch (Exception e) {
                if (path != null) new File(path).delete();
                e.printStackTrace();
            }
        }
    }

    public void updateDataFromFile(){
        try {
            System.out.println("need?");
            adapter.updateWeatherFragments();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isConnected()) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Couldn't connect to Internet");
            dialog.setMessage("Weather information may not be accurate. To ensure that information are actual you should connect to Internet.");
            dialog.show();
        }

        Button menu_button = findViewById(R.id.menu_button);
        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        SharedPreferences sharedPref = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        lat = Double.parseDouble(sharedPref.getString("lat", "51.759445"));
        lon = Double.parseDouble(sharedPref.getString("lon", "19.457216"));
        update_time = sharedPref.getString("refresh_time", "1 s");
        isMetric = sharedPref.getBoolean("isMetric", true);
        shouldUpdate = sharedPref.getBoolean("shouldUpdate", false);

        refresh_time = Integer.parseInt(update_time.split(" ")[0]);
        if(update_time.split(" ")[1].startsWith("m")) refresh_time *= 60;

        TextView latitude = findViewById(R.id.latitude);
        latitude.setText("lat: " + lat);
        TextView longitude = findViewById(R.id.longitude);
        longitude.setText("lon: " + lon);

        timer = new Thread() {
            @Override
            public void run() {
            try {
                while (!timer.isInterrupted()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                            Calendar cali = Calendar.getInstance();
                            String time = timeFormat.format(cali.getTimeInMillis());
                            TextView current_time = findViewById(R.id.current_time);
                            current_time.setText(time);
                        }
                    });
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
            }
            }
        };
        timer.start();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction();
        sun_fragment = (SunFragment) fragmentManager.findFragmentById(R.id.s_fragment);
        if (sun_fragment != null) {
            sun_fragment.setCoordinates(lat,lon);
            sun_fragment.getSunInfo();
        }
        moon_fragment = (MoonFragment) fragmentManager.findFragmentById(R.id.m_fragment);
        if (moon_fragment != null) {
            moon_fragment.setCoordinates(lat,lon);
            moon_fragment.getMoonInfo();
        }

        view_pager = findViewById(R.id.view_pager);
        adapter = null;
        if (view_pager != null) {
            adapter = new ViewPagerAdapter(getSupportFragmentManager());
            view_pager.setAdapter(adapter);
            //manage sun anf moon fragments
            sun_fragment = (SunFragment)adapter.instantiateItem(view_pager, 0);
            moon_fragment = (MoonFragment)adapter.instantiateItem(view_pager, 1);
            if (sun_fragment != null)  sun_fragment.setCoordinates(lat,lon);
            if (moon_fragment != null)  moon_fragment.setCoordinates(lat,lon);
        }

        // updates sun and moon info
        update_info = new Thread() {
            @Override
            public void run() {
                try {
                    while (!update_info.isInterrupted()) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println();
                                try {
                                    if(sun_fragment != null) sun_fragment.getSunInfo();
                                    if(moon_fragment != null) moon_fragment.getMoonInfo();
                                    //Log.d("Updated!", Integer.toString(refresh_time));
                                } catch (Exception e) {
                                }
                            }
                        });
                        Thread.sleep(1000 * refresh_time); //sekunda //* refresh_time - czas wybrany przez urzytkownika z uwzględnieniem czy minuty, czy sekundy
                    }
                } catch (InterruptedException e) {}
            }
        };
        update_info.start();

        //manage weather fragments
        createWeatherData();
        //new WeatherManager(this, isMetric).start();

        if (shouldUpdate) {
            System.out.println("yes?");
            WeatherManager update = new WeatherManager(this, isMetric);
            update.start();
            shouldUpdate = false;
            SharedPreferences sharedPref2 = getSharedPreferences("Settings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref2.edit();
            editor.putBoolean("shouldUpdate", false);
            editor.commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.interrupt();
        update_info.interrupt();
    }
}