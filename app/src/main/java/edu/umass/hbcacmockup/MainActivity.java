package edu.umass.hbcacmockup;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import edu.umass.hbcacmockup.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
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

    //for nav
    //ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //connect nav
        //binding = ActivityMainBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
        //////
        //setContentView(R.layout.activity_main);

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