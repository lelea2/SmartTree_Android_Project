package com.kdao.cmpe235_project;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import com.kdao.cmpe235_project.util.Config;
import com.kdao.cmpe235_project.util.Utility;

public class CreateTreeActivity extends AppCompatActivity {

    private EditText treeTitle;
    private EditText treeDesc;
    private EditText treeAddr;
    private EditText youtubeId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tree);
        getElem();
    }

    //Private function to get elem view
    private void getElem() {
        treeTitle = (EditText) findViewById(R.id.new_tree_title);
        treeDesc = (EditText) findViewById(R.id.new_tree_desc);
        treeAddr = (EditText) findViewById(R.id.new_tree_address);
        youtubeId = (EditText) findViewById(R.id.new_tree_youtubeId);
    }

    //public view function to handle create tree
    public void createNewTree(View v) {
        String name = treeTitle.getText().toString();
        String desc = treeDesc.getText().toString();
        String addr = treeAddr.getText().toString();
        String videoId = youtubeId.getText().toString();
        if (!Utility.isEmptyString(name) && !Utility.isEmptyString(desc) && !Utility
                .isEmptyString(addr) && !Utility.isEmptyString(videoId)) {
            // Valide form
            handleCreateTree(name, desc, addr, videoId);
        } else { //Empty form handle
            Toast.makeText(getApplicationContext(), Config.VALID_FORM, Toast.LENGTH_LONG).show();
        }
    }

    private void handleCreateTree(String name, String desc, String addr, String videoId) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(CreateTreeActivity.this, "", Config.CREATE_TREE);
            }
            @Override
            protected String doInBackground(String... params) {
                String title = params[0];
                String desc = params[1];
                String addr = params[2];
                String youtubeId = params[3];
                HttpClient httpClient = new DefaultHttpClient();
                HttpPut httpPut = new HttpPut(Config.BASE_URL + "/tree/register");
                JSONObject json = new JSONObject();
                try {
                    //REST body: {id: params.id, title: params.title, description: params.description, address: params.address, longitude: params.longitude, latitude: params.latitude, youtubeId: params.youtubeId}
                    json.put("title", title);
                    json.put("description", desc);
                    json.put("address", addr);
                    json.put("youtubeId", youtubeId);
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
                String treeId = "";
                try {
                    JSONObject jObject  = new JSONObject(result);
                    treeId = (String) jObject.get("id");
                    if(!Utility.isEmptyString(treeId)) {
                        _nagivateToTreesListActivity();
                    } else {
                        Toast.makeText(getApplicationContext(), Config.CREATE_TREE_ERR, Toast.LENGTH_LONG)
                                .show();
                    }
                } catch(Exception ex) {
                    Toast.makeText(getApplicationContext(), Config.CREATE_TREE_ERR, Toast.LENGTH_LONG).show();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(name, desc, addr, videoId);
    }

    //private function to create new tree
    private void _nagivateToTreesListActivity() {
        Intent launchActivity = new Intent(getApplicationContext(), TreesListActivity.class);
        launchActivity.putExtra(Config.TREE_ACTIVITY, Config.NEW_TREE_CREATED);
        startActivity(launchActivity);
    }

    //public function navigate back to main activity
    public void navigateToMainActivity(View v) {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }
}
