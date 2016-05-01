package com.kdao.cmpe235_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kdao.cmpe235_project.data.Sensor;
import com.kdao.cmpe235_project.data.SensorType;
import com.kdao.cmpe235_project.util.Config;
import com.kdao.cmpe235_project.util.Utility;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SensorsListActivity extends MyActivity {
    private ProgressDialog progressDialog;
    private ListView sensorList;
    private List<Sensor> sensors = new ArrayList<Sensor>();
    private static String GET_SENSORS_URL = Config.BASE_URL + "/sensors";
    private String treeId;
    private String treeName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors_list);
        Bundle extras = getIntent().getExtras();
        try {
            String msg = extras.getString(Config.SENSOR_ACTIVITY).toString();
            if (!Utility.isEmptyString(msg)) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        } catch(Exception ex) {}
        try {
            treeId = extras.getString(Config.TREE_SESSION_ID);
            treeName = extras.getString(Config.TREE_SESSION_NAME);
            if (treeId != null) {
                GET_SENSORS_URL = Config.BASE_URL + "/sensors?treeId=" + treeId;
            }
        } catch(Exception ex) {}
        getSensors();
    }

    private void populateSensors(JSONArray arrObj) {
        System.out.println(arrObj);
        if (arrObj.size() == 0) {
            Toast.makeText(getApplicationContext(), Config.NO_SENSORS, Toast.LENGTH_LONG).show();
            return;
        }
        for (int i = 0; i < arrObj.size(); i++) {
            try {
                JSONObject object = (JSONObject) arrObj.get(i);
                SensorType type = new SensorType(Integer.parseInt(object.get("sensorType").toString()));
                boolean isDeploy = (treeId != null) ? true : false;
                try {
                    if (!object.get("treeId").toString().isEmpty()) {
                        isDeploy = true;
                    }
                } catch (Exception ex) {}
                sensors.add(new Sensor(object.get("id").toString(), object.get("name").toString()
                        , type, isDeploy));
            } catch(Exception e) {
                System.out.println(e);
            }
        }
    }

    public void handleViewSensorItem(AdapterView<?> adapter, View v, int position, long id) {
        Sensor selItem = (Sensor) adapter.getItemAtPosition(position);
        String sensorId = selItem.getId();
        System.out.println(">>>> sensorId: " + sensorId + "<<<<<<<");
        if (selItem.isDeploy()) { //If sensor is deployed
            Intent newIntent = new Intent(getApplicationContext(), SensorActivity.class);
            newIntent.putExtra(Config.SENSOR_SESSION_ID, sensorId);
            newIntent.putExtra(Config.SENSOR_SESSION_TYPE, Integer.toString(selItem.getType().getId()));
            newIntent.putExtra(Config.TREE_SESSION_NAME, treeName);
            startActivity(newIntent);
        } else {
            Toast.makeText(getApplicationContext(), Config.SENSOR_NO_DEPLOY, Toast.LENGTH_LONG).show();
        }
    }

    private void populateSensorsView() {
        ArrayAdapter<Sensor> adapter = new MyListAdapter();
        sensorList = (ListView) findViewById(R.id.sensorListView);
        sensorList.setAdapter(adapter);
        //handle tree item on click
        sensorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                handleViewSensorItem(adapter, v, position, id);
            }
        });
    }

    /**
     * Private function to get all the sensors available comment
     *
     * @method getSensors
     */
    private void getSensors() {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(SensorsListActivity.this, "", Config.GET_SENSORS);
            }

            @Override
            protected String doInBackground(String... params) {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(GET_SENSORS_URL);
                System.out.println(">>>> SensorsURL: " + GET_SENSORS_URL);
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
                    populateSensors(arrayObj);
                    populateSensorsView();
                } catch(Exception ex) {
                    System.out.println(">>>>>>> Sensor list exception <<<<<<");
                    System.out.println(ex);
                    Toast.makeText(getApplicationContext(), Config.SCAN_ERR, Toast.LENGTH_LONG).show();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    private class MyListAdapter extends ArrayAdapter<Sensor> {
        public MyListAdapter() {
            super(SensorsListActivity.this, R.layout.tree_item, sensors);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.sensor_item, parent, false);
            }
            //System.out.println(">>>> Generate tree <<<<< ");
            Sensor sensor = sensors.get(position);
            TextView sensorName = (TextView) itemView.findViewById(R.id.sensor_name);
            sensorName.setText(sensor.getName());
            TextView sensorDeploy = (TextView) itemView.findViewById(R.id.sensor_deploy_state);
            sensorDeploy.setText(sensor.isDeploy() ? "Connected" : "Disconnected");
            sensorDeploy.setTextColor(getResources().getColor(sensor.isDeploy() ? R.color.colorGreen
                    : R.color.colorRed));
            return itemView;
        }
    }

    //Public function navigate back to main activity page
    public void navigateToMainActivity(View v) {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainIntent);
    }
}
