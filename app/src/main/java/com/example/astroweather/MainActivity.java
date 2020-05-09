package com.example.astroweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button ok_button = findViewById(R.id.menu_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPref = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        lat = Double.parseDouble(sharedPref.getString("lat", "0"));
        lon = Double.parseDouble(sharedPref.getString("lon", "0"));

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

        /*FragmentManager fragmentManager = getSupportFragmentManager();
        sun_fragment = (SunFragment)fragmentManager.findFragmentById(R.id.sun_fragment);
        moon_fragment = (MoonFragment)fragmentManager.findFragmentById(R.id.moon_fragment);*/


        ViewPager view_pager = findViewById(R.id.view_pager);
        if (view_pager != null) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            view_pager.setAdapter(adapter);
            sun_fragment = (SunFragment)adapter.instantiateItem(view_pager, 0);
            if(sun_fragment != null) {
                sun_fragment.setCoordinates(lat, lon);
            }
            moon_fragment = (MoonFragment)adapter.instantiateItem(view_pager, 1);
            if(moon_fragment != null) {
                moon_fragment.setCoordinates(lat, lon);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            timer.stop();
        } catch (Exception e) {}
    }
}
