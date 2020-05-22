package com.example.astroweather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    private long countSynodicMonthDay() { //synodic month - time between two new moons, lasts about 29 days
        //for counting previous new moon
        AstroDateTime dateTime = new AstroDateTime(year, month, day - 29, hour, minute, second, getCurrentTimezoneOffset(), true);
        AstroCalculator.Location location = new AstroCalculator.Location(lat, lon);
        AstroCalculator calculator = new AstroCalculator(dateTime, location);
        AstroDateTime prev_new_moon = calculator.getMoonInfo().getNextNewMoon();
        String prev_new_moon_string = String.format("%02d.%02d.%04d", prev_new_moon.getDay(), prev_new_moon.getMonth(), prev_new_moon.getYear());
        String today = String.format("%02d.%02d.%04d", day, month, year);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        Date firstDate = null;
        Date secondDate = null;
        try {
            firstDate = sdf.parse(prev_new_moon_string);
            secondDate = sdf.parse(today);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public void getMoonInfo() {
        getDateTime();

        AstroDateTime dateTime = new AstroDateTime(year, month, day, hour, minute, second, getCurrentTimezoneOffset(), true);
        AstroCalculator.Location location = new AstroCalculator.Location(lat, lon);
        AstroCalculator calculator = new AstroCalculator(dateTime, location);
        AstroCalculator.MoonInfo moonInfo = calculator.getMoonInfo();

        try {
            TextView moonrise_time_view = getView().findViewById(R.id.moonrise_time);
            AstroDateTime moonrise_time = moonInfo.getMoonrise();
            moonrise_time_view.setText(String.format("%02d:%02d", moonrise_time.getHour(), moonrise_time.getMinute()));
        } catch (Exception e) {
            TextView moonrise_time_view = getView().findViewById(R.id.moonrise_time);
            moonrise_time_view.setText("Error");
        }

        try {
            TextView moonset_time_view = getView().findViewById(R.id.moonset_time);
            AstroDateTime moonset_time = moonInfo.getMoonset();
            moonset_time_view.setText(String.format("%02d:%02d", moonset_time.getHour(), moonset_time.getMinute()));
        } catch (Exception e) {}

        try {
            TextView nearest_new_moon = getView().findViewById(R.id.new_moon);
            AstroDateTime new_moon = moonInfo.getNextNewMoon();
            String new_moon_text = String.format("%02d.%02d.%04d", new_moon.getDay(), new_moon.getMonth(), new_moon.getYear());
            nearest_new_moon.setText(new_moon_text);
        } catch (Exception e) {
            TextView nearest_new_moon = getView().findViewById(R.id.new_moon);
            nearest_new_moon.setText("Error");
        }

        try {
            TextView nearest_full_moon = getView().findViewById(R.id.full_moon);
            AstroDateTime full_moon = moonInfo.getNextFullMoon();
            String full_moon_text = String.format("%02d.%02d.%04d", full_moon.getDay(), full_moon.getMonth(), full_moon.getYear());
            nearest_full_moon.setText(full_moon_text);
        } catch (Exception e) {
            TextView nearest_full_moon = getView().findViewById(R.id.full_moon);
            nearest_full_moon.setText("Error");
        }

        try {
            TextView moon_phase_view = getView().findViewById(R.id.moon_phase);
            moon_phase_view.setText(String.format("%.2f%%", moonInfo.getIllumination() * 100));
        } catch (Exception e) {
            TextView moon_phase_view = getView().findViewById(R.id.moon_phase);
            moon_phase_view.setText("Error");
        }

        try {
            TextView sunset_azimuth_view = getView().findViewById(R.id.synodic_month_day);
            String lunar_day = Integer.toString((int) countSynodicMonthDay());
            sunset_azimuth_view.setText(lunar_day);
        } catch (Exception e) {
            TextView sunset_azimuth_view = getView().findViewById(R.id.synodic_month_day);
            sunset_azimuth_view.setText("Error");
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMoonInfo();
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
