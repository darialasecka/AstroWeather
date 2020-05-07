package com.example.astroweather;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FragmentView extends AppCompatActivity {
    private Double lat;
    private Double lon;
    private Thread timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_view);

        Intent this_intent = getIntent();
        lat = this_intent.getDoubleExtra("lat", 0);
        lon = this_intent.getDoubleExtra("lon", 0);

        TextView latitude = findViewById(R.id.latitude);
        latitude.setText("lat: " + Double.toString(lat));
        TextView longitude = findViewById(R.id.longitude);
        longitude.setText("lon: " + Double.toString(lon));




        timer = new Thread() {
            @Override
            public void run() {
                try {
                    while (!timer.isInterrupted()) {
                        Thread.sleep(1000);
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
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        timer.start();

    }
}
