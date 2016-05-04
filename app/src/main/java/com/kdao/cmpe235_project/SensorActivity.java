package com.kdao.cmpe235_project;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
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
import java.util.ArrayList;
import java.util.List;

public class SensorActivity extends MyActivity implements RobotChangedStateListener {

    private String sensorId;
    private String sensorType;
    private ProgressDialog progressDialog;
    private static String GET_SENSOR_URL = Config.BASE_URL + "/sensor/";
    private Sensor sensor;
    private SensorType type;
    private int typeInt;
    private SensorHelper sensorHelper;
    private String treeName;
    private String treeId;
    private static final CharSequence[] colors = {" Red "," Blue "," Green "," White ", " Yellow " +
            "", " Pink "};
    final Context context = this;

    private TextView sensorDeploy;
    private ToggleButton btnToggle;
    private AlertDialog colorDialog;
    private FrameLayout colorPalette;
    private RelativeLayout lightSensor;
    private RelativeLayout commonSensor;
    private Button undeployBtn;
    private Button spheroBtn;
    private Button spheroBlink;

    private CustomedNumberPicker numberPicker;
    private int DEFAULT_COLOR = Color.BLUE;
    private int colorPicker = DEFAULT_COLOR; // default state of color state
    private boolean sensorState  = false;
    private boolean userLoggedIn = false;
    private int userRole = 2;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 42;
    private static final float ROBOT_VELOCITY = 0.6f;
    private boolean initialLit = true;

    private ConvenienceRobot mRobot;

    private static String TAG = "Sphero";

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

