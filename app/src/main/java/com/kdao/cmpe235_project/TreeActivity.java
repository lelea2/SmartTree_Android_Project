package com.kdao.cmpe235_project;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.Button;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

import android.util.Log;

import com.kdao.cmpe235_project.data.Location;
import com.kdao.cmpe235_project.data.Tree;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.kdao.cmpe235_project.util.Config;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class TreeActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private HashMap<String, Tree> hmTrees= new HashMap<String, Tree>();
    final Context context = this;
    Switch switchButton;
    TextView textView;
    String switchOn = "ON";
    String switchOff = "OFF";

    static String TAG = "TreeActivity";
    //Pre-define for youtube video handler
    private ProgressDialog progressDialog;

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;
    private YouTubePlayer player;
    private Tree currentTree;

    private String treeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);
        try {
            Bundle extras = getIntent().getExtras();
            treeId = extras.getString(Config.TREE_SESSION_ID);
            if (treeId != null) {
            } else {
                showAlertDialog();
                navigateToTreesList();
            }
        } catch(Exception ex) {
            //catching
            showAlertDialog();
            navigateToTreesList();
        }
    }

    //Helper function to get tree information
    private void getTree(String treeId) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(TreeActivity.this, "", Config.GET_TREE_INFO);
            }

            @Override
            protected String doInBackground(String... params) {
                String treeId = params[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(Config.BASE_URL + "/tree/" + treeId);
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
                } catch(Exception ex) {
                    System.out.println(ex);
                    showAlertDialog();
                    navigateToTreesList();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(treeId);
    }

    private void navigateToTreesList() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }
    //Function handle session passed by other activity
    /*private void handleSession() {
        Bundle extras = getIntent().getExtras();
        String value = "";
        currentTree = null;
        if (extras != null) {
            value = extras.getString("SESSION_ID");
            Log.i(TAG, "SESSION_ID=" + value);
            currentTree = hmTrees.get(value);
        }
        if (currentTree != null) {
            createCurrentTreeView(currentTree);
            //Initialize youtube video view
            youTubeView = (YouTubePlayerView) findViewById(R.id.tree_video);
            youTubeView.initialize(Config.YOUTUBE_API_KEY, this);
            playerStateChangeListener = new MyPlayerStateChangeListener();
            playbackEventListener = new MyPlaybackEventListener();
        } else {
            showAlertDialog();
        }
    }*/

    /**
     * Create view for current tree
     */
    private void createCurrentTreeView(Tree mytree) {

    }

    /**
     * Helper function to show alert box
     */
    private void showAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        // set title
        alertDialogBuilder.setTitle("Tree Information View");
        // set dialog message
        alertDialogBuilder
                .setMessage("No information for tree exist. Please try again")
                .setCancelable(false)
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Navigate back to barcode page
                        Intent activity = new Intent(TreeActivity.this, TreesListActivity.class);
                        startActivity(activity);
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    /**
     * Initialize Youtube video
     * @param provider
     * @param player
     * @param wasRestored
     */
    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        this.player = player;
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);
        if (!wasRestored) {
            Log.i(TAG, "Initial youtube video load");
            player.cueVideo(currentTree.getYoutubeId()); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    /**
     * Handle youtube video failure
     * @param provider
     * @param errorReason
     */
    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handle youtube video loading
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    }

    protected Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_interact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            // Called when playback starts, either due to user action or call to play().
            showMessage("Playing");
        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
            showMessage("Paused");
        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
            showMessage("Stopped");
        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
        }

        @Override
        public void onSeekTo(int i) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
        }
    }

    //The following is to handle youtube video action
    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        @Override
        public void onLoaded(String s) {
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
            Log.e(TAG, errorReason.toString());
            showMessage("Error");
        }
    }
}
