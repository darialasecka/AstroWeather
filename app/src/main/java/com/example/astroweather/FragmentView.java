package com.example.astroweather;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FragmentView extends AppCompatActivity {
    private Double lat;
    private Double lon;


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



    }
}
