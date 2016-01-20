package com.example.jvf.robot2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by N87 on 18/01/2016.
 */
public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            // Showing splah screen with a timer
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent myIntent = new Intent(SplashActivity.this, MainMenu.class);

                startActivity(myIntent);
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}







