package com.example.astroweather;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    /*private Double lat;
    private Double lon;*/

    private Double lat;
    private Double lon;
    private Thread timer;

    private SunFragment sun_fragment;
    private MoonFragment moon_fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        /*FragmentManager fragmentManager = getSupportFragmentManager();
        sun_fragment = (SunFragment)fragmentManager.findFragmentById(R.id.sun_fragment);
        moon_fragment = (MoonFragment)fragmentManager.findFragmentById(R.id.moon_fragment);*/


        ViewPager view_pager = findViewById(R.id.view_pager);
        if (view_pager != null) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            view_pager.setAdapter(adapter);
            sun_fragment = (SunFragment)adapter.instantiateItem(view_pager, 0);
            if(sun_fragment != null) {
                //sun_fragment.setCoordinates(lat, lon);
            }
            moon_fragment = (MoonFragment)adapter.instantiateItem(view_pager, 1);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            timer.stop();
        } catch (Exception e) {}
    }



        /*final AlertDialog.Builder error_dialog = new AlertDialog.Builder(this);
        TextView error_msg = new TextView(this);
        error_msg.setText("Incorrect input");
        error_msg.setGravity(Gravity.CENTER_HORIZONTAL);
        error_dialog.setView(error_msg);

        Button ok_button = findViewById(R.id.ok_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText lat_text = findViewById(R.id.lat);
                EditText lon_text = findViewById(R.id.lon);

                lat = 0.0;
                lon = 0.0;

                Intent intent = new Intent(MainActivity.this, FragmentViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", lat);
                bundle.putDouble("lon", lon);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();

                /*try {
                    lat = Double.parseDouble(lat_text.getText().toString());
                    lon = Double.parseDouble(lon_text.getText().toString());
                    if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                        error_dialog.show();
                    } else {
                        Intent intent = new Intent(MainActivity.this, FragmentViewActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putDouble("lat", lat);
                        bundle.putDouble("lon", lon);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    error_dialog.show();
                }*/
            /*}
        });

        Spinner spinner = findViewById(R.id.refresh_time);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("5 minutes");
        arrayList.add("10 minutes");
        arrayList.add("15 minutes");
        arrayList.add("20 minutes");
        arrayList.add("25 minutes");
        arrayList.add("30 minutes");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        /*
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                string tutorialsName = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected: " + tutorialsName, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });*/
    //}
}
