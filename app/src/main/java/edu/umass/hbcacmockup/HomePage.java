package edu.umass.hbcacmockup;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.StructuredQuery;

import java.util.HashMap;
import java.util.Map;

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
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //get current date
    Date currentDate = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    String formattedDate = dateFormat.format(currentDate);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        //exit to login screen if user is not logged in
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        //store current date in a subcollection if not already present in database
        CollectionReference dayCollection = db.collection("users").document(user.getUid()).collection("days");

        dayCollection.whereEqualTo("date", formattedDate).get()
                .addOnCompleteListener(task -> {
                    QuerySnapshot querySnapshot = task.getResult();
                    if ((querySnapshot.isEmpty())) {
                        // store date and goals in the 'date' document
                        Map<String, Object> goals = new HashMap<>();
                        goals.put("date", formattedDate);
                        goals.put("steps", 0);
                        stepsProgressBar.setProgress(0);
                        goals.put("sleep", 0);
                        sleepProgressBar.setProgress(0);
                        goals.put("veggies", 0);
                        veggiesProgressBar.setProgress(0);
                        goals.put("water", 0);
                        waterProgressBar.setProgress(0);
                        goals.put("exercise", 0);
                        goals.put("food", 0);
                        goals.put("allGoalsMet", false);
                        goals.put("goalsMet", 0);
                        db.collection("users").document(user.getUid()).collection("days").document(formattedDate).set(goals);
                    }
                });

        setContentView(R.layout.activity_home_page);
        alignProgress(); //match firestore progress with progress bar

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
        //default video
        mVideoView = findViewById(R.id.bgVideoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_video);
        mVideoView.setVideoURI(uri);

        CollectionReference collectionRef = db.collection("users").document(user.getUid()).collection("days");
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int docNum = task.getResult().size();
                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                //set to default background if user's account is <3 days old
                if (docNum < 3){
                    mVideoView = findViewById(R.id.bgVideoView);
                    Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_video);
                    mVideoView.setVideoURI(uri);
                    mVideoView.start();
                }

                else if (docNum >= 7){
                    int counter = 0; //counter for number of times in last 7 days 4/4 goals not met
                    List<DocumentSnapshot> lastSevenDocuments = documents.subList(documents.size() - 7, documents.size()); //last 7 documents
                    for (DocumentSnapshot document : lastSevenDocuments) {
                        boolean allGoalsMet = document.getBoolean("allGoalsMet");
                        if(!allGoalsMet){
                            counter++;
                        }
                    }

                    if (counter >= 7){
                        //set background to ghost fish
                        mVideoView = findViewById(R.id.bgVideoView);
                        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ghostfishbg);
                        mVideoView.setVideoURI(uri);
                        mVideoView.start();
                    }
                    else if (counter >= 3){
                        //set background to colorless fish
                        mVideoView = findViewById(R.id.bgVideoView);
                        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.colorlessfishbg);
                        mVideoView.setVideoURI(uri);
                        mVideoView.start();
                    }
                    else if (counter < 3){
                        //set background to default
                        mVideoView = findViewById(R.id.bgVideoView);
                        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_video);
                        mVideoView.setVideoURI(uri);
                        mVideoView.start();
                    }
                }

                else if (docNum < 7){
                    int counter = 0; //counter for number of times in last 7 days user did not meet goal
                    List<DocumentSnapshot> lastThreeDocuments = documents.subList(documents.size() - 3, documents.size()); //last 3 documents
                    for (DocumentSnapshot document : lastThreeDocuments) {
                        boolean allGoalsMet = document.getBoolean("allGoalsMet");
                        if(allGoalsMet == false){
                            counter++;
                        }
                    }

                    if (counter >= 7){
                        //set background to ghost fish
                        mVideoView = findViewById(R.id.bgVideoView);
                        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ghostfishbg);
                        mVideoView.setVideoURI(uri);
                        mVideoView.start();
                    }
                    else if (counter >= 3){
                        //set background to colorless fish
                        mVideoView = findViewById(R.id.bgVideoView);
                        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.colorlessfishbg);
                        mVideoView.setVideoURI(uri);
                        mVideoView.start();
                    }
                    else if (counter < 3){
                        //set background to default
                        mVideoView = findViewById(R.id.bgVideoView);
                        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_video);
                        mVideoView.setVideoURI(uri);
                        mVideoView.start();
                    }
                }
            }
        });

        //loop bg video
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
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

                    //increment steps value by 1k in firestore
                    DocumentReference stepRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
                    stepRef.update("steps", FieldValue.increment(1000));
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

                    //increment water by 8 oz in firestore
                    DocumentReference stepRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
                    stepRef.update("water", FieldValue.increment(8));
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

                    //increment sleep by 1 hr in firestore
                    DocumentReference stepRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
                    stepRef.update("sleep", FieldValue.increment(1));
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

                    //increment veggies by 1 serving in firestore
                    DocumentReference stepRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
                    stepRef.update("veggies", FieldValue.increment(1));
                }
            }
        });

        updateProgressBar();
    }

    private void updateProgressBar() {
            stepsProgressBar.setProgress(stepsProgress);
            stepsTextViewProgress.setText("Steps\n" + stepsProgress + "%\n+");

            if (stepsProgress == 100 && stepsFlag) {
                activitiesDoneToday++;

                stepsFlag = false;
            }

            waterProgressBar.setProgress(waterProgress);
            waterTextViewProgress.setText("Water\n" + waterProgress + "%\n+");

            if (waterProgress == 100 && waterFlag) {
                activitiesDoneToday++;
                waterFlag = false;
            }

            sleepProgressBar.setProgress(sleepProgress);
            sleepTextViewProgress.setText("Sleep\n" + sleepProgress + "%\n+");

            if (sleepProgress == 100 && sleepFlag) {
                activitiesDoneToday++;
                sleepFlag = false;
            }

            veggiesProgressBar.setProgress(veggiesProgress);
            veggiesTextViewProgress.setText("Veggies\n" + veggiesProgress + "%\n+");

            if (veggiesProgress == 100 && veggiesFlag) {
                activitiesDoneToday++;
                veggiesFlag = false;
            }

            //update the 'goalsMet' boolean if all goals are met
            if (activitiesDoneToday == 4) {
                DocumentReference goalsMetRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
                goalsMetRef.update("allGoalsMet", true);
            }

            TextView todayProgress = (TextView) findViewById(R.id.todayProgressTextView);
            todayProgress.setText("Today: " + activitiesDoneToday + "/4");
            DocumentReference goalsMetRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
            goalsMetRef.update("goalsMet", activitiesDoneToday);
        }


    //aligning firestore data with progress bars
    private void alignProgress() {

        DocumentReference dateRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
        dateRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {

                //update step count
                if (task.getResult().getLong("steps") == 0){
                    stepsProgressBar.setProgress(0);
                }
                else {
                    long stepCount = task.getResult().getLong("steps");
                    stepsProgress += (stepCount) / 100;
                    updateProgressBar();
                }

                //update sleep
                if(task.getResult().getLong("sleep") == 0) {
                    sleepProgressBar.setProgress(0);
                }
                else {
                    long sleepCount = task.getResult().getLong("sleep");
                    sleepProgress += (sleepCount) * 10;
                    updateProgressBar();
                }

                //update veggies
                if(task.getResult().getLong("veggies") == 0) {
                    veggiesProgressBar.setProgress(0);
                }
                else {
                    long vegCount = task.getResult().getLong("veggies");
                    veggiesProgress += (vegCount) * 10;
                    updateProgressBar();
                }

                //update water
                if(task.getResult().getLong("water") == 0) {
                    waterProgressBar.setProgress(0);
                }
                else {
                    long waterCount = task.getResult().getLong("water");
                    waterProgress += ((waterCount) / 8) * 10;
                    updateProgressBar();
                }
            }
        });
    }
}