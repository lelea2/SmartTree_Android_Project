package com.kdao.cmpe235_project;

/*
 * Copyright 2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.kdao.cmpe235_project.api.SpinAdapter;
import com.kdao.cmpe235_project.data.Location;
import com.kdao.cmpe235_project.data.Sensor;
import com.kdao.cmpe235_project.data.Tree;
import com.kdao.cmpe235_project.util.Config;
import com.kdao.cmpe235_project.util.PreferenceData;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * UploadActivity is a ListActivity of uploading, and uploaded records as well
 * as buttons for managing the uploads and creating new ones.
 */
public class UploadActivity extends ListActivity {

    // Indicates that no upload is currently selected
    private static final int INDEX_NOT_CHECKED = -1;

    // TAG for logging;
    private static final String TAG = "UploadActivity";

    // Button for upload operations
    private Button btnUploadAudio;
    private Button btnUploadVideo;
    private Button btnUploadImage;
    private Button btnPause;
    private Button btnResume;
    private Button btnCancel;
    private Button btnDelete;
    private Button btnPauseAll;
    private Button btnCancelAll;

    //Set up for tree dropdown
    private Spinner treeListSpinner;
    private SpinAdapter adapter;
    private ProgressDialog progressDialog;
    private List<Tree> trees = new ArrayList<Tree>();
    private static String GEL_TREES_URL = Config.BASE_URL + "/trees";
    private String treeId;
    private String userId;
    private String uploadFileName;
    private String APIurl;
    private boolean isTreeSelected = false;


    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;

    // The SimpleAdapter adapts the data about transfers to rows in the UI
    private SimpleAdapter simpleAdapter;

    // A List of all transfers
    private List<TransferObserver> observers;

    /**
     * This map is used to provide data to the SimpleAdapter above. See the
     * fillMap() function for how it relates observers to rows in the displayed
     * activity.
     */
    private ArrayList<HashMap<String, Object>> transferRecordMaps;

