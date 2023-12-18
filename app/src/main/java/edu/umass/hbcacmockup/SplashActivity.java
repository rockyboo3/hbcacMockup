package edu.umass.hbcacmockup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TimerTask task = new TimerTask(){
            @Override
            public void run(){
                Intent intent = new Intent(SplashActivity.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        };

        Timer opening = new Timer();
        opening.schedule(task, 3000);
    }
}