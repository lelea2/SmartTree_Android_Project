package com.kdao.cmpe235_project;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

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