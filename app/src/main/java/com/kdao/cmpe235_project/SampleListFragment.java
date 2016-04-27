package com.kdao.cmpe235_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kdao.cmpe235_project.util.PreferenceData;

public class SampleListFragment extends Fragment implements View.OnClickListener {

    private Button register_link;
    private Button nav_alltree;
    private Button nav_scan;
    private Button nav_photo;

    private TextView fullName;
    private boolean userLoggedIn;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leftmenu, container, false);
        userLoggedIn = PreferenceData.getUserLoggedInStatus(getActivity().getApplicationContext());
        register_link = (Button) view.findViewById(R.id.nav_register_link);
        nav_alltree = (Button) view.findViewById(R.id.nav_alltree);
        nav_scan = (Button) view.findViewById(R.id.nav_scan);
        nav_photo = (Button) view.findViewById(R.id.nav_photo);
        fullName = (TextView) view.findViewById(R.id.nav_fullName);
        register_link.setOnClickListener(this);
        nav_alltree.setOnClickListener(this);
        nav_scan.setOnClickListener(this);
        nav_photo.setOnClickListener(this);
        if (userLoggedIn == true) { //user logged in
            fullName.setText(PreferenceData.getLoggedInUserFullname(getActivity().getApplicationContext()));
        } else {
            fullName.setVisibility(View.GONE);
            register_link.setText(R.string.login_btn);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.nav_register_link:
                navRegistration(v);
                break;
            case R.id.nav_alltree:
                navAllTree(v);
                break;
            case R.id.nav_scan:
                navScan(v);
                break;
            case R.id.nav_photo:
                navPhoto(v);
                break;
            default:
                break;
        }
    }

    private void navAllTree(View v) {
        Intent launchActivity = new Intent(getActivity().getApplicationContext(),
                TreesListActivity.class);
        launchActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(launchActivity);
    }

    private void navScan(View v) {
        Intent launchActivity = new Intent(getActivity().getApplicationContext(), BarcodeActivity.class);
        launchActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(launchActivity);
    }

    private void navPhoto(View v) {
        Intent launchActivity = new Intent(getActivity().getApplicationContext(), CameraActivity.class);
        launchActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(launchActivity);
    }

    //log user out
    public void navRegistration(View v) {
        if (userLoggedIn == true) {
            PreferenceData.clearLoggedInEmailAddress(getActivity().getApplicationContext());
        }
        Intent launchActivity = new Intent(getActivity().getApplicationContext(), SigninActivity.class);
        launchActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(launchActivity);
    }
}