package com.example.astroweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WeatherSettings extends AppCompatActivity {
    SharedPreferences sharedPref;
    private Double lat;
    private Double lon;
    boolean isMetric;

    Thread timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_settings);

        sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        //set current preferences on top of the screen
        lat = Double.parseDouble(sharedPref.getString("lat", "51.759445"));
        lon = Double.parseDouble(sharedPref.getString("lon", "19.457216"));
        isMetric = sharedPref.getBoolean("isMetric", true);

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

        Button add_city_button = findViewById(R.id.add_city_button);
        add_city_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText city_name_input = findViewById(R.id.city_name_input);
                String city_name = city_name_input.getText().toString().toLowerCase();
                if(!city_name.isEmpty()) {
                    try {
                        WeatherConnection connection = new WeatherConnection(city_name, isMetric, WeatherSettings.this);
                        connection.execute();
                        if (connection.get() != null) {
                            try {
                                connection.addLocation(connection.get(), WeatherSettings.this);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Intent intent = new Intent(WeatherSettings.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        RadioGroup system = findViewById(R.id.system_of_measurements);
        if(isMetric) system.check(R.id.metric);
        else system.check(R.id.imperial);
        system.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor editor = sharedPref.edit();

                switch (checkedId){
                    case R.id.imperial:
                        //do imperial
                        System.out.println("imperial");
                        editor.putBoolean("isMetric", false);

                        break;
                    case R.id.metric:
                        //do metric
                        System.out.println("metric");
                        editor.putBoolean("isMetric", true);
                        break;
                }

                editor.commit();

            }
        });

    }
}