        /*
            Associate a listener for robot state changes with the DualStackDiscoveryAgent.
            DualStackDiscoveryAgent checks for both Bluetooth Classic and Bluetooth LE.
            DiscoveryAgentClassic checks only for Bluetooth Classic robots.
            DiscoveryAgentLE checks only for Bluetooth LE robots.
       */
        DualStackDiscoveryAgent.getInstance().addRobotStateListener( this );

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            int hasLocationPermission = checkSelfPermission( android.Manifest.permission.ACCESS_COARSE_LOCATION );
            if( hasLocationPermission != PackageManager.PERMISSION_GRANTED ) {
                Log.e(TAG, Config.SPHERO_NOT_GRANTED);
                Toast.makeText(getApplicationContext(), Config.SPHERO_NOT_GRANTED, Toast.LENGTH_LONG).show();
                List<String> permissions = new ArrayList<String>();
                permissions.add( Manifest.permission.ACCESS_COARSE_LOCATION);
                requestPermissions(permissions.toArray(new String[permissions.size()] ), REQUEST_CODE_LOCATION_PERMISSION );
            } else {
                Log.d(TAG, Config.SPHERO_GRANTED);
                Toast.makeText(getApplicationContext(), Config.SPHERO_GRANTED, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getElem() {
        sensorDeploy = (TextView) findViewById(R.id.sensor_tree_deploy);
        btnToggle = (ToggleButton) findViewById(R.id.toggleButton);
        colorPalette = (FrameLayout) findViewById(R.id.light_color);
        lightSensor = (RelativeLayout) findViewById(R.id.light_sensor);
        commonSensor = (RelativeLayout) findViewById(R.id.common_sensor);
        undeployBtn = (Button) findViewById(R.id.btn_disconnect);
        numberPicker = (CustomedNumberPicker) findViewById(R.id.numberPicker);
        spheroBtn = (Button) findViewById(R.id.btn_interact);
        spheroBlink = (Button) findViewById(R.id.btn_blink);
        //Hide undeploy btn if user is not admin
        if (userLoggedIn == false || userRole != Config.ADMIN_ROLE) {
            undeployBtn.setVisibility(View.GONE);
        }
    }

    //Public function dealing with sensor interact
    public void spheroInteract(View v) {
        if (typeInt == Config.LIGHT_SENSOR) {
            blink(true, false); //lit all the time
        } else if (typeInt == Config.SPEED_SENSOR) {
            robotDrive();
        } else {
            Toast.makeText(getApplicationContext(), Config.ROBOT_NO_INTERACT, Toast.LENGTH_LONG).show();
        }
    }

    //Public function setting blinking
    public void spheroBlink(View v) {
        System.out.println(">>> Set blinking led <<<<");
        blink(true, true); //lit all the time
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

    private void setBtnInteract() {
        //Hide interact button if not correct sensor
        try {
            if (typeInt != Config.SPEED_SENSOR && typeInt != Config.LIGHT_SENSOR) {
                spheroBtn.setVisibility(View.GONE);
                spheroBlink.setVisibility(View.GONE);
            }
        } catch(Exception ex) {
            spheroBtn.setVisibility(View.GONE);
            spheroBlink.setVisibility(View.GONE);
        }
    }

    private void generateSensorView(JSONObject obj) {
        treeId = obj.get("treeId").toString();
        System.out.println(obj);
        if (treeName != null) {
            sensorDeploy.setText("Tree Deployed: " + treeName);
        } else {
            sensorDeploy.setText("Tree Deployed: " + treeId);
        }
        try {
            boolean selected = sensorHelper.getSensorState();
            System.out.println(">>>> sensor on=" + selected);
            //setSelected() doesn't change the toggle state. According to google it does the following: Changes the selection state of this view. A view can be selected or not. Note that selection is not the same as focus. Views are typically selected in the context of an AdapterView like ListView or GridView; the selected view is the view that is highlighted.
            btnToggle.setSelected(selected);
            btnToggle.setChecked(selected);
            //btnToggle.forceLayout(); //update state
            typeInt = sensor.getType().getId();
            if (typeInt == Config.LIGHT_SENSOR) { //light sensor
                commonSensor.setVisibility(View.GONE);
                //colorPalette.setBackgroundColor(Integer.parseInt());
                int colorInt = DEFAULT_COLOR;
                try {
                    colorInt = Integer.parseInt(obj.get("lightcolor").toString());
                } catch(Exception ex) {}
                colorPicker = colorInt;
                colorPalette.setBackgroundColor(colorInt);
            } else { //set color for other sensor
                lightSensor.setVisibility(View.GONE);
                try {
                    int data = Integer.parseInt(getCommonSensorData(obj));
                    numberPicker.setValue(data);
                } catch(Exception ex) {}
            }
            setBtnInteract();
        } catch(Exception ex) {}
    }

    //Private helper function getting data of sensor
    private String getCommonSensorData(JSONObject obj) {
        String data = "60";
        //[{"treeId":"8f14886c-d267-44b8-8518-8cf363634929","sensorType":"3","name":"speed sensor","id":null,"wateron":null,"watertemp":null,"ts":null,"voiceon":null,"volume":null,"speedon":null,"speedlimit":null,"lighton":null,"lightcolor":null}]
        int typeInt = sensor.getType().getId();
        if (typeInt == Config.WATER_SENSOR) {
            data = obj.get("watertemp").toString();
        } else if (typeInt == Config.VOICE_SENSOR) {
            data = obj.get("volume").toString();
        } else if (typeInt == Config.SPEED_SENSOR) {
            data = obj.get("speedlimit").toString();
        }
        return data;
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
        String sensorValue;
        if (userLoggedIn == true) { //user only able to interact with tree when they log in
            sensorState = btnToggle.isChecked();
            System.out.println(">>> sensorState:<<<<" + sensorState);
            if (sensor.getType().getId() == Config.LIGHT_SENSOR) { //update light sensor
                sensorValue = colorPicker + "";
            } else { //update other sensor
                sensorValue = numberPicker.getValue() + "";
            }
            _updateSensor(sensorValue);
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
                HttpDelete httpDelete = new HttpDelete(Config.BASE_URL + "/tree/" + treeId + "/sensor/" + sensorId);
                //System.out.println(Config.BASE_URL + "/tree/" + treeId + "/sensor");
                //System.out.println(">>>> sensorId:" + sensorId + "<<<<<<<<");
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
                    //Toast.makeText(getApplicationContext(), Config.SENSOR_DELETED, Toast.LENGTH_LONG).show();
                    _navigateBackToTree();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    //Private function handling update sensors
    private void _updateSensor(String sensorValue) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(SensorActivity.this, "", Config.UPDATE_SENSOR);
            }
            @Override
            protected String doInBackground(String... params) {
                String value = params[0];
                HttpClient httpClient = new DefaultHttpClient();
                //update sensor URL: /sensor/update/:type/:id
                HttpPut httpPut = new HttpPut(Config.BASE_URL + "/sensor/update/" + sensorType + "/" + sensorId);
                org.json.JSONObject json = new org.json.JSONObject();
                try {
                    json.put("on", (sensorState == true) ? 1 : 0);
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
                    if (sensorState == false && sensor.getType().getId() == Config.LIGHT_SENSOR) { //turn off the light
                        blink(false, false);
                    }
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(sensorValue);
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
                    case 4:
                        colorPicker = Color.YELLOW;
                        break;
                    case 5:
                        colorPicker = Color.rgb(240, 167, 235);
                        break;
                }
                colorPalette.setBackgroundColor(colorPicker);
                colorDialog.dismiss();
            }
        });
        colorDialog = builder.create();
        colorDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch ( requestCode ) {
            case REQUEST_CODE_LOCATION_PERMISSION: {
                for( int i = 0; i < permissions.length; i++ ) {
                    if( grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                        startDiscovery();
                        Log.d( "Permissions", "Permission Granted: " + permissions[i] );
                    } else if( grantResults[i] == PackageManager.PERMISSION_DENIED ) {
                        Log.d( "Permissions", "Permission Denied: " + permissions[i] );
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                    || checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startDiscovery();
            }
        } catch(Exception ex) {}
    }

    private void startDiscovery() {
        //If the DiscoveryAgent is not already looking for robots, start discovery.
        if( !DualStackDiscoveryAgent.getInstance().isDiscovering() ) {
            try {
                DualStackDiscoveryAgent.getInstance().startDiscovery( this );
            } catch (DiscoveryException e) {
                Log.e(TAG, "DiscoveryException: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onStop() {
        //If the DiscoveryAgent is in discovery mode, stop it.
        if( DualStackDiscoveryAgent.getInstance().isDiscovering() ) {
            DualStackDiscoveryAgent.getInstance().stopDiscovery();
        }

        //If a robot is connected to the device, disconnect it
        if( mRobot != null ) {
            mRobot.disconnect();
            mRobot = null;
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DualStackDiscoveryAgent.getInstance().addRobotStateListener(null);
    }

    private void robotDrive() {
        //If the robot is null, then it is probably not connected and nothing needs to be done
        if( mRobot == null ) {
            Toast.makeText(getApplicationContext(), Config.ROBOT_NOT_ONLINE, Toast.LENGTH_LONG).show();
            return;
        }

        //Forward
        mRobot.drive( 0.0f, ROBOT_VELOCITY );
        //To the right
        //mRobot.drive( 90.0f, ROBOT_VELOCITY );
        //Backward
        //mRobot.drive( 180.0f, ROBOT_VELOCITY );
        //To the left
        //mRobot.drive( 270.0f, ROBOT_VELOCITY );
    }

    @Override
    public void handleRobotChangedState(Robot robot, RobotChangedStateNotificationType type) {
        switch (type) {
            case Online: {
                //If robot uses Bluetooth LE, Developer Mode can be turned on.
                //This turns off DOS protection. This generally isn't required.
                if( robot instanceof RobotLE) {
                    ( (RobotLE) robot ).setDeveloperMode( true );
                }
                //Save the robot as a ConvenienceRobot for additional utility methods
                mRobot = new ConvenienceRobot(robot);
                break;
            }
            case Disconnected: {
                Toast.makeText(getApplicationContext(), Config.ROBOT_DISCONNECTED, Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    //Turn the robot LED on or off every two seconds
    private void blink(final boolean lit, final boolean blinking) {
        if (mRobot == null || btnToggle.isChecked() == false) {
            Toast.makeText(getApplicationContext(), Config.ROBOT_NOT_ONLINE, Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(getApplicationContext(), Config.SET_LED, Toast.LENGTH_LONG).show();
        if (lit == false) {
            mRobot.setLed(0.0f, 0.0f, 0.0f);
        } else {
            if (colorPicker == Color.BLUE) {
                mRobot.setLed(0.0f, 0.0f, 1.0f);
            } else if (colorPicker == Color.RED) {
                mRobot.setLed(1.0f, 0.0f, 0.0f);
            } else if (colorPicker == Color.GREEN) {
                mRobot.setLed(0.0f, 1.0f, 0.0f);
            } else if (colorPicker == Color.YELLOW) {
                mRobot.setLed(1.0f, 1.0f, 0.0f);
            } else if (colorPicker == Color.rgb(240, 167, 235)) {
                mRobot.setLed(0.9f, 0.6f, 0.9f);
            } else {
                mRobot.setLed(0.5f, 0.5f, 0.5f);
            }
        }
        if (blinking == true) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    blink(!lit, blinking);
                }
            }, 2000);
        }
    }
}
