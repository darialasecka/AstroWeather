package com.example.astroweather;

import androidx.appcompat.app.AppCompatActivity;

public class FragmentViewActivity extends AppCompatActivity {

/*

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_view_activity);

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

        *//*FragmentManager fragmentManager = getSupportFragmentManager();
        sun_fragment = (SunFragment)fragmentManager.findFragmentById(R.id.sun_fragment);
        moon_fragment = (MoonFragment)fragmentManager.findFragmentById(R.id.moon_fragment);*//*


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
    }*/
}
