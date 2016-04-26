package com.kdao.cmpe235_project;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.MenuItem;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.kdao.cmpe235_project.util.PreferenceData;

public class MyActivity extends SlidingFragmentActivity {

    protected Fragment mFrag;
    private Button register_link;
    private TextView fullName;

    private boolean userLoggedIn;

    /*public MyActivity(int titleRes) {
        mTitleRes = titleRes;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceData.clearLoggedInEmailAddress(getApplicationContext());
        userLoggedIn = PreferenceData.getUserLoggedInStatus(getApplicationContext());
        // set the Behind View
        setBehindContentView(R.layout.leftmenu);
        if (savedInstanceState == null) {
            FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
            mFrag = new SampleListFragment();
            t.replace(R.id.menu_frame, mFrag);
            t.commit();
        } else {
            mFrag = (Fragment) this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
        }

        // customize the SlidingMenu
        SlidingMenu sm = getSlidingMenu();
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeDegree(0.35f);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        getElem();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getElem() {
        register_link = (Button) findViewById(R.id.register_link);
        fullName = (TextView) findViewById(R.id.nav_fullName);

        if (userLoggedIn == true) { //user logged in
            fullName.setText(PreferenceData.getLoggedInUserFullname(getApplicationContext()));
        } else {
            fullName.setVisibility(View.GONE);
            register_link.setText(R.string.login_btn);
        }
        register_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navRegistration(v);
            }
        });
    }

    public void navAllTree(View v) {
        Intent launchActivity = new Intent(getApplicationContext(), TreesListActivity.class);
        launchActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(launchActivity);
    }

    public void navScan(View v) {
        Intent launchActivity = new Intent(getApplicationContext(), BarcodeActivity.class);
        launchActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(launchActivity);
    }

    public void navPhoto(View v) {
        Intent launchActivity = new Intent(getApplicationContext(), CameraActivity.class);
        launchActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(launchActivity);
    }

    //log user out
    public void navRegistration(View v) {
        if (userLoggedIn == true) {
            PreferenceData.clearLoggedInEmailAddress(getApplicationContext());
        }
        Intent launchActivity = new Intent(getApplicationContext(), SigninActivity.class);
        launchActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(launchActivity);
    }

}