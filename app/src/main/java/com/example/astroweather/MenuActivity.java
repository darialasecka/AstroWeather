package com.example.astroweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class MenuActivity extends AppCompatActivity {
    private Double lat;
    private Double lon;

    private SharedPreferences sharedPref;
    private Set<String> locations = new HashSet<>();

    Thread timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        //set current preferences on top of the screen
        lat = Double.parseDouble(sharedPref.getString("lat", "51.759445"));
        lon = Double.parseDouble(sharedPref.getString("lon", "19.457216"));

        locations = sharedPref.getStringSet("locations", null);

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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                EditText add_city_input = findViewById(R.id.add_city_input);
                String location_name = add_city_input.getText().toString().toLowerCase();
                try {
                    System.out.println("test");

                    WeatherConnection connection = new WeatherConnection(location_name, true);
                    connection.execute();

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("lat", lat.toString());
                    editor.putString("lon", lon.toString());
                    editor.putString("location", location_name);

                    Set<String> newLocations = new HashSet<>();
                    if(locations != null) newLocations.addAll(locations);
                    newLocations.add(location_name);
                    editor.putStringSet("locations", newLocations);

                    editor.commit();

                    Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        /*final AlertDialog.Builder error = new AlertDialog.Builder(this);
        //final View layout = getLayoutInflater().inflate(R.layout.error_msg, null);
        //error.setView(layout);

        error.setTitle("Error");
        error.setMessage("Incorrect input");*/


        /*Button save_button = findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText lat_text = findViewById(R.id.lat);
                EditText lon_text = findViewById(R.id.city);

                try{
                    lat = Double.parseDouble(lat_text.getText().toString());
                    lon = Double.parseDouble(lon_text.getText().toString());

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
                } catch(Exception e){
                    error.show();
                }
            }
        });*/

        final Spinner spinner = findViewById(R.id.refresh_time);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("1 second");
        arrayList.add("10 seconds");
        arrayList.add("30 seconds");
        arrayList.add("1 minute");
        arrayList.add("5 minutes");
        arrayList.add("10 minutes");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        int refresh_time_position = sharedPref.getInt("refresh_time_position",-1);
        if(refresh_time_position != -1)
            spinner.setSelection(refresh_time_position);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int refresh_time_position = spinner.getSelectedItemPosition();
                String refresh_time = spinner.getSelectedItem().toString();
                sharedPref = getSharedPreferences("Settings",0);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("refresh_time_position", refresh_time_position);
                editor.putString("refresh_time", refresh_time);
                editor.commit();

            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) { }
        });

    }
}
