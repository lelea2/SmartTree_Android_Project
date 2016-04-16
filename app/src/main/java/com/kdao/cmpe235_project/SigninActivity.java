package com.kdao.cmpe235_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.*;
import org.apache.http.entity.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;

import java.io.*;

import com.kdao.cmpe235_project.util.Utility;
import com.kdao.cmpe235_project.util.Config;
import com.kdao.cmpe235_project.util.PreferenceData;

public class SigninActivity extends AppCompatActivity {
    private TextView errMsg;
    private EditText emailText;
    private EditText pwdText;

    private static String TAG = "SigninActivity";
    private static String LOGIN_URL = Config.BASE_URL + "/user/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        getElem();
    }

    /**
     * Private method to get elemnt on page
     */
    private void getElem() {
        errMsg = (TextView) findViewById(R.id.login_error);
        emailText = (EditText) findViewById(R.id.loginEmail);
        pwdText = (EditText) findViewById(R.id.loginPassword);
    }

    /**
     * Method trigger when click on login user
     *
     * @method loginUser
     */
    public void loginUser(View view) {
        String email = emailText.getText().toString();
        String password = pwdText.getText().toString();
        if (!Utility.isEmptyString(email) && !Utility.isEmptyString(password)) {
            // When Email entered is Valid
            if (Utility.isEmailValid(email)) {
                logUserIn(email, password);
            } else { // When Email is invalid
                Toast.makeText(getApplicationContext(), Config.VALID_EMAIL, Toast.LENGTH_LONG).show();
            }
        } else { //Empty form handle
            Toast.makeText(getApplicationContext(), Config.VALID_FORM, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Private function for user login
     *
     * @method logUserIn
     */
    private void logUserIn(String email, String password) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String userEmail = params[0];
                String userPassword = params[1];
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(LOGIN_URL);
                JSONObject json = new JSONObject();
                try {
                    json.put("email", userEmail);
                    json.put("password", userPassword);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    StringEntity se = new StringEntity(json.toString());
                    se.setContentEncoding("UTF-8");
                    httpPost.setEntity(se);
                    try {
                        HttpResponse httpResponse = httpClient.execute(httpPost);
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
                //System.out.println(result);
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
                    Toast.makeText(getApplicationContext(), Config.LOGIN_ERR, Toast.LENGTH_LONG).show();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(email, password);
    }

    /**
     * Public method to navigate to signup page
     * @method navigatetoSignupActivity
     */
    public void navigatetoSignupActivity(View view) {
        Intent loginIntent = new Intent(getApplicationContext(), SignupActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

    /**
     * Public method to navigate to page to login by barcode
     * @method loginByBarCode
     */
    public void loginByBarCode(View view) {
        Intent launchActivity = new Intent(SigninActivity.this, BarcodeActivity.class);
        Log.i(TAG, "Sign-in with barcode");
        //navigate to barcode page with SIGN_IN_WITH_BARCODE flag
        launchActivity.putExtra(Config.SIGN_IN_WITH_BARCODE, "1");
        startActivity(launchActivity);
    }

    /**
     * Private function to navigate to home activity
     * @method navigateToMainActivity
     */
    private void navigateToMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }
}
