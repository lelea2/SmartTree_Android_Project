package com.kdao.cmpe235_project;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;

import android.view.View;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.kdao.cmpe235_project.util.PreferenceData;

public class MyActivity extends SlidingFragmentActivity {

    private int mTitleRes;
    protected ListFragment mFrag;

    /*public MyActivity(int titleRes) {
        mTitleRes = titleRes;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceData.clearLoggedInEmailAddress(getApplicationContext());
        boolean userLoggedIn = PreferenceData.getUserLoggedInStatus(getApplicationContext());

        if (userLoggedIn == true) {
            // set the Behind View
            setBehindContentView(R.layout.leftmenu);
            if (savedInstanceState == null) {
                FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
                mFrag = new SampleListFragment();
                t.replace(R.id.menu_frame, mFrag);
                t.commit();
            } else {
                mFrag = (ListFragment) this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
            }

            // customize the SlidingMenu
            SlidingMenu sm = getSlidingMenu();
            sm.setShadowWidthRes(R.dimen.shadow_width);
            sm.setShadowDrawable(R.drawable.shadow);
            sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
            sm.setFadeDegree(0.35f);
            sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        }

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void navAllTree(View v) {

    }

    public void navScan(View v) {

    }

    public void navPhoto(View v) {

    }

    public void navLogout(View v) {
        
    }
}