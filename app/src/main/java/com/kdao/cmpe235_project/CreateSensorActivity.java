package com.kdao.cmpe235_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Spinner;
import android.widget.EditText;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Toast;

import com.kdao.cmpe235_project.util.Config;
import com.kdao.cmpe235_project.util.Utility;

import java.util.List;
import java.util.ArrayList;

public class CreateSensorActivity extends AppCompatActivity {
    private Spinner sensorType;
    private EditText sensorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sensor);
        sensorName = (EditText) findViewById(R.id.new_sensor_name);
        addSensorType();
    }

    private void addSensorType() {
        sensorType = (Spinner) findViewById(R.id.new_sensor_type);
        List<String> list = new ArrayList<String>();
        list.add("Light Sensor");
        list.add("Water Sensor");
        list.add("Speed Sensor");
        list.add("Voice Sensor");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout
                .simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sensorType.setAdapter(dataAdapter);
    }

    //Create new tree
    public void createNewSensor(View v) {
        String name = sensorName.getText().toString();
        if (!Utility.isEmptyString(name)) {
            // Valid form
            handleCreateSensor(name, 1);
        } else { //Empty form handle
            Toast.makeText(getApplicationContext(), Config.VALID_FORM, Toast.LENGTH_LONG).show();
        }
    }

    private void handleCreateSensor(String name, int sensorType) {

    }

    public void navigateToCreateTree(View v) {
        Intent newIntent = new Intent(getApplicationContext(), CreateTreeActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(newIntent);
    }

    public void navigateToMainActivity(View v) {
        Intent newIntent = new Intent(getApplicationContext(), MainActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(newIntent);
    }

}
