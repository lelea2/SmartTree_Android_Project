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

    /*public MyActivity(int titleRes) {
        mTitleRes = titleRes;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the Behind View
        setBehindContentView(R.layout.content_frame);
        if (savedInstanceState == null) {
            FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
            mFrag = new SampleListFragment();
            t.replace(R.id.content_frame, mFrag);
            t.commit();
        } else {
            mFrag = (Fragment) this.getSupportFragmentManager().findFragmentById(R.id.content_frame);
        }

        // customize the SlidingMenu
        SlidingMenu sm = getSlidingMenu();
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeDegree(0.35f);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}