package com.kdao.cmpe235_project;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.kdao.cmpe235_project.util.Config;
import com.kdao.cmpe235_project.util.PreferenceData;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void navigateToAllTree(View v) {
        Intent launchActivity = new Intent(getApplicationContext(), TreesListActivity.class);
        Log.i(TAG, "Navigate to view all trees available");
        startActivity(launchActivity);
    }

    public void navigateToBarCode(View v) {
        Intent launchActivity = new Intent(getApplicationContext(), BarcodeActivity.class);
        Log.i(TAG, "Navigate to Barcode");
        startActivity(launchActivity);
    }

    public void navigateToPhoto(View v) {
        Intent launchActivity = new Intent(getApplicationContext(), CameraActivity.class);
        Log.i(TAG, "Navigate to Camera activity");
        startActivity(launchActivity);
    }

    public void navigateToComment(View v) {
        Intent launchActivity = new Intent(getApplicationContext(), CommentsListActivity.class);
        Log.i(TAG, "Navigate to Comment lists");
        startActivity(launchActivity);
    }

    public void navigateToCreateTree(View v) {
        Intent launchActivity = new Intent(getApplicationContext(), CommentsListActivity.class);
        Log.i(TAG, "Navigate to Create tree");
        startActivity(launchActivity);
    }

    public void navigateToCreateSensor(View v) {
        Intent launchActivity = new Intent(getApplicationContext(), CommentsListActivity.class);
        Log.i(TAG, "Navigate to Create tree");
        startActivity(launchActivity);
    }

    public void navigateToDeployTree(View v) {
        Intent launchActivity = new Intent(getApplicationContext(), CommentsListActivity.class);
        Log.i(TAG, "Navigate to deploy tree");
        startActivity(launchActivity);
    }

    public void navigateToStatus(View v) {
        Intent launchActivity = new Intent(getApplicationContext(), CommentsListActivity.class);
        Log.i(TAG, "Navigate to view status");
        startActivity(launchActivity);
    }
}
