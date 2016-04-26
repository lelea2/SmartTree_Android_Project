package com.kdao.cmpe235_project;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;

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

public class CommentActivity extends MyActivity {
    private String sessionTreeId;
    private String userId;
    private EditText commentText;
    private RatingBar ratingBar;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        commentText = (EditText) findViewById(R.id.comment_text);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        userId = PreferenceData.getLoggedInUserId(getApplicationContext());
        try {
            Bundle extras = getIntent().getExtras();
            sessionTreeId = extras.getString(Config.TREE_SESSION_ID).toString();
        } catch(Exception ex) {}
    }

    /**
     * Public function handling add comment
     * @method addComment
     */
    public void addComment(View view) {
        String rateValue = String.valueOf(ratingBar.getRating());
        String comment = "";
        try {
            comment = commentText.getText().toString();
        } catch(Exception ex) {}
        if (Utility.isEmptyString(comment)) {
            Toast.makeText(CommentActivity.this, Config.VALID_FORM, Toast.LENGTH_LONG).show();
        } else {
            addCommentToDB(rateValue, comment, userId, sessionTreeId, true);
        }
    }

    /**
     * Private function update new comment to DB
     * @method addCommentToDB
     */
    private void addCommentToDB(String rateValue, String comment, String userId, String
            treeId, boolean isLike) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(CommentActivity.this, "", Config.CREATE_COMMENT);
            }

            @Override
            protected String doInBackground(String... params) {
                String rateValue = params[0];
                String comment = params[1];
                String userId = params[2];
                String treeId = params[3];
                String isLike = params[4];
                HttpClient httpClient = new DefaultHttpClient();
                HttpPut httpPut = new HttpPut(Config.BASE_URL + "/comment/add");
                JSONObject json = new JSONObject();
                try {
                    /**
                     * {
                     "userId": "03786a40-698c-4aba-a826-3d4e532d0fab",
                     "treeId": "45304c60-9eac-48bf-9d0b-c02dda6c6cb3",
                     "comment": "This app is useful",
                     "islike": true,
                     "rating": 4
                     }
                     */
                    json.put("comment", comment);
                    json.put("rating", rateValue);
                    json.put("userId", userId);
                    json.put("treeId", treeId);
                    json.put("islike", isLike);
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
                    navigateToCommentListActivity();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(rateValue, comment, userId, sessionTreeId, String.valueOf(isLike));
    }

    //private function create comment per tree
    private void navigateToCommentListActivity() {
        Intent newIntent = new Intent(getApplicationContext(), CommentsListActivity.class);
        newIntent.putExtra(Config.TREE_SESSION_ID, sessionTreeId);
        newIntent.putExtra(Config.COMMENT_SESSION_ID, Config.NEW_COMMENT_CREATED);
        startActivity(newIntent);
    }

    public void navigateToMainActivity(View v) {
        Intent newIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(newIntent);
    }

}
