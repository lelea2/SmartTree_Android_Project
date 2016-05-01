package com.kdao.cmpe235_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Spinner;
import android.widget.EditText;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Toast;

import com.kdao.cmpe235_project.util.Config;
import com.kdao.cmpe235_project.util.Utility;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

public class CreateSensorActivity extends AppCompatActivity {
    private Spinner sensorType;
    private EditText sensorName;
    private ProgressDialog progressDialog;

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
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sensorType.setAdapter(dataAdapter);
    }

    //1. water
    //2. light
    //3. speed
    //4. voice
    private int getSensorIntType(String name) {
        if (name == "Voice Sensor") {
            return 4;
        } else if (name == "Water Sensor") {
            return 1;
        } else if (name == "Speed Sensor") {
            return 3;
        } else {
            return 2;
        }
    }

    //Create new tree
    public void createNewSensor(View v) {
        String name = "";
        String type = "";
        try {
            name = sensorName.getText().toString();
            type =  String.valueOf(sensorType.getSelectedItem());
        } catch(Exception ex) {}
        if (!Utility.isEmptyString(name)) {
            // Valid form
            handleCreateSensor(name, getSensorIntType(type));
        } else { //Empty form handle
            Toast.makeText(getApplicationContext(), Config.VALID_FORM, Toast.LENGTH_LONG).show();
        }
    }

    private void handleCreateSensor(String name, int type) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(CreateSensorActivity.this, "", Config.CREATE_SENSOR);
            }
            @Override
            protected String doInBackground(String... params) {
                String name = params[0];
                int type = Integer.parseInt(params[1]);
                HttpClient httpClient = new DefaultHttpClient();
                HttpPut httpPut = new HttpPut(Config.BASE_URL + "/sensor/register");
                JSONObject json = new JSONObject();
                try {
                    json.put("name", name);
                    json.put("type", type);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    StringEntity se = new StringEntity(json.toString());
                    se.setContentEncoding("UTF-8");
                    httpPut.setEntity(se);
                    try {
                        HttpResponse httpResponse = httpClient.execute(httpPut);
                        InputStream inputStream = httpResponse.getEntity().getContent();
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String bufferedStrChunk = null;
                        while((bufferedStrChunk = bufferedReader.readLine()) != null) {
                            stringBuilder.append(bufferedStrChunk);
                        }
                        return stringBuilder.toString();
                    } catch (Exception e) {
                        System.out.println("An Exception given because of UrlEncodedFormEntity " +
                                "argument :" + e);
                        e.printStackTrace();
                    }
                } catch (Exception uee) {
                    System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
                    uee.printStackTrace();
                }
                return "error";
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                progressDialog.dismiss();
                String sensorId = "";
                try {
                    JSONObject jObject  = new JSONObject(result);
                    sensorId = (String) jObject.get("id");
                    if(!Utility.isEmptyString(sensorId)) {
                        _nagivateToSensorListActivity();
                    } else {
                        Toast.makeText(getApplicationContext(), Config.CREATE_SENSOR_ERR, Toast.LENGTH_LONG).show();
                    }
                } catch(Exception ex) {
                    Toast.makeText(getApplicationContext(), Config.CREATE_SENSOR_ERR, Toast.LENGTH_LONG).show();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(name, String.valueOf(type));
    }


    private void _nagivateToSensorListActivity() {
        Intent launchActivity = new Intent(getApplicationContext(), SensorsListActivity.class);
        launchActivity.putExtra(Config.SENSOR_ACTIVITY, Config.NEW_SENSOR_CREATED);
        startActivity(launchActivity);
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
