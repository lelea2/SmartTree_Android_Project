package com.kdao.cmpe235_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.content.Context;
import android.widget.Button;

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
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import com.kdao.cmpe235_project.util.PreferenceData;
import com.orbotix.ConvenienceRobot;
import com.orbotix.DualStackDiscoveryAgent;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.le.RobotLE;

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
    final Context context = this;

    private TextView sensorDeploy;
    private ToggleButton btnToggle;
    private AlertDialog colorDialog;
    private FrameLayout colorPalette;
    private RelativeLayout lightSensor;
    private RelativeLayout commonSensor;
    private Button undeployBtn;
    private CustomedNumberPicker numberPicker;
    private int colorPicker = Color.WHITE; // default state of color state
    private boolean sensorState  = false;
    private boolean userLoggedIn = false;
    private int userRole = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        userLoggedIn = PreferenceData.getUserLoggedInStatus(getApplicationContext());
        userRole = PreferenceData.getLoggedInRole(getApplicationContext());

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
        undeployBtn = (Button) findViewById(R.id.btn_disconnect);
        //Hide undeploy btn if user is not admin
        if (userLoggedIn == false || userRole != Config.ADMIN_ROLE) {
            undeployBtn.setVisibility(View.GONE);
        }
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
        launchActivity.putExtra(Config.TREE_SESSION_NAME, treeName);
        startActivity(launchActivity);
    }

    //Public function execute when user update sensor
    public void updateSensor(View v) {
        if (userLoggedIn == true) { //user only able to interact with tree when they log in
            sensorState = btnToggle.isChecked();
            if (sensor.getType().getId() == Config.LIGHT_SENSOR) { //update light sensor
            } else { //update other sensor
            }
        } else {
            showAlertDialog(Config.SENSOR_TITLE, Config.SENSOR_UPDATE_REQUIRED, Config.DIALOG_TRY_AGAIN);
        }
    }

    private void showAlertDialog(String title, String message, String tryText) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set title
        alertDialogBuilder.setTitle(title);
        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(tryText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Navigate back to barcode page
                        if (userLoggedIn == false) {
                            Intent activity;
                            activity = new Intent(SensorActivity.this, SigninActivity.class);
                            startActivity(activity);
                        } else {
                            _undeploySensor();
                        }
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    //Public function to handle un-deploy sensor
    public void undeploySensor(View v) {
        showAlertDialog(Config.SENSOR_UNDEPLOY_TITLE, Config.SENSOR_UNDEPLOY_CHECK, Config.DIALOG_TRY_AGAIN);
    }

    //function undeploy sensor
    private void _undeploySensor() {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(SensorActivity.this, "", Config.UNDEPLOY_SENSOR);
            }

            @Override
            protected String doInBackground(String... params) {
                HttpClient httpClient = new DefaultHttpClient();
                HttpDelete httpDelete = new HttpDelete(Config.BASE_URL + "/tree/" + treeId + "/sensor");
                org.json.JSONObject json = new org.json.JSONObject();
                try {
                    json.put("sensorId", sensorId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    try {
                        HttpResponse httpResponse = httpClient.execute(httpDelete);
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
                    //Toast.makeText(getApplicationContext(), Config.SENSOR_DELETED, Toast
                            .LENGTH_LONG).show();
                    _navigateBackToTree();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
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
                //update sensor URL: /sensor/update/:type/:id
                HttpPut httpPut = new HttpPut(Config.BASE_URL + "/sensor/update" + sensorType + "/" + sensorId);
                org.json.JSONObject json = new org.json.JSONObject();
                try {
                    json.put("on", state);
                    json.put("data", value);
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

    //Navigate back to tree
    private void _navigateBackToTree() {
        Intent launchActivity = new Intent(getApplicationContext(), TreesListActivity.class);
        launchActivity.putExtra(Config.TREE_ACTIVITY, Config.SENSOR_DELETED);
        startActivity(launchActivity);
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
