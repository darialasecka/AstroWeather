package com.example.astroweather;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MenuActivity extends AppCompatActivity {
    private Double lat;
    private Double lon;

    private SharedPreferences sharedPref;

    Thread timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        //set current preferences on top of the screen
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

        final AlertDialog.Builder error = new AlertDialog.Builder(this);
        final View layout = getLayoutInflater().inflate(R.layout.error_msg, null);
        error.setView(layout);

        Button save_button = findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText lat_text = findViewById(R.id.lat);
                EditText lon_text = findViewById(R.id.lon);

                //check latitude
                if (lat_text.getText().toString().isEmpty()) {
                    lat = 0.0;
                } else {
                    try {
                        lat = Double.parseDouble(lat_text.getText().toString());
                    } catch (Exception e) {
                        error.show();
                    }
                }

                //check longitude
                if (lon_text.getText().toString().isEmpty()) {
                    lon = 0.0;
                } else {
                    try {
                        lon = Double.parseDouble(lon_text.getText().toString());
                    } catch (Exception e) {
                        error.show();
                    }
                }

                //validate coordinates
                if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                    error.show();
                } else {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("lat", lat.toString());
                    editor.putString("lon", lon.toString());
                    editor.commit();

                    Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        final Spinner spinner = findViewById(R.id.refresh_time);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("5 minutes");
        arrayList.add("10 minutes");
        arrayList.add("15 minutes");
        arrayList.add("20 minutes");
        arrayList.add("25 minutes");
        arrayList.add("30 minutes");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        int refresh_time = sharedPref.getInt("refresh_time",-1);
        if(refresh_time != -1)
            spinner.setSelection(refresh_time);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int refresh_time = spinner.getSelectedItemPosition();
                sharedPref = getSharedPreferences("Settings",0);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("refresh_time", refresh_time);
                editor.commit();
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) { }
        });

    }
}
