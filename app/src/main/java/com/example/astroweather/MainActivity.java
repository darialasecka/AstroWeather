package com.example.astroweather;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Double lat;
    private Double lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AlertDialog.Builder error_dialog = new AlertDialog.Builder(this);
        TextView error_msg = new TextView(this);
        error_msg.setText("Incorrect input");
        error_msg.setGravity(Gravity.CENTER_HORIZONTAL);
        error_dialog.setView(error_msg);

        Button ok_button = findViewById(R.id.ok_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText lat_text = findViewById(R.id.lat);
                EditText lon_text= findViewById(R.id.lon);

                try {
                    lat = Double.parseDouble(lat_text.getText().toString());
                    lon = Double.parseDouble(lon_text.getText().toString());
                    if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                        error_dialog.show();
                    } else {
                        Intent intent = new Intent(MainActivity.this, FragmentView.class);
                        Bundle bundle = new Bundle();
                        bundle.putDouble("lat", lat);
                        bundle.putDouble("lon", lon);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    error_dialog.show();
                }
            }
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
    }
}
