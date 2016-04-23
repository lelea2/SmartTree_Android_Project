package com.kdao.cmpe235_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kdao.cmpe235_project.util.Config;

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

public class DeployConfirmActivity extends AppCompatActivity {

    private String sensorId;
    private String treeId;
    private String sensorName;
    private String treeName;
    private TextView treeText;
    private TextView sensorText;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deploy_confirm);
        try {
            Bundle extras = getIntent().getExtras();
            treeId = extras.getString(Config.TREE_SESSION_ID);
            treeName = extras.getString(Config.TREE_SESSION_NAME);
            sensorId = extras.getString(Config.SENSOR_SESSION_ID);
            sensorName = extras.getString(Config.SENSOR_SESSION_NAME);
            treeText = (TextView) findViewById(R.id.tree_name);
            sensorText = (TextView) findViewById(R.id.sensor_name);
            if (treeId != null && sensorId != null) {
                sensorText.setText("Sensor name: " + sensorName);
                treeText.setText("Tree name: " + treeName);
            } else { //return to treeId list if treeId in session not available
                _navigateToTreeListDeploy();
            }
        } catch (Exception ex) {
            //catching
            _navigateToTreeListDeploy();
        }
    }

    public void navigateBackToSensor(View v) {
        Intent launchActivity = new Intent(getApplicationContext(), DeployActivity.class);
        launchActivity.putExtra(Config.TREE_SESSION_ID, treeId);
        launchActivity.putExtra(Config.TREE_SESSION_NAME, treeName);
        startActivity(launchActivity);
    }

    public void deployTree(View v) {
        _deployTree();
    }

    private void _deployTree() {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(DeployConfirmActivity.this, "", Config.DEPLOY_TREE);
            }

            @Override
            protected String doInBackground(String... params) {
                String sensorId = params[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpPut httpPut = new HttpPut(Config.BASE_URL + "/tree/" + treeId + "/sensor");
                JSONObject json = new JSONObject();
                try {
                    json.put("sensorId", sensorId);
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
                if (result.equals("error")) { //error case
                    Toast.makeText(getApplicationContext(), Config.SERVER_ERR, Toast.LENGTH_LONG).show();
                } else {
                    navigateToSensorList();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(sensorId);
    }

    private void navigateToSensorList() {
        Intent launchActivity = new Intent(getApplicationContext(), SensorsListActivity.class);
        launchActivity.putExtra(Config.SENSOR_ACTIVITY, Config.TREE_DEPLOYED);
        startActivity(launchActivity);
    }

    private void _navigateToTreeListDeploy() {
        Intent launchActivity = new Intent(getApplicationContext(), DeployTreeListActivity.class);
        launchActivity.putExtra(Config.TREE_DEPLOY_ACTIVITY, Config.DEPLOY_ERR);
        startActivity(launchActivity);
    }
}
