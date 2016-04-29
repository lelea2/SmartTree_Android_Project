package com.kdao.cmpe235_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kdao.cmpe235_project.api.CustomedNumberPicker;
import com.kdao.cmpe235_project.data.SensorType;
import com.kdao.cmpe235_project.data.SensorHelper;
import com.kdao.cmpe235_project.util.Config;
import com.kdao.cmpe235_project.data.Sensor;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SensorActivity extends MyActivity {

    private String sensorId;
    private String sensorType;
    private ProgressDialog progressDialog;
    private static String GET_SENSOR_URL = Config.BASE_URL + "/sensor/";
    private Sensor sensor;
    private SensorType type;
    private SensorHelper sensorHelper;
    private String treeName;
    private String treeId;
    private static final CharSequence[] colors = {" Red "," Blue "," Green "," White "};


    private TextView sensorDeploy;
    private ToggleButton btnToggle;
    private AlertDialog colorDialog;
    private FrameLayout colorPalette;
    private RelativeLayout lightSensor;
    private RelativeLayout commonSensor;
    private CustomedNumberPicker numberPicker;
    private int colorPicker = Color.WHITE; // default state of color state
    private boolean sensorState  = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        try {
            Bundle extras = getIntent().getExtras();
            sensorId = extras.getString(Config.SENSOR_SESSION_ID);
            sensorType = extras.getString(Config.SENSOR_SESSION_TYPE);
            //Generate getSensor URL
            getElem();
            if (sensorId != null && sensorType != null) {
                treeName = extras.getString(Config.TREE_SESSION_NAME);
                GET_SENSOR_URL =  Config.BASE_URL + "/sensor/type/" + sensorType + "/" + sensorId;
                System.out.println(GET_SENSOR_URL);
                getSensor();
            } else  if (sensorId != null) {
                GET_SENSOR_URL =  Config.BASE_URL + "/sensor/" + sensorId;
                System.out.println(GET_SENSOR_URL);
                getSensor();
            } else { //return to treeId list if treeId in session not available
                navigateToSensorsList();
            }
        } catch(Exception ex) {
            //catching
            navigateToSensorsList();
        }
    }

    private void getElem() {
        sensorDeploy = (TextView) findViewById(R.id.sensor_tree_deploy);
        btnToggle = (ToggleButton) findViewById(R.id.toggleButton);
        colorPalette = (FrameLayout) findViewById(R.id.light_color);
        lightSensor = (RelativeLayout) findViewById(R.id.light_sensor);
        commonSensor = (RelativeLayout) findViewById(R.id.common_sensor);
    }

    private void getSensor() {
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
                System.out.println("Do get sensors >>>>>");
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
                    arrayObj= (JSONArray) jsonParser.parse(result);
                    JSONObject obj = (JSONObject) arrayObj.get(0);
                    createSensorView(obj);
                    generateSensorView(obj);
                } catch(Exception ex) {
                    System.out.println("Error while generate sensor view");
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    private void generateSensorView(JSONObject obj) {
        treeId = obj.get("treeId").toString();
        if (treeName != null) {
            sensorDeploy.setText("Tree Deployed: " + treeName);
        } else {
            sensorDeploy.setText("Tree Deployed: " + treeId);
        }
        try {
            boolean selected = sensorHelper.getSensorState();
            //System.out.println(">>>> sensor on=" + selected);
            btnToggle.setSelected(selected);
            if (sensor.getType().getId() == Config.LIGHT_SENSOR) {
                commonSensor.setVisibility(View.GONE);
            } else {
                lightSensor.setVisibility(View.GONE);
            }
        } catch(Exception ex) {
        }
    }

    private void createSensorView(JSONObject obj) {
        boolean isDeploy = (obj.get("treeId") != null) ? true : false;
        type = new SensorType(Integer.parseInt(sensorType), "");
        sensor = new Sensor(obj.get("id").toString(), obj.get("name").toString(), type, isDeploy);
        sensorHelper = new SensorHelper(type, obj);
    }

    private void navigateToSensorsList() {
        Intent launchActivity = new Intent(getApplicationContext(), SensorsListActivity.class);
        launchActivity.putExtra(Config.SENSOR_SESSION_ID, Config.SENSOR_NO_AVAIL);
        startActivity(launchActivity);
    }

    public void navigateToSensorsList(View v) {
        Intent launchActivity = new Intent(getApplicationContext(), SensorsListActivity.class);
        launchActivity.putExtra(Config.TREE_SESSION_ID, treeId);
        startActivity(launchActivity);
    }

    //Public function execute when user update sensor
    public void updateSensor(View v) {
        sensorState = btnToggle.isChecked();
        if (sensor.getType().getId() == Config.LIGHT_SENSOR) {
        } else {

        }
    }

    //Private function handling update sensors
    private void _updateSensor() {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(SensorActivity.this, "", Config.UPDATE_SENSOR);
            }

            @Override
            protected String doInBackground(String... params) {
                String state = params[0];
                String value = params[1];
                HttpClient httpClient = new DefaultHttpClient();
                HttpPut httpPut = new HttpPut(Config.BASE_URL + "/sensor/update" + sensorType + "/" + sensorId);
                org.json.JSONObject json = new org.json.JSONObject();
                try {
                    json.put("on", state);
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
                if (result == "error") { //error case
                    Toast.makeText(getApplicationContext(), Config.SERVER_ERR, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), Config.SENSOR_UPDATED, Toast.LENGTH_LONG).show();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    public void colorPick(View v) {
        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.color_dialog);
        builder.setSingleChoiceItems(colors, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int color) {
                //private static final CharSequence[] colors = {" Red "," Blue "," Green "," White "};
                switch (color) {
                    case 0:
                        colorPicker = Color.RED;
                        break;
                    case 1:
                        colorPicker = Color.BLUE;
                        break;
                    case 2:
                        colorPicker = Color.GREEN;
                        break;
                    case 3:
                        colorPicker = Color.WHITE;
                        break;
                }
                colorPalette.setBackgroundColor(colorPicker);
                colorDialog.dismiss();
            }
        });
        colorDialog = builder.create();
        colorDialog.show();
    }
}