    // Which row in the UI is currently checked (if any)
    private int checkedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        treeListSpinner = (Spinner) findViewById(R.id.tree_list_selection);
        getAllTrees();
        // Initializes TransferUtility, always do this before using it.
        transferUtility = Agent.getTransferUtility(this);
        checkedIndex = INDEX_NOT_CHECKED;
        transferRecordMaps = new ArrayList<HashMap<String, Object>>();
        userId = PreferenceData.getLoggedInUserId(getApplicationContext());
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Get the data from any transfer's that have already happened,
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Clear transfer listeners to prevent memory leak, or
        // else this activity won't be garbage collected.
        if (observers != null && !observers.isEmpty()) {
            for (TransferObserver observer : observers) {
                observer.cleanTransferListener();
            }
        }
    }

    /**
     * Gets all relevant transfers from the Transfer Service for populating the
     * UI
     */
    private void initData() {
        transferRecordMaps.clear();
        // Use TransferUtility to get all upload transfers.
        observers = transferUtility.getTransfersWithType(TransferType.UPLOAD);
        TransferListener listener = new UploadListener();
        for (TransferObserver observer : observers) {

            // For each transfer we will will create an entry in
            // transferRecordMaps which will display
            // as a single row in the UI
            HashMap<String, Object> map = new HashMap<String, Object>();
            Agent.fillMap(map, observer, false);
            transferRecordMaps.add(map);

            // Sets listeners to in progress transfers
            if (TransferState.WAITING.equals(observer.getState())
                    || TransferState.WAITING_FOR_NETWORK.equals(observer.getState())
                    || TransferState.IN_PROGRESS.equals(observer.getState())) {
                observer.setTransferListener(listener);
            }
        }
        simpleAdapter.notifyDataSetChanged();
    }

    private void initUI() {
        /**
         * This adapter takes the data in transferRecordMaps and displays it,
         * with the keys of the map being related to the columns in the adapter
         */
        simpleAdapter = new SimpleAdapter(this, transferRecordMaps,
                R.layout.record_item, new String[] {
                "checked", "fileName", "progress", "bytes", "state", "percentage"
        },
                new int[] {
                        R.id.radioButton1, R.id.textFileName, R.id.progressBar1, R.id.textBytes,
                        R.id.textState, R.id.textPercentage
                });
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                switch (view.getId()) {
                    case R.id.radioButton1:
                        RadioButton radio = (RadioButton) view;
                        radio.setChecked((Boolean) data);
                        return true;
                    case R.id.textFileName:
                        TextView fileName = (TextView) view;
                        fileName.setText((String) data);
                        return true;
                    case R.id.progressBar1:
                        ProgressBar progress = (ProgressBar) view;
                        progress.setProgress((Integer) data);
                        return true;
                    case R.id.textBytes:
                        TextView bytes = (TextView) view;
                        bytes.setText((String) data);
                        return true;
                    case R.id.textState:
                        TextView state = (TextView) view;
                        state.setText(((TransferState) data).toString());
                        return true;
                    case R.id.textPercentage:
                        TextView percentage = (TextView) view;
                        percentage.setText((String) data);
                        return true;
                }
                return false;
            }
        });
        setListAdapter(simpleAdapter);

        // Updates checked index when an item is clicked
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {

                if (checkedIndex != pos) {
                    transferRecordMaps.get(pos).put("checked", true);
                    if (checkedIndex >= 0) {
                        transferRecordMaps.get(checkedIndex).put("checked", false);
                    }
                    checkedIndex = pos;
                    updateButtonAvailability();
                    simpleAdapter.notifyDataSetChanged();
                }
            }
        });

        //btnUploadFile = (Button) findViewById(R.id.buttonUploadFile);
        btnUploadImage = (Button) findViewById(R.id.buttonUploadImage);
        btnUploadAudio = (Button) findViewById(R.id.buttonUploadAudio);
        btnUploadVideo = (Button) findViewById(R.id.buttonUploadVideo);
        btnPause = (Button) findViewById(R.id.buttonPause);
        btnResume = (Button) findViewById(R.id.buttonResume);
        btnCancel = (Button) findViewById(R.id.buttonCancel);
        btnDelete = (Button) findViewById(R.id.buttonDelete);
        btnPauseAll = (Button) findViewById(R.id.buttonPauseAll);
        btnCancelAll = (Button) findViewById(R.id.buttonCancelAll);



        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTreeSelected) {
                    Intent intent = new Intent();
                    if (Build.VERSION.SDK_INT >= 19) {
                        // For Android versions of KitKat or later, we use a
                        // different intent to ensure
                        // we can get the file path from the returned intent URI
                        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                        APIurl = "/tree/"+treeId+"/photo";
                    } else {
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                    }

                    intent.setType("image/*");
                    startActivityForResult(intent, 0);
                }
                else {
                    Toast.makeText(getApplicationContext(), Config.UPLOAD_NOTREE_ERR, Toast.LENGTH_LONG).show();
                }
            }
        });


        btnUploadAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTreeSelected) {
                    Intent intent = new Intent();
                    if (Build.VERSION.SDK_INT >= 19) {
                        // For Android versions of KitKat or later, we use a
                        // different intent to ensure
                        // we can get the file path from the returned intent URI
                        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                        APIurl = "/tree/"+treeId+"/audio";
                    } else {
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                    }

                    intent.setType("audio/*");
                    startActivityForResult(intent, 0);
                }
                else {
                    Toast.makeText(getApplicationContext(), Config.UPLOAD_NOTREE_ERR, Toast.LENGTH_LONG).show();
                }
            }

        });


        btnUploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTreeSelected) {
                    Intent intent = new Intent();
                    if (Build.VERSION.SDK_INT >= 19) {
                        // For Android versions of KitKat or later, we use a
                        // different intent to ensure
                        // we can get the file path from the returned intent URI
                        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                        APIurl = "/tree/"+treeId+"/video";
                    } else {
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                    }

                    intent.setType("video/*");
                    startActivityForResult(intent, 0);
                }
                else {
                    Toast.makeText(getApplicationContext(), Config.UPLOAD_NOTREE_ERR, Toast.LENGTH_LONG).show();
                }
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make sure the user has selected a transfer
                if (checkedIndex >= 0 && checkedIndex < observers.size()) {
                    Boolean paused = transferUtility.pause(observers.get(checkedIndex).getId());
                    /**
                     * If paused does not return true, it is likely because the
                     * user is trying to pause an upload that is not in a
                     * pausable state (For instance it is already paused, or
                     * canceled).
                     */
                    if (!paused) {
                        Toast.makeText(
                                UploadActivity.this,
                                "Cannot pause transfer.  You can only pause transfers in a IN_PROGRESS or WAITING state.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make sure the user has selected a transfer
                if (checkedIndex >= 0 && checkedIndex < observers.size()) {
                    TransferObserver resumed = transferUtility.resume(observers.get(checkedIndex)
                            .getId());
                    // Sets a new transfer listener to the original observer.
                    // This will overwrite existing listener.
                    observers.get(checkedIndex).setTransferListener(new UploadListener());
                    /**
                     * If resume returns null, it is likely because the transfer
                     * is not in a resumable state (For instance it is already
                     * running).
                     */
                    if (resumed == null) {
                        Toast.makeText(
                                UploadActivity.this,
                                "Cannot resume transfer.  You can only resume transfers in a PAUSED state.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make sure a transfer is selected
                if (checkedIndex >= 0 && checkedIndex < observers.size()) {
                    Boolean canceled = transferUtility.cancel(observers.get(checkedIndex).getId());
                    /**
                     * If cancel returns false, it is likely because the
                     * transfer is already canceled
                     */
                    if (!canceled) {
                        Toast.makeText(
                                UploadActivity.this,
                                "Cannot cancel transfer.  You can only resume transfers in a PAUSED, WAITING, or IN_PROGRESS state.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make sure a transfer is selected
                if (checkedIndex >= 0 && checkedIndex < observers.size()) {
                    transferUtility.deleteTransferRecord(observers.get(checkedIndex).getId());
                    observers.remove(checkedIndex);
                    transferRecordMaps.remove(checkedIndex);
                    checkedIndex = INDEX_NOT_CHECKED;
                    updateButtonAvailability();
                    updateList();
                }
            }
        });

        btnPauseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferUtility.pauseAllWithType(TransferType.UPLOAD);
            }
        });

        btnCancelAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferUtility.cancelAllWithType(TransferType.UPLOAD);
            }
        });

        updateButtonAvailability();
    }

    /*
     * Updates the ListView according to the observers.
     */
    private void updateList() {
        TransferObserver observer = null;
        HashMap<String, Object> map = null;
        for (int i = 0; i < observers.size(); i++) {
            observer = observers.get(i);
            map = transferRecordMaps.get(i);
            Agent.fillMap(map, observer, i == checkedIndex);
        }
        simpleAdapter.notifyDataSetChanged();

    }

    /*
     * Enables or disables buttons according to checkedIndex.
     */
    private void updateButtonAvailability() {
        boolean availability = checkedIndex >= 0;
        btnPause.setEnabled(availability);
        btnResume.setEnabled(availability);
        btnCancel.setEnabled(availability);
        btnDelete.setEnabled(availability);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {
                String path = getPath(uri);

                boolean isCompleted = beginUpload(path);
                if (isCompleted) {
                    File file = new File(path);
                    addUploadedFIleToDB(APIurl, treeId, file.getName());
                }
            } catch (URISyntaxException e) {
                Toast.makeText(this,
                        "Unable to get the file from the given URI.  See error log for details",
                        Toast.LENGTH_LONG).show();
                Log.e(TAG, "Unable to upload file from the given uri", e);
            }
        }
    }


    private void addUploadedFIleToDB(final String APIurl, String treeId, String fileName) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(UploadActivity.this, "", Config.SAVE_TO_DB);
            }

            @Override
            protected String doInBackground(String... params) {
                String uploadFileName = params[0];
                String treeId = params[1];
                String APIurl = params[2];
                HttpClient httpClient = new DefaultHttpClient();
                HttpPut httpPut = new HttpPut(Config.BASE_URL + APIurl);
                org.json.JSONObject json = new org.json.JSONObject();
                try {
                    json.put("filename", uploadFileName);
                    json.put("treeId", treeId);
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
                    Toast.makeText(getApplicationContext(), Config.DB_WRITE_SUCCEED,
                            Toast.LENGTH_LONG).show();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(fileName, treeId, APIurl);
    }
    /*
     * Begins to upload the file specified by the file path.
     */
    private boolean beginUpload(String filePath) {
        if (filePath == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file", Toast.LENGTH_LONG).show();
            return false;
        }
        File file = new File(filePath);
        TransferObserver observer = transferUtility.upload(getString(R.string.BUCKET_NAME), file.getName(),
                file);
        while (observer.getState() != TransferState.FAILED && observer.getState() != TransferState.COMPLETED
                && observer.getState() != TransferState.CANCELED){}
        return observer.getState() == TransferState.COMPLETED;
        /*
         * Note that usually we set the transfer listener after initializing the
         * transfer. However it isn't required in this sample app. The flow is
         * click upload button -> start an activity for image selection
         * startActivityForResult -> onActivityResult -> beginUpload -> onResume
         * -> set listeners to in progress transfers.
         */
        // observer.setTransferListener(new UploadListener());
    }

    /*
     * Gets the file path of the given Uri.
     */
    @SuppressLint("NewApi")
    private String getPath(Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[] {
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /*
     * A TransferListener class that can listen to a upload task and be notified
     * when the status changes.
     */
    private class UploadListener implements TransferListener {

        // Simply updates the UI list when notified.
        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "Error during upload: " + id, e);
            updateList();
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
            updateList();
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            Log.d(TAG, "onStateChanged: " + id + ", " + newState);
            updateList();
        }
    }

    private void getAllTrees() {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(UploadActivity.this, "", Config.GET_TREES);
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
                    createTreeDrodown();
                } catch(Exception ex) {
                    System.out.println(ex);
                    Toast.makeText(getApplicationContext(), Config.SCAN_ERR, Toast.LENGTH_LONG).show();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
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
        if (arrayObj.size() > 0) {
            treeId = trees.get(0).getId();
        }
    }

    private void createTreeDrodown() {
        adapter = new SpinAdapter(UploadActivity.this, R.layout.spinner_item, trees);
        treeListSpinner.setAdapter(adapter);
        treeListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                Tree tree = (Tree) adapter.getItem(position);
                treeId = tree.getId();
                isTreeSelected = true;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
                isTreeSelected = false;
            }
        });
    }
}
