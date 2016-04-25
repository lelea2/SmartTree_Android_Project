package com.kdao.cmpe235_project;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

//Create class which is extension of default android app
public class MyActivity extends AppCompatActivity {
    SlidingMenu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
       // menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setSecondaryMenu(R.layout.leftmenu);
    }


}
