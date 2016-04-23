package com.kdao.cmpe235_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import com.kdao.cmpe235_project.util.Config;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SensorActivity extends AppCompatActivity {

    private String sensorId;
    private ProgressDialog progressDialog;
    private static String GET_SENSOR_URL = Config.BASE_URL + "/sensor/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        try {
            Bundle extras = getIntent().getExtras();
            sensorId = extras.getString(Config.SENSOR_SESSION_ID);
            if (sensorId != null) {
                GET_SENSOR_URL += sensorId;
                getSensor(sensorId);
            } else { //return to treeId list if treeId in session not available
                navigateToSensorsList();
            }
        } catch(Exception ex) {
            //catching
            navigateToSensorsList();
        }
    }

    private void getSensor(String id) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(SensorActivity.this, "", Config.GET_SENSOR);
            }

            @Override
            protected String doInBackground(String... params) {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(GET_SENSOR_URL);
                try {
                    try {
                        HttpResponse httpResponse = httpClient.execute(httpGet);
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
                        e.printStackTrace();
                    }
                } catch (Exception uee) {
                    System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
                    uee.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                progressDialog.dismiss();
                try {
                    JSONArray arrayObj = null;
                    JSONParser jsonParser = new JSONParser();
                } catch(Exception ex) {
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    private void navigateToSensorsList() {
        Intent launchActivity = new Intent(getApplicationContext(), SensorsListActivity.class);
        launchActivity.putExtra(Config.SENSOR_SESSION_ID, Config.SENSOR_NO_AVAIL);
        startActivity(launchActivity);
    }

    public void navigateToSensorsList(View v) {
        Intent launchActivity = new Intent(getApplicationContext(), SensorsListActivity.class);
        launchActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(launchActivity);
    }

    public void updateSensor(View v) {

    }
}
