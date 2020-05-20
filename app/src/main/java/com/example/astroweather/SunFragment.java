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
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SunFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SunFragment extends Fragment {
    private Double lat = 0.0;
    private Double lon = 0.0;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    public SunFragment() {
        // Required empty public constructor
    }

    public void setCoordinates(Double latitude, Double longitude) {
        this.lat = latitude;
        this.lon = longitude;
    }

    public static int getCurrentTimezoneOffset() {
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = Calendar.getInstance(tz);
        return tz.getOffset(cal.getTimeInMillis());
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

    public void getSunInfo() {
        getDateTime();

        AstroDateTime dateTime = new AstroDateTime(year, month, day, hour, minute, second, getCurrentTimezoneOffset(), true);
        AstroCalculator.Location location = new AstroCalculator.Location(lat, lon);
        AstroCalculator calculator = new AstroCalculator(dateTime, location);
        AstroCalculator.SunInfo sunInfo = calculator.getSunInfo();

        TextView sunrise_time_view = getView().findViewById(R.id.sunrise_time);
        AstroDateTime sunrise_time = sunInfo.getSunrise();
        sunrise_time_view.setText(String.format("%02d:%02d", sunrise_time.getHour(), sunrise_time.getMinute()));

        TextView sunrise_azimuth_view = getView().findViewById(R.id.sunrise_azimuth);
        //sunrise_azimuth_view.setText(Double.toString(sunInfo.getAzimuthRise()));
        sunrise_azimuth_view.setText(String.format("%.4f", sunInfo.getAzimuthRise()) + (char) 0x00B0); //(char) 0x00B0 - symbol stopni

        TextView sunset_time_view = getView().findViewById(R.id.sunset_time);
        AstroDateTime sunset_time = sunInfo.getSunset();
        sunset_time_view.setText(String.format("%02d:%02d", sunset_time.getHour(), sunset_time.getMinute()));

        TextView sunset_azimuth_view = getView().findViewById(R.id.sunset_azimuth);
        //String sunset_azimuth = Double.toString(sunInfo.getAzimuthSet());
        sunset_azimuth_view.setText(String.format("%.4f", sunInfo.getAzimuthSet()) + (char) 0x00B0);

        TextView dusk_view = getView().findViewById(R.id.dusk_time);
        AstroDateTime dusk = sunInfo.getTwilightEvening();
        dusk_view.setText(String.format("%02d:%02d", dusk.getHour(), dusk.getMinute()));

        TextView dawn_view = getView().findViewById(R.id.dawn_time);
        AstroDateTime dawn = sunInfo.getTwilightMorning();
        dawn_view.setText(String.format("%02d:%02d", dawn.getHour(), dawn.getMinute()));

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getSunInfo();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sun, container, false);
    }
}
