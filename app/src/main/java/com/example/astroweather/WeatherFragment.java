package com.example.astroweather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherFragment extends Fragment {

    private String path;
    private JSONObject jsonObject;
    private boolean isMetric;

    //units
    private String degrees;
    private String distance;
    private String speed;
    private String pressure;


    public WeatherFragment(String path, boolean isMetric) throws Exception {
        this.path = path;
        String content = new String(Files.readAllBytes(Paths.get(path)));
        this.jsonObject = new JSONObject(content);
        this.isMetric = isMetric;
    }

    public void updateData() throws Exception{
        String content = new String(Files.readAllBytes(Paths.get(path)));
        this.jsonObject = new JSONObject(content);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            update();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void update() throws JSONException {
        //TODO: somehow save utils type in json file and get it"
        if(isMetric){
             degrees = (char) 0x00B0 + "C";
             distance = " km";
             speed = " km/h";
             pressure = " mbar";
        }
        else {
            degrees = (char) 0x00B0 + "F";
            distance = " mi";
            speed = " mph";
            pressure = " inHg";
        }

        //TODO: add label for county
        //TODO: add changing lat i lon from json
        //city
        JSONObject location = jsonObject.getJSONObject("location");

        TextView city_name = getView().findViewById(R.id.city_name);
        city_name.setText(location.get("city").toString());

        //cordinates - doesn't work
        /*TextView latitude = getView().findViewById(R.id.latitude);
        latitude.setText("lat: " + location.get("lat").toString());

        TextView longitude = getView().findViewById(R.id.longitude);
        longitude.setText("lon: " + location.get("lat").toString());*/

        //today
        JSONObject current_observation = jsonObject.getJSONObject("current_observation");

        JSONObject wind = current_observation.getJSONObject("wind");
        JSONObject atmosphere = current_observation.getJSONObject("atmosphere");
        JSONObject condition = current_observation.getJSONObject("condition");

        //wind
        //TODO: add textviews with descriptions (ex. one textview with word "Chill: ", second with number)
        TextView wind_chill = getView().findViewById(R.id.chill_label);
        wind_chill.setText("Chill: " + wind.get("chill").toString() + (char) 0x00B0);

        TextView wind_direction = getView().findViewById(R.id.direction_label);
        wind_direction.setText("Direction: " + wind.get("direction").toString() + (char) 0x00B0);

        TextView wind_speed = getView().findViewById(R.id.speed_label);
        wind_speed.setText("Speed: " + wind.get("speed").toString() + speed);

        //atmosphere
        TextView atmo_humidity = getView().findViewById(R.id.humidity_label);
        atmo_humidity.setText("Humidity: " + atmosphere.get("humidity").toString() + "%");

        TextView atmo_visibility = getView().findViewById(R.id.visibility_label);
        atmo_visibility.setText("Visibility: " +  atmosphere.get("visibility").toString() + distance);

        TextView atmo_pressure = getView().findViewById(R.id.pressure_label);
        atmo_pressure.setText("Pressure: " + atmosphere.get("pressure").toString() + pressure);

        //condition
        TextView today_temp = getView().findViewById(R.id.today_temp_label);
        today_temp.setText(condition.get("temperature").toString() + degrees); //for now it will always show celsius

        TextView today_cond = getView().findViewById(R.id.today_cond_label);
        today_cond.setText(condition.get("text").toString());

        //forecast
        JSONArray forecasts = jsonObject.getJSONArray("forecasts");
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM");
        DateFormat todayTomorrowFormat = new SimpleDateFormat("dd MMM");

        TextView today_label = getView().findViewById(R.id.today_label);
        Date date = new Date(Long.parseLong(forecasts.getJSONObject(0).get("date").toString()) * 1000);
        today_label.setText("Today, " + todayTomorrowFormat.format(date));
        TextView today_temp2 = getView().findViewById(R.id.today_temp_label2);
        today_temp2.setText(forecasts.getJSONObject(0).get("high").toString() + degrees + " / " + forecasts.getJSONObject(0).get("low").toString() + degrees);
        TextView today_cond2 = getView().findViewById(R.id.today_cond_label2);
        today_cond2.setText(forecasts.getJSONObject(0).get("text").toString());

        TextView tomorrow_label = getView().findViewById(R.id.tomorrow_label);
        date = new Date(Long.parseLong(forecasts.getJSONObject(1).get("date").toString()) * 1000);
        tomorrow_label.setText("Tomorrow, " + todayTomorrowFormat.format(date));
        TextView tomorrow_temp_label = getView().findViewById(R.id.tomorrow_temp_label);
        tomorrow_temp_label.setText(forecasts.getJSONObject(1).get("high").toString() + degrees + " / " + forecasts.getJSONObject(1).get("low").toString() + degrees);
        TextView tomorrow_cond_label = getView().findViewById(R.id.tomorrow_cond_label);
        tomorrow_cond_label.setText(forecasts.getJSONObject(1).get("text").toString());

        TextView day3_label = getView().findViewById(R.id.day3_label);
        date = new Date(Long.parseLong(forecasts.getJSONObject(2).get("date").toString()) * 1000);
        day3_label.setText(dateFormat.format(date));
        TextView day3_temp_label = getView().findViewById(R.id.day3_temp_label);
        day3_temp_label.setText(forecasts.getJSONObject(2).get("high").toString() + degrees + " / " + forecasts.getJSONObject(2).get("low").toString() + degrees);
        TextView day3_cond_label = getView().findViewById(R.id.day3_cond_label);
        day3_cond_label.setText(forecasts.getJSONObject(2).get("text").toString());

        TextView day4_label = getView().findViewById(R.id.day4_label);
        date = new Date(Long.parseLong(forecasts.getJSONObject(3).get("date").toString()) * 1000);
        day4_label.setText(dateFormat.format(date));
        TextView day4_temp_label = getView().findViewById(R.id.day4_temp_label);
        day4_temp_label.setText(forecasts.getJSONObject(3).get("high").toString() + degrees + " / " + forecasts.getJSONObject(3).get("low").toString() + degrees);
        TextView day4_cond_label = getView().findViewById(R.id.day4_cond_label);
        day4_cond_label.setText(forecasts.getJSONObject(3).get("text").toString());

        TextView day5_label = getView().findViewById(R.id.day5_label);
        date = new Date(Long.parseLong(forecasts.getJSONObject(4).get("date").toString()) * 1000);
        day5_label.setText(dateFormat.format(date));
        TextView day5_temp_label = getView().findViewById(R.id.day5_temp_label);
        day5_temp_label.setText(forecasts.getJSONObject(4).get("high").toString() + degrees + " / " + forecasts.getJSONObject(4).get("low").toString() + degrees);
        TextView day5_cond_label = getView().findViewById(R.id.day5_cond_label);
        day5_cond_label.setText(forecasts.getJSONObject(4).get("text").toString());

        TextView day6_label = getView().findViewById(R.id.day6_label);
        date = new Date(Long.parseLong(forecasts.getJSONObject(5).get("date").toString()) * 1000);
        day6_label.setText(dateFormat.format(date));
        TextView day6_temp_label = getView().findViewById(R.id.day6_temp_label);
        day6_temp_label.setText(forecasts.getJSONObject(5).get("high").toString() + degrees + " / " + forecasts.getJSONObject(5).get("low").toString() + degrees);
        TextView day6_cond_label = getView().findViewById(R.id.day6_cond_label);
        day6_cond_label.setText(forecasts.getJSONObject(5).get("text").toString());

        TextView day7_label = getView().findViewById(R.id.day7_label);
        date = new Date(Long.parseLong(forecasts.getJSONObject(6).get("date").toString()) * 1000);
        day7_label.setText(dateFormat.format(date));
        TextView day7_temp_label = getView().findViewById(R.id.day7_temp_label);
        day7_temp_label.setText(forecasts.getJSONObject(6).get("high").toString() + degrees + " / " + forecasts.getJSONObject(6).get("low").toString() + degrees);
        TextView day7_cond_label = getView().findViewById(R.id.day7_cond_label);
        day7_cond_label.setText(forecasts.getJSONObject(6).get("text").toString());

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }
}
