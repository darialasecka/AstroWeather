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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

public class MenuActivity extends AppCompatActivity {
    private Double lat;
    private Double lon;
    boolean isMetric;
    private String location;

    private SharedPreferences sharedPref;

    Thread timer;

    private void manage_fav(String location){
        File weather = new File(getCacheDir(),"Weather");
        location = location.replaceAll("\\s", "_");
        try{
            String path = weather.getPath() + "/" + location;
            String content = new String(Files.readAllBytes(Paths.get(path)));
            JSONObject jsonObject = new JSONObject(content);

            String lat = jsonObject.getJSONObject("location").get("lat").toString();
            String lon = jsonObject.getJSONObject("location").get("long").toString();

            String tz = jsonObject.getJSONObject("location").get("timezone_id").toString();
            TimeZone timeZone = TimeZone.getTimeZone(tz);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("lat", lat);
            editor.putString("lon", lon);
            editor.putInt("timeZone", timeZone.getRawOffset() + timeZone.getDSTSavings());
            editor.putString("location", location);
            editor.commit();

            Toast.makeText(MenuActivity.this, "Set " + location + " as favourite", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

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

        final AlertDialog.Builder error = new AlertDialog.Builder(this);
        //final View layout = getLayoutInflater().inflate(R.layout.error_msg, null);
        //error.setView(layout);
        error.setTitle("Error");
        error.setMessage("Incorrect input");


        Button save_button = findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText lat_text = findViewById(R.id.lat);
                EditText lon_text = findViewById(R.id.lon);

                try{
                    lat = Double.parseDouble(lat_text.getText().toString());
                } catch(Exception e) { }
                try{
                    lon = Double.parseDouble(lon_text.getText().toString());
                } catch(Exception e) { }

                //validate coordinates
                if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                    error.show();
                } else {
                    //weather//jeśli jest między -180 - 0 to dodaje ileś, a jeśli 60 - 180 to odejmuje,
                    //-90 - 0 dodaje, 0 - 90 odejmuje

                    try {
                        Toast.makeText(MenuActivity.this, "Adding location. Please wait", Toast.LENGTH_LONG).show();

                        WeatherConnectionCoords connection = new WeatherConnectionCoords(lat, lon, isMetric, MenuActivity.this);
                        connection.execute();
                        boolean empty_json = new JSONObject(connection.get()).getJSONObject("location").length() == 0;
                        int counter = 0;
                        int offset = 5;
                        while(empty_json) { //I konw this system isn't the best, but it works, kind of...
                            counter ++;
                            if(counter % 10 == 0) offset += 5;
                            if(lat > 0 && lat <= 90) lat -= offset;
                            else lat += offset;

                            if(lon > 0 && lon <= 180) lon -= offset;
                            else lon += offset;

                            System.out.println(lat + " " + lon);
                            connection = new WeatherConnectionCoords(lat, lon, isMetric, MenuActivity.this);
                            connection.execute();

                            empty_json = new JSONObject(connection.get()).getJSONObject("location").length() == 0;
                        }
                        //here we know that some location exists
                        try {
                            location = connection.addLocation(connection.get(), MenuActivity.this);
                            System.out.println(location);
                        } catch (Exception e) {
                            Toast.makeText(MenuActivity.this, "Couldn't add location.", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        /*WeatherConnectionCoords connection = new WeatherConnectionCoords(lat, lon, isMetric, MenuActivity.this);
                        connection.execute();

                        if (connection.get() != null) {
                            try {
                                location = connection.addLocation(connection.get(), MenuActivity.this);
                                System.out.println(location);
                            } catch (Exception e) {
                                Toast.makeText(MenuActivity.this, "Couldn't add location.", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }*/
                    } catch (ExecutionException e) {
                        Toast.makeText(MenuActivity.this, "Couldn't add location.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    File weather = new File(getCacheDir(),"Weather");
                    if (!weather.exists())
                        weather.mkdirs();
                    if(weather.list().length == 1) manage_fav(location);

                    Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        Button weather_settings = findViewById(R.id.weather_settings);
        weather_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, WeatherSettings.class);
                startActivity(intent);
                finish();
            }
        });

        final Spinner spinner = findViewById(R.id.refresh_time);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("1 second");
        arrayList.add("5 seconds");
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

        Button close_menu_settings = findViewById(R.id.close_menu_settings);
        close_menu_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
