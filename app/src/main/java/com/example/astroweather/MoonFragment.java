package com.example.astroweather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoonFragment extends Fragment {
    private Double lat = 0.0;
    private Double lon = 0.0;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    public MoonFragment() {
        // Required empty public constructor
    }

    public void setCoordinates(Double latitude, Double longitude) {
        this.lat = latitude;
        this.lon = longitude;
    }

    public static int getCurrentTimezoneOffset() {
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());
        return (int)TimeUnit.MICROSECONDS.toHours(offsetInMillis);

    }

    private void getDateTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
    }

    public void getMoonInfo() {
        getDateTime();

        AstroDateTime dateTime = new AstroDateTime(year, month, day, hour, minute, second, getCurrentTimezoneOffset(), true);
        AstroCalculator.Location location = new AstroCalculator.Location(lat, lon);
        AstroCalculator calculator = new AstroCalculator(dateTime, location);
        AstroCalculator.MoonInfo moonInfo = calculator.getMoonInfo();

        TextView moonrise_time_view = getView().findViewById(R.id.moonrise_time);
        AstroDateTime moonrise_time = moonInfo.getMoonrise();
        moonrise_time_view.setText(String.format("%02d:%02d", moonrise_time.getHour(), moonrise_time.getMinute()));

        TextView moonset_time_view = getView().findViewById(R.id.moonset_time);
        AstroDateTime moonset_time = moonInfo.getMoonset();
        moonset_time_view.setText(String.format("%02d:%02d", moonset_time.getHour(), moonset_time.getMinute()));

        TextView nearest_new_moon = getView().findViewById(R.id.new_moon);
        AstroDateTime new_moon = moonInfo.getNextNewMoon();
        nearest_new_moon.setText(String.format("%02d.%02d.%04d", new_moon.getDay(), new_moon.getMonth(), new_moon.getYear()));

        TextView nearest_full_moon = getView().findViewById(R.id.full_moon);
        AstroDateTime full_moon = moonInfo.getNextFullMoon();
        nearest_full_moon.setText(String.format("%02d.%02d.%04d", full_moon.getDay(), full_moon.getMonth(), full_moon.getYear()));

        TextView moon_phase_view = getView().findViewById(R.id.moon_phase);
        moon_phase_view.setText(String.format("%.2f%%", moonInfo.getIllumination() * 100));

        TextView sunset_azimuth_view = getView().findViewById(R.id.synodic_month_day);
        String lunar_day = Integer.toString((int)moonInfo.getAge());
        sunset_azimuth_view.setText(lunar_day);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMoonInfo();
    }

    public static MoonFragment newInstance(String param1, String param2) {
        MoonFragment fragment = new MoonFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_moon, container, false);
    }
}
