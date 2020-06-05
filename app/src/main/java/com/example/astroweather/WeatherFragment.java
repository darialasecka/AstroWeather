package com.example.astroweather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;

public class WeatherFragment extends Fragment {

    String path;
    JSONObject jsonObject;

    public WeatherFragment(String path, boolean isMetric) throws Exception {
        this.path = path;
        String content = new String(Files.readAllBytes(Paths.get(path)));
        this.jsonObject = new JSONObject(content);
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
        //TODO: add label for county
        //TODO: add changing lat i lon from json
        //city
        JSONObject location = jsonObject.getJSONObject("location");

        TextView city_name = getView().findViewById(R.id.city_name);
        city_name.setText(location.get("city").toString());

        //today
        JSONObject current_observation = jsonObject.getJSONObject("current_observation");

        JSONObject wind = current_observation.getJSONObject("wind");
        JSONObject atmosphere = current_observation.getJSONObject("atmosphere");
        JSONObject condition = current_observation.getJSONObject("condition");

        //wind
        //TODO: add textviews with descriptions (ex. one textview with word "Chill: ", second with number)
        TextView wind_chill = getView().findViewById(R.id.chill_label);
        wind_chill.setText("Chill: " + wind.get("chill").toString());

        TextView wind_direction = getView().findViewById(R.id.direction_label);
        wind_direction.setText("Direction: " + wind.get("direction").toString());

        TextView wind_speed = getView().findViewById(R.id.speed_label);
        wind_speed.setText("Speed: " + wind.get("speed").toString());

        //atmosphere
        TextView atmo_humidity = getView().findViewById(R.id.humidity_label);
        atmo_humidity.setText("Humidity: " + atmosphere.get("humidity").toString());

        TextView atmo_visibility = getView().findViewById(R.id.visibility_label);
        atmo_visibility.setText("Visibility: " + atmosphere.get("visibility").toString());

        TextView atmo_pressure = getView().findViewById(R.id.pressure_label);
        atmo_pressure.setText("Pressure: " + atmosphere.get("pressure").toString());

        //condition
        //TODO: manage all types of units units
        TextView today_temp = getView().findViewById(R.id.today_temp_label);
        today_temp.setText(condition.get("temperature").toString() + (char) 0x00B0 + "C"); //for now it will always show celsius

        TextView today_cond = getView().findViewById(R.id.today_cond_label);
        today_cond.setText(condition.get("text").toString());


        //TODO: get forecast data
        //forecast


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
