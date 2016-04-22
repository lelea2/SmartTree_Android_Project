package com.kdao.cmpe235_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Button;
import android.widget.ArrayAdapter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.*;

import com.kdao.cmpe235_project.data.Tree;
import com.kdao.cmpe235_project.data.Location;
import com.kdao.cmpe235_project.data.Sensor;

import com.kdao.cmpe235_project.util.Config;
import com.kdao.cmpe235_project.util.PreferenceData;
import com.kdao.cmpe235_project.util.Utility;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
public class TreesListActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private ListView treeList;
    private List<Tree> trees = new ArrayList<Tree>();
    private static String GEL_TREES_URL = Config.BASE_URL + "/trees";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trees_list);
        try {
            Bundle extras = getIntent().getExtras();
            String msg = extras.getString(Config.TREE_ACTIVITY);
            if (!Utility.isEmptyString(msg)) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        } catch(Exception ex) {
        }
        getAllTrees();
    }

    /**
     * Private function to set up list view
     * @method populateListView
     */
    private void populateListView() {
        ArrayAdapter<Tree> adapter = new MyListAdapter();
        treeList = (ListView) findViewById(R.id.treeListView);
        treeList.setAdapter(adapter);
        //handle tree item on click
        treeList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                handleViewTreeItem(adapter, v, position, id);
            }
        });
    }

    /**
     * Private function to populate comments
     * @method populateTrees
     */
    private void populateTrees(JSONArray arrayObj) {
        //System.out.println(arrayObj.size());
        for (int i = 0; i < arrayObj.size(); i++) {
            try {
                JSONObject object = (JSONObject) arrayObj.get(i);
                Location location = new Location(Double.parseDouble(object.get("longitude").toString()),
                        Double.parseDouble(object.get("latitude").toString()), object.get
                        ("address").toString(), object.get("title").toString());
                Sensor sensor = new Sensor();
                trees.add(new Tree(object.get("id").toString(), object.get("description")
                        .toString(), "", object.get("youtubeId").toString(), location, sensor));
            } catch(Exception e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Private function to get all the available comment
     *
     * @method getAllTrees
     */
    private void getAllTrees() {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(TreesListActivity.this, "", Config.GET_TREES);
            }

            @Override
            protected String doInBackground(String... params) {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(GEL_TREES_URL);
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
                    populateTrees(arrayObj);
                    populateListView();
                } catch(Exception ex) {
                    System.out.println(ex);
                    Toast.makeText(getApplicationContext(), Config.SCAN_ERR, Toast.LENGTH_LONG).show();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    private class MyListAdapter extends ArrayAdapter<Tree> {
        public MyListAdapter() {
            super(TreesListActivity.this, R.layout.tree_item, trees);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.tree_item, parent, false);
            }
            //System.out.println(">>>> Generate tree <<<<< ");
            Tree tree = trees.get(position);
            TextView treeTitle = (TextView) itemView.findViewById(R.id.tree_title);
            treeTitle.setText(tree.getLocation().getName());
            TextView treeDesc = (TextView) itemView.findViewById(R.id.tree_description);
            treeDesc.setText(tree.getDescription());
            return itemView;
        }
    }

    //Handle navigate back to home page
    public void navigateToMainActivity(View view) {
        Intent newIntent = new Intent(getApplicationContext(), MainActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(newIntent);
    }

    //handle navigate to tree view page
    public void handleViewTreeItem(AdapterView<?> adapter, View v, int position, long id) {
        Tree selItem = (Tree) adapter.getItemAtPosition(position);
        String treeId = selItem.getId();
        System.out.println(">>>> treeId: " + treeId + "<<<<<<<");
        Intent newIntent = new Intent(getApplicationContext(), TreeActivity.class);
        newIntent.putExtra(Config.TREE_SESSION_ID, treeId);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(newIntent);
    }
}
