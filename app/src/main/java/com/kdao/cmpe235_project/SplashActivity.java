package com.kdao.cmpe235_project;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;

import com.kdao.cmpe235_project.util.PreferenceData;

public class SplashActivity extends AppCompatActivity {

    //Splash Window keep 3 sec will into MainActivity
    private final int SPLASH_DISPLAY_LENGHT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                //PreferenceData.clearLoggedInEmailAddress(getApplicationContext());
                boolean userLoggedIn = PreferenceData.getUserLoggedInStatus(getApplicationContext());
                if (userLoggedIn == false) { //navigate to signin page if user is not signin yet*/
                    Intent signinIntent = new Intent(SplashActivity.this, SigninActivity.class);
                    SplashActivity.this.startActivity(signinIntent);
                } else {
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                }
                SplashActivity.this.finish();
            }

        }, SPLASH_DISPLAY_LENGHT);
    }
}
