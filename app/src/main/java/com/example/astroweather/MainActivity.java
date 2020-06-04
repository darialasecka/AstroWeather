package com.example.astroweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private Double lat;
    private Double lon;
    private Thread timer;

    private SunFragment sun_fragment;
    private MoonFragment moon_fragment;

    private String update_time;
    private int refresh_time;
    private Thread update_info;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            WeatherConnection weather = new WeatherConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("test");


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

        ViewPager view_pager = findViewById(R.id.view_pager);
        if (view_pager != null) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            view_pager.setAdapter(adapter);
            sun_fragment = (SunFragment)adapter.instantiateItem(view_pager, 0);
            moon_fragment = (MoonFragment)adapter.instantiateItem(view_pager, 1);
            if (sun_fragment != null)  sun_fragment.setCoordinates(lat,lon);
            if (moon_fragment != null)  moon_fragment.setCoordinates(lat,lon);
        }

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

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.interrupt();
        update_info.interrupt();
    }
}