package com.kdao.cmpe235_project;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kdao.cmpe235_project.data.Tree;
import com.kdao.cmpe235_project.util.Config;
import com.kdao.cmpe235_project.util.PreferenceData;
import com.kdao.cmpe235_project.util.Utility;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class BarcodeActivity extends AppCompatActivity {

    static String TAG = "BarCodeActivity";
    private ImageView scanBtn;
    private String signinWithBarCode = "0";
    private String viewTreeWithBarCode = "0";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        try {
            Bundle extras = getIntent().getExtras();
            signinWithBarCode = extras.getString(Config.SIGN_IN_WITH_BARCODE);
            viewTreeWithBarCode = extras.getString(Config.VIEW_TREE_WITH_BARCODE);
            if (signinWithBarCode == "1" || viewTreeWithBarCode == "1") {
                initiateScan();
            } else {
                navigateToMainActivity();
            }
        } catch(Exception ex) {
            //catching
        }
    }

    /**
     * Helper function initiate scan
     */
    public void initiateScan() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    /**
     * Function handle barcode scanning result
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            if (signinWithBarCode.equals("1")) {
                logUserIn(scanContent);
            } else {
                getTreePerId(scanContent);
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), Config.SCAN_ERR, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Private function to log user in
     */
    private void logUserIn(String userId) {
        //scanBtn.setImageResource(R.drawable.progressbar);
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(BarcodeActivity.this, "", Config.AUTHENTICATE);
            }
            @Override
            protected String doInBackground(String... params) {
                String id = params[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(Config.BASE_URL + "/user/"+ id);
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
                String userId = "";
                try {
                    JSONObject jObject  = new JSONObject(result);
                    userId = (String) jObject.get("userId");
                } catch(Exception ex) {
                }
                if(!Utility.isEmptyString(userId)){
                    PreferenceData.setUserLoggedInStatus(getApplication(), true);
                    PreferenceData.setLoggedInUserId(getApplication(), userId);
                    navigateToMainActivity();
                } else {
                    Log.e(TAG, "Signin with barcode failed");
                    navigateToSignInActivity();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(userId);
    }

    /**
     *  Private function to get tree perId
     */
    private void getTreePerId(String treeId) {
        Intent launchActivity = new Intent(BarcodeActivity.this, TreeActivity.class);
        launchActivity.putExtra(Config.TREE_SESSION_ID, treeId);
        startActivity(launchActivity);
    }

    /**
     * Private function to navigate back to signin activity if scanning barcode failed
     * @method navigateToSignInActivity
     */
    private void navigateToSignInActivity() {
        Intent launchActivity = new Intent(getApplicationContext(), SigninActivity.class);
        launchActivity.putExtra(Config.SIGN_IN_WITH_BARCODE_ERR, "1");
        launchActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(launchActivity);
    }

    /**
     * Function navigate to main activity after signin
     */
    private void navigateToMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }
}

