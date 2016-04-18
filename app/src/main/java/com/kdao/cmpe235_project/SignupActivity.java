package com.kdao.cmpe235_project;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.widget.TextView;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;

import com.kdao.cmpe235_project.util.Config;
import com.kdao.cmpe235_project.util.Utility;
import com.kdao.cmpe235_project.util.PreferenceData;

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


public class SignupActivity extends AppCompatActivity {
    private ProgressDialog prgDialog;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText phoneInput;
    private EditText pwdInput;
    private EditText confirmPwdInput;

    private static String REGISTER_URL = Config.BASE_URL + "/user/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getElem();
    }

    /**
     * Private function to get method
     */
    private void getElem() {
        firstNameInput = (EditText)findViewById(R.id.registerFirstName);
        lastNameInput = (EditText)findViewById(R.id.registerLastName);
        emailInput = (EditText)findViewById(R.id.registerEmail);
        phoneInput = (EditText)findViewById(R.id.registerPhone);
        pwdInput = (EditText)findViewById(R.id.registerPwd);
        confirmPwdInput = (EditText) findViewById(R.id.registerRepwd);
        //Set up progress dialog
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
    }

    /**
     * Method trigger when click on register user
     *
     * @method registerUser
     */
    public void registerUser(View view) {
        String email = emailInput.getText().toString();
        String password = pwdInput.getText().toString();
        String confirmpwd = confirmPwdInput.getText().toString();
        String firstName = firstNameInput.getText().toString();
        String lastName = lastNameInput.getText().toString();
        String phone = phoneInput.getText().toString();
        if (!Utility.isEmptyString(email) && !Utility.isEmptyString(password) && !Utility.isEmptyString(firstName) && !Utility.isEmptyString(lastName) && !Utility.isEmptyString(phone)) {
            //When confirm pwd and pwd not match
            if (!Utility.isEmailValid(email)) {
                Toast.makeText(getApplicationContext(), Config.VALID_EMAIL, Toast.LENGTH_LONG).show();
            } else if (password != confirmpwd) {
                Toast.makeText(getApplicationContext(), Config.VALID_PWD, Toast.LENGTH_LONG).show();
            } else { //Valid email and pwd, proceed to signup user
                signupUser(email, password, firstName, lastName, phone);
            }
        } else { //Handle empty form
            Toast.makeText(getApplicationContext(), Config.VALID_FORM, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Private method for signup user
     * @param email
     * @param password
     * @param firstName
     * @param lastName
     * @param phone
     */
    private void signupUser(String email, String password, String firstName, String lastName, String phone) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String userEmail = params[0];
                String userPassword = params[1];
                String firstName = params[2];
                String lastName = params[3];
                String phone = params[4];
                HttpClient httpClient = new DefaultHttpClient();
                HttpPut httpPut = new HttpPut(REGISTER_URL);
                JSONObject json = new JSONObject();
                try {
                    json.put("email", userEmail);
                    json.put("password", userPassword);
                    json.put("firstName", firstName);
                    json.put("lastName", firstName);
                    json.put("phoneNum", phone);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    StringEntity se = new StringEntity(json.toString());
                    System.out.println(se);
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
                if(!Utility.isEmptyString(userId)) {
                    PreferenceData.setUserLoggedInStatus(getApplication(), true);
                    PreferenceData.setLoggedInUserId(getApplication(), userId);
                    navigateToMainActivity();
                } else {
                    Toast.makeText(getApplicationContext(), Config.REGISTER_ERR, Toast.LENGTH_LONG).show();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(email, password, firstName, lastName, phone);
    }

    /**
     * Method which navigates from Signup Activity to Signin Activity
     * @method navigatetoSigninActivity
     */
    public void navigatetoSigninActivity(View view){
        Intent loginIntent = new Intent(getApplicationContext(), SigninActivity.class);
        // Clears History of Activity
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
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
