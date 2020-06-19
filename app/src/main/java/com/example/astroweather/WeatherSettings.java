package com.example.astroweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class WeatherSettings extends AppCompatActivity {
    SharedPreferences sharedPref;
    private Double lat;
    private Double lon;
    boolean isMetric;
    String city;

    static boolean changed = false;

    Thread timer;

    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_settings);

        sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        //set current preferences on top of the screen
        lat = Double.parseDouble(sharedPref.getString("lat", "51.759445"));
        lon = Double.parseDouble(sharedPref.getString("lon", "19.457216"));
        isMetric = sharedPref.getBoolean("isMetric", true);
        city = sharedPref.getString("location", "");

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
                String city_name = city_name_input.getText().toString().toLowerCase().replaceAll("\\s", "_");
                if(!city_name.isEmpty()) {
                    try {
                        Toast.makeText(WeatherSettings.this, "Adding city. Please wait", Toast.LENGTH_LONG).show();
                        WeatherConnection connection = new WeatherConnection(city_name, isMetric, WeatherSettings.this);
                        connection.execute();
                        if (connection.get() != null) {
                            try {
                                connection.addLocation(connection.get(), WeatherSettings.this);
                            } catch (Exception e) {
                                Toast.makeText(WeatherSettings.this, "Couldn't add location.", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                        Intent intent = new Intent(WeatherSettings.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(WeatherSettings.this, "Couldn't add location.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(WeatherSettings.this, "Give location you want to add.", Toast.LENGTH_LONG).show();
                }
            }
        });

        final RadioGroup system = findViewById(R.id.system_of_measurements);
        if(isMetric) system.check(R.id.metric);
        else system.check(R.id.imperial);
        system.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isConnected()) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    switch (checkedId) {
                        case R.id.imperial:
                            //do imperial
                            editor.putBoolean("isMetric", false);
                            break;
                        case R.id.metric:
                            //do metric
                            editor.putBoolean("isMetric", true);
                            break;
                    }
                    editor.putBoolean("shouldUpdate", true);
                    editor.commit();
                } else{
                    Toast.makeText(WeatherSettings.this, "Could't change system. Try connecting to the Internet first.", Toast.LENGTH_LONG).show();

                }
            }
        });

        File weather = new File(getCacheDir(),"Weather");
        //System.out.println(weather.getAbsolutePath());
        if (!weather.exists())
            weather.mkdirs();
        String[] locations = weather.list();

        final Spinner spinner = findViewById(R.id.fav_location);
        ArrayList<String> arrayList = new ArrayList<>();

        if(locations.length > 0) {
            System.out.println("test");
            for(String location : locations){
                arrayList.add(location);
            }
        }
        else arrayList.add("No cities");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setSelection(getIndex(spinner, city));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String location = spinner.getSelectedItem().toString();
                if(!location.equals("No cities")){
                    sharedPref = getSharedPreferences("Settings",0);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("location", location);
                    editor.commit();
                }

            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) { }
        });



        Button close_weather_settings = findViewById(R.id.close_weather_settings);
        close_weather_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherSettings.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button sun_moon_settings = findViewById(R.id.sun_moon_setting);
        sun_moon_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherSettings.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button remove = findViewById(R.id.remove_button);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File weather = new File(getCacheDir() + "/Weather");
                String[] locations = weather.list();

                EditText city_name_input = findViewById(R.id.city_name_input);
                String city_name = city_name_input.getText().toString().toLowerCase().replaceAll("\\s", "_");
                if(!city_name.isEmpty()) {
                    for(String location : locations) {
                        if(location.toLowerCase().equals(city_name)){
                            String path = weather.getPath() + "/" + location;
                            new File(path).delete();
                            Toast.makeText(WeatherSettings.this, "Deleted " + location, Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                    Toast.makeText(WeatherSettings.this, "Could't find location to delete", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(WeatherSettings.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(WeatherSettings.this, "Give location you want to remove.", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button remove_all = findViewById(R.id.remove_all_button);
        remove_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File weather = new File(getCacheDir() + "/Weather");
                String[] locations = weather.list();
                for(String location : locations) {
                    String path = weather.getPath() + "/" + location;
                    new File(path).delete();
                }
                Toast.makeText(WeatherSettings.this, "Deleted all locations", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(WeatherSettings.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button update_now = findViewById(R.id.update_now_button);
        update_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("shouldUpdate", true);
                editor.commit();
                Toast.makeText(WeatherSettings.this, "Updated information.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(WeatherSettings.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private int getIndex(Spinner spinner, String myString){
        for (int i = 0; i < spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

}
