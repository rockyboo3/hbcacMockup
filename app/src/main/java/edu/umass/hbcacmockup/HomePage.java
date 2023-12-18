package edu.umass.hbcacmockup;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomePage extends AppCompatActivity {
    private int stepsProgress = 0;
    private int waterProgress = 0;
    private int sleepProgress = 0;
    private int veggiesProgress = 0;
    private int activitiesDoneToday = 0;

    private boolean stepsFlag = true;
    private boolean waterFlag = true;
    private boolean sleepFlag = true;
    private boolean veggiesFlag = true;
    private ProgressBar stepsProgressBar;
    private ProgressBar waterProgressBar;
    private ProgressBar sleepProgressBar;
    private ProgressBar veggiesProgressBar;
    private TextView stepsTextViewProgress;
    private TextView waterTextViewProgress;
    private TextView sleepTextViewProgress;
    private TextView veggiesTextViewProgress;

    private VideoView mVideoView;
    ImageView settings;
    ImageView calendar;
    FirebaseUser user;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_home_page);


        //click to view settings page
        settings = findViewById(R.id.settingsImageView);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //click to view calendar page
        calendar = findViewById(R.id.calendarImageView);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, CalendarActivity.class);
                startActivity(intent);
                finish();
            }
        });



        /////////////////////BACKGROUND VIDEO CODE///////////////////////////
        mVideoView = (VideoView) findViewById(R.id.bgVideoView);

        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.bg_video);
        mVideoView.setVideoURI(uri);
        mVideoView.start();

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
            @Override
            public void onPrepared(MediaPlayer mediaPlayer){
                mediaPlayer.setLooping(true);
            }
        });
        /////////////////////////////////////////////////////////////////////

        //Steps
        stepsProgressBar = findViewById(R.id.stepsProgressBar);
        stepsTextViewProgress = findViewById(R.id.stepsProgressText);

        stepsTextViewProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stepsProgress <= 90) {
                    stepsProgress += 10;
                    updateProgressBar();
                }
            }
        });

        //Water
        waterProgressBar = findViewById(R.id.waterProgressBar);
        waterTextViewProgress = findViewById(R.id.waterProgressText);

        waterTextViewProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (waterProgress <= 90) {
                    waterProgress += 10;
                    updateProgressBar();
                }
            }
        });

        //Sleep
        sleepProgressBar = findViewById(R.id.sleepProgressBar);
        sleepTextViewProgress = findViewById(R.id.sleepProgressText);

        sleepTextViewProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sleepProgress <= 90) {
                    sleepProgress += 10;
                    updateProgressBar();
                }
            }
        });

        //Veggies
        veggiesProgressBar = findViewById(R.id.veggiesProgressBar);
        veggiesTextViewProgress = findViewById(R.id.veggiesProgressText);

        veggiesTextViewProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (veggiesProgress <= 90) {
                    veggiesProgress += 10;
                    updateProgressBar();
                }
            }
        });

        updateProgressBar();
    }

    private void updateProgressBar() {
        stepsProgressBar.setProgress(stepsProgress);
        stepsTextViewProgress.setText("Steps\n" + stepsProgress + "%\n+");

        if(stepsProgress == 100 && stepsFlag){
            activitiesDoneToday++;
            stepsFlag = false;
        }

        waterProgressBar.setProgress(waterProgress);
        waterTextViewProgress.setText("Water\n" + waterProgress + "%\n+");

        if(waterProgress == 100 && waterFlag){
            activitiesDoneToday++;
            waterFlag = false;
        }

        sleepProgressBar.setProgress(sleepProgress);
        sleepTextViewProgress.setText("Sleep\n" + sleepProgress + "%\n+");

        if(sleepProgress == 100 && sleepFlag){
            activitiesDoneToday++;
            sleepFlag = false;
        }

        veggiesProgressBar.setProgress(veggiesProgress);
        veggiesTextViewProgress.setText("Veggies\n" + veggiesProgress + "%\n+");

        if(veggiesProgress == 100 && veggiesFlag){
            activitiesDoneToday++;
            veggiesFlag = false;
        }

        TextView todayProgress = (TextView) findViewById(R.id.todayProgressTextView);
        todayProgress.setText("Today: " + activitiesDoneToday + "/4");
    }
}