package com.kdao.cmpe235_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kdao.cmpe235_project.data.Comment;
import com.kdao.cmpe235_project.util.Config;
import com.kdao.cmpe235_project.util.PreferenceData;
import com.kdao.cmpe235_project.util.Utility;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Text;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommentsListActivity extends MyActivity {

    private ListView commentList;
    private List<Comment> comments = new ArrayList<Comment>();
    private ProgressDialog progressDialog;
    private static String COMMENT_URL = Config.BASE_URL + "/comments";
    private String sessionTreeId = "";

    private TextView commentRate;
    private TextView commentCount;
    private TextView commentLikeCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_list);
        try {
            Bundle extras = getIntent().getExtras();
            sessionTreeId = extras.getString(Config.TREE_SESSION_ID).toString();
            COMMENT_URL = Config.BASE_URL + "/comments/tree/" + sessionTreeId;
        } catch(Exception ex) {
            //catching
        }
        if (Utility.isEmptyString(sessionTreeId)) {
            Toast.makeText(getApplicationContext(), "View all comments available", Toast
                    .LENGTH_LONG);
        }
        getElems();
        getAllComments();
    }

    /**
     * Private function to get elements that need dynamic change on page
     */
    private void getElems() {
        commentRate = (TextView) findViewById(R.id.comment_avg_rate);
        commentCount = (TextView) findViewById(R.id.comment_count);
        commentLikeCount = (TextView) findViewById(R.id.comment_like_count);
    }

    /**
     * Private function to set up list view
     * @method populateListView
     */
    private void populateListView() {
        ArrayAdapter<Comment> adapter = new MyListAdapter();
        commentList = (ListView) findViewById(R.id.commentList);
        commentList.setAdapter(adapter);
    }

    /**
     * Private function to populate comments
     * @method populateComments
     */
    private void populateComments(JSONArray arrayObj) {
        if (arrayObj.size() == 0) {
            Toast.makeText(getApplicationContext(), Config.NO_COMMENTS, Toast.LENGTH_LONG).show();
            commentCount.setText("0");
            commentLikeCount.setText("0");
            commentRate.setText("");
        } else {
            System.out.println(">>>>>>> Comments count:" + arrayObj.size());
            int avgRating = 0;
            int likecount = 0;
            commentCount.setText(arrayObj.size() + "");
            for (int i = 0; i < arrayObj.size(); i++) {
                try {
                    JSONObject object = (JSONObject) arrayObj.get(i);
                    boolean isLike = (Integer.parseInt(object.get("islike").toString()) == 1) ?
                            true : false;
                    Comment comment = new Comment(object.get("firstName").toString(), Integer.parseInt
                            (object.get("rating").toString()), object.get("comment").toString(),
                            object.get("ts").toString(), isLike);
                    System.out.println(Boolean.valueOf(object.get("islike").toString()));
                    comments.add(comment);
                    avgRating += comment.getRating();
                    if (isLike) {
                        likecount += 1;
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            commentLikeCount.setText(likecount + "");
            commentRate.setText(avgRating/arrayObj.size() + "");
        }
    }

    /**
     * Private function to get all the available comment
     *
     * @method getAllComments
     */
    private void getAllComments() {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(CommentsListActivity.this, "", Config.GET_COMMENTS);
            }

            @Override
            protected String doInBackground(String... params) {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(COMMENT_URL);
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
                    populateComments(arrayObj);
                    populateListView();
                } catch(Exception ex) {
                    System.out.println(ex);
                    Toast.makeText(getApplicationContext(), Config.SERVER_ERR, Toast.LENGTH_LONG).show();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    private class MyListAdapter extends ArrayAdapter<Comment> {
        public MyListAdapter() {
            super(CommentsListActivity.this, R.layout.comment_item, comments);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.comment_item, parent, false);
            }

            // Find the car to work with.
            Comment comment = comments.get(position);
            TextView usernameText = (TextView) itemView.findViewById(R.id.comment_username);
            usernameText.setText(comment.getUsername());
            TextView ratingTxt = (TextView) itemView.findViewById(R.id.comment_rating);
            ratingTxt.setText(Integer.toString(comment.getRating()));
            TextView commentTxt = (TextView) itemView.findViewById(R.id.comment);
            commentTxt.setText(comment.getComment());
            TextView timeText = (TextView) itemView.findViewById(R.id.comment_time);
            timeText.setText(comment.getTime());
            return itemView;
        }
    }

    /**
     * Public function navigate to add comment activity
     * @method addComment
     */
    public void addComment(View view) {
        boolean userLoggedIn = PreferenceData.getUserLoggedInStatus(getApplicationContext());
        if (userLoggedIn == false) { //If user not log in then cannot add comment
            Intent newIntent = new Intent(getApplicationContext(), SigninActivity.class);
            newIntent.putExtra(Config.SIGN_IN_REQUIRED, Config.COMMENT_NO_SIGNIN);
            startActivity(newIntent);
        } else {
            if (Utility.isEmptyString(sessionTreeId)) {
                Intent newIntent = new Intent(getApplicationContext(), TreesListActivity.class);
                newIntent.putExtra(Config.TREE_ACTIVITY, Config.COMMENT_PER_TREE);
                startActivity(newIntent);
            } else {
                Intent newIntent = new Intent(getApplicationContext(), CommentActivity.class);
                newIntent.putExtra(Config.TREE_SESSION_ID, sessionTreeId);
                startActivity(newIntent);
            }
        }
    }

    public void navigateToMainActivity(View v) {
        Intent newIntent = new Intent(getApplicationContext(), MainActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(newIntent);
    }

}
