package com.example.astroweather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherFragment extends Fragment {

    private String path;
    private JSONObject jsonObject;

    //units
    private String degrees;
    private String distance;
    private String speed;
    private String pressure;


    public WeatherFragment(String path) throws Exception {
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
        update();
    }

    void update() {
        try{
            //System.out.println("json " + jsonObject.get("units").toString());
            if(jsonObject.get("units").toString().equals("metric")) {
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

            //city
            JSONObject location = jsonObject.getJSONObject("location");
            //System.out.println(jsonObject);

            TextView city_name = getView().findViewById(R.id.city_label);
            city_name.setText(location.get("city").toString());

            TextView country_name = getView().findViewById(R.id.country_label);
            country_name.setText(location.get("country").toString());

            //today
            JSONObject current_observation = jsonObject.getJSONObject("current_observation");

            JSONObject wind = current_observation.getJSONObject("wind");
            JSONObject atmosphere = current_observation.getJSONObject("atmosphere");
            JSONObject condition = current_observation.getJSONObject("condition");

            //wind
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

            /*TextView today_cond = getView().findViewById(R.id.today_cond_label);
            today_cond.setText(condition.get("text").toString());*/
            setImageOnCondition(Integer.parseInt(condition.get("code").toString()), R.id.today_cond_img);

            //forecast
            JSONArray forecasts = jsonObject.getJSONArray("forecasts");
            DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM");
            DateFormat todayTomorrowFormat = new SimpleDateFormat("dd MMM");

            TextView today_label = getView().findViewById(R.id.today_label);
            Date date = new Date(Long.parseLong(forecasts.getJSONObject(0).get("date").toString()) * 1000);
            today_label.setText("Today, " + todayTomorrowFormat.format(date));
            TextView today_temp2 = getView().findViewById(R.id.today_temp_label2);
            today_temp2.setText(forecasts.getJSONObject(0).get("high").toString() + degrees + " / " + forecasts.getJSONObject(0).get("low").toString() + degrees);
            //TextView today_cond2 = getView().findViewById(R.id.today_cond_label2);
            //today_cond2.setText(forecasts.getJSONObject(0).get("text").toString());
            setImageOnCondition(Integer.parseInt(forecasts.getJSONObject(0).get("code").toString()), R.id.today_cond_img2);

            TextView tomorrow_label = getView().findViewById(R.id.tomorrow_label);
            date = new Date(Long.parseLong(forecasts.getJSONObject(1).get("date").toString()) * 1000);
            tomorrow_label.setText("Tmrw, " + todayTomorrowFormat.format(date));
            TextView tomorrow_temp_label = getView().findViewById(R.id.tomorrow_temp_label);
            tomorrow_temp_label.setText(forecasts.getJSONObject(1).get("high").toString() + degrees + " / " + forecasts.getJSONObject(1).get("low").toString() + degrees);
        /*TextView tomorrow_cond_label = getView().findViewById(R.id.tomorrow_cond_label);
        tomorrow_cond_label.setText(forecasts.getJSONObject(1).get("text").toString());*/
            setImageOnCondition(Integer.parseInt(forecasts.getJSONObject(1).get("code").toString()), R.id.tomorrow_cond_img);


            TextView day3_label = getView().findViewById(R.id.day3_label);
            date = new Date(Long.parseLong(forecasts.getJSONObject(2).get("date").toString()) * 1000);
            day3_label.setText(dateFormat.format(date));
            TextView day3_temp_label = getView().findViewById(R.id.day3_temp_label);
            day3_temp_label.setText(forecasts.getJSONObject(2).get("high").toString() + degrees + " / " + forecasts.getJSONObject(2).get("low").toString() + degrees);
        /*TextView day3_cond_label = getView().findViewById(R.id.day3_cond_label);
        day3_cond_label.setText(forecasts.getJSONObject(2).get("text").toString());*/
            setImageOnCondition(Integer.parseInt(forecasts.getJSONObject(2).get("code").toString()), R.id.day3_cond_img);

            TextView day4_label = getView().findViewById(R.id.day4_label);
            date = new Date(Long.parseLong(forecasts.getJSONObject(3).get("date").toString()) * 1000);
            day4_label.setText(dateFormat.format(date));
            TextView day4_temp_label = getView().findViewById(R.id.day4_temp_label);
            day4_temp_label.setText(forecasts.getJSONObject(3).get("high").toString() + degrees + " / " + forecasts.getJSONObject(3).get("low").toString() + degrees);
        /*TextView day4_cond_label = getView().findViewById(R.id.day4_cond_label);
        day4_cond_label.setText(forecasts.getJSONObject(3).get("text").toString());*/
            setImageOnCondition(Integer.parseInt(forecasts.getJSONObject(3).get("code").toString()), R.id.day4_cond_img);

            TextView day5_label = getView().findViewById(R.id.day5_label);
            date = new Date(Long.parseLong(forecasts.getJSONObject(4).get("date").toString()) * 1000);
            day5_label.setText(dateFormat.format(date));
            TextView day5_temp_label = getView().findViewById(R.id.day5_temp_label);
            day5_temp_label.setText(forecasts.getJSONObject(4).get("high").toString() + degrees + " / " + forecasts.getJSONObject(4).get("low").toString() + degrees);
        /*TextView day5_cond_label = getView().findViewById(R.id.day5_cond_label);
        day5_cond_label.setText(forecasts.getJSONObject(4).get("text").toString());*/
            setImageOnCondition(Integer.parseInt(forecasts.getJSONObject(4).get("code").toString()), R.id.day5_cond_img);

            TextView day6_label = getView().findViewById(R.id.day6_label);
            date = new Date(Long.parseLong(forecasts.getJSONObject(5).get("date").toString()) * 1000);
            day6_label.setText(dateFormat.format(date));
            TextView day6_temp_label = getView().findViewById(R.id.day6_temp_label);
            day6_temp_label.setText(forecasts.getJSONObject(5).get("high").toString() + degrees + " / " + forecasts.getJSONObject(5).get("low").toString() + degrees);
        /*TextView day6_cond_label = getView().findViewById(R.id.day6_cond_label);
        day6_cond_label.setText(forecasts.getJSONObject(5).get("text").toString());*/
            setImageOnCondition(Integer.parseInt(forecasts.getJSONObject(5).get("code").toString()), R.id.day6_cond_img);

            TextView day7_label = getView().findViewById(R.id.day7_label);
            date = new Date(Long.parseLong(forecasts.getJSONObject(6).get("date").toString()) * 1000);
            day7_label.setText(dateFormat.format(date));
            TextView day7_temp_label = getView().findViewById(R.id.day7_temp_label);
            day7_temp_label.setText(forecasts.getJSONObject(6).get("high").toString() + degrees + " / " + forecasts.getJSONObject(6).get("low").toString() + degrees);
        /*TextView day7_cond_label = getView().findViewById(R.id.day7_cond_label);
        day7_cond_label.setText(forecasts.getJSONObject(6).get("text").toString());*/
            setImageOnCondition(Integer.parseInt(forecasts.getJSONObject(6).get("code").toString()), R.id.day7_cond_img);
        } catch (Exception e) {}

    }

    private void setImageOnCondition(int code, int image_id) {
        ImageView image = getView().findViewById(image_id);
        image.setScaleX(0.75f); image.setScaleY(0.75f);
        switch(code){
            case 0:
            case 1:
            case 2:
                image.setImageResource(R.drawable.tornado);
                break;
            case 3:
            case 4:
            case 37:
            case 38:
            case 47:
                image.setImageResource(R.drawable.thunderstorm);
                break;
            case 5:
            case 6:
            case 10:
            case 12:
                image.setImageResource(R.drawable.rain);
                break;
            case 7:
            case 13:
            case 14:
            case 15:
            case 16:
            case 18:
            case 19:
            case 25:
            case 43:
            case 46:
                image.setImageResource(R.drawable.snowy);
                break;
            case 8:
            case 9:
            case 11:
            case 39:
            case 45:
                image.setImageResource(R.drawable.drizzle);
                break;
            case 17:
            case 35:
                image.setImageResource(R.drawable.hail);
                break;
            case 20:
            case 21:
            case 22:
                image.setImageResource(R.drawable.foggy);
                break;
            case 23:
            case 24:
                image.setImageResource(R.drawable.windy);
                break;
            case 26:
                image.setImageResource(R.drawable.cloudy);
                break;
            case 27:
            case 29:
            case 33:
                image.setImageResource(R.drawable.partly_cloudy_n);
                break;
            case 28:
            case 30:
            case 34:
                image.setImageResource(R.drawable.partly_cloudy_d);
                break;
            case 31:
                image.setImageResource(R.drawable.clear_night);
                break;
            case 32:
            case 36:
                image.setImageResource(R.drawable.sunny);
                break;
            case 40:
                image.setImageResource(R.drawable.heavy_rain);
                break;
            default:
                image.setImageResource(R.drawable.unknown);
                image.setScaleX(0.5f); image.setScaleY(0.5f);
        }
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
