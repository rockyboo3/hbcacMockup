package edu.umass.hbcacmockup;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private TextView foodAvailable;
    private long fishFood = 0;
    private int waterProgress = 0;
    private int sleepProgress = 0;
    private int veggiesProgress = 0;
    private double activitiesDoneToday = 0;

    private boolean stepsFlag = true;
    private boolean waterFlag = true;
    private boolean sleepFlag = true;
    private boolean veggiesFlag = true;
    private boolean steps50Flag = true;
    private boolean water50Flag = true;
    private boolean sleep50Flag = true;
    private boolean veggies50Flag = true;

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
    Uri currentBgUri;
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
                        goals.put("numGoalsMet", 0);
                        goals.put("food", 0);
                        goals.put("allGoalsMet", false);
                        goals.put("fishAte", 0);
                        db.collection("users").document(user.getUid()).collection("days").document(formattedDate).set(goals);
                    }
                });

        setContentView(R.layout.activity_home_page);
        alignProgress(); //match firestore progress with progress bars & textviews

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
        currentBgUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_video);
        mVideoView.setVideoURI(currentBgUri);

        CollectionReference collectionRef = db.collection("users").document(user.getUid()).collection("days");
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int docNum = task.getResult().size();
                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                //set to default background if user's account is <3 days old
                if (docNum < 3){
                    mVideoView = findViewById(R.id.bgVideoView);
                    currentBgUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_video);
                    mVideoView.setVideoURI(currentBgUri);
                    mVideoView.start();
                }

                else if (docNum >= 7){
                    int counter = 0; //counter for number of times in last 7 days 4/4 goals not met
                    List<DocumentSnapshot> lastSevenDocuments = documents.subList(documents.size() - 7, documents.size()); //last 7 documents
                    for (DocumentSnapshot document : lastSevenDocuments) {
                        long numFishAte = document.getLong("fishAte");
                        if(numFishAte < 4){
                            counter++;
                        }
                    }
                    if (counter >= 7){
                        //set background to ghost fish
                        mVideoView = findViewById(R.id.bgVideoView);
                        currentBgUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ghostfishbg);
                        mVideoView.setVideoURI(currentBgUri);
                        mVideoView.start();
                    }
                    else if (counter >= 3){
                        //set background to colorless fish
                        mVideoView = findViewById(R.id.bgVideoView);
                        currentBgUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.colorlessfishbg);
                        mVideoView.setVideoURI(currentBgUri);
                        mVideoView.start();
                    }
                    else if (counter < 3){
                        //set background to default
                        mVideoView = findViewById(R.id.bgVideoView);
                        currentBgUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_video);
                        mVideoView.setVideoURI(currentBgUri);
                        mVideoView.start();
                    }
                }

                else if (docNum < 7){
                    int counter = 0; //counter for number of times in last 7 days user did not meet goal
                    List<DocumentSnapshot> lastThreeDocuments = documents.subList(documents.size() - 3, documents.size()); //last 3 documents
                    for (DocumentSnapshot document : lastThreeDocuments) {
                        long numFishAte = document.getLong("fishAte");
                        if(numFishAte < 4){
                            counter++;
                        }
                    }
                    if (counter >= 3){
                        //set background to colorless fish
                        mVideoView = findViewById(R.id.bgVideoView);
                        currentBgUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.colorlessfishbg);
                        mVideoView.setVideoURI(currentBgUri);
                        mVideoView.start();
                    }
                    else if (counter < 3){
                        //set background to default
                        mVideoView = findViewById(R.id.bgVideoView);
                        currentBgUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_video);
                        mVideoView.setVideoURI(currentBgUri);
                        mVideoView.start();
                    }
                }
            }
        });

        //loop video
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.setVideoURI(currentBgUri);
                mVideoView.start();
            }
        });
        /////////////////////////////////////////////////////////////////////

        feedFish(); //feed fish button

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
                    stepRef.update("water", FieldValue.increment(1));
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
            DocumentReference goalRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
            stepsProgressBar.setProgress(stepsProgress);
            stepsTextViewProgress.setText("Steps\n" + stepsProgress + "%\n+");

            if (stepsProgress == 100 && stepsFlag) {
                activitiesDoneToday++;
                fishFood++;
                stepsFlag = false;
            }
            else if(stepsProgress == 50 && steps50Flag){
                fishFood++;
                steps50Flag = false;
            }

            waterProgressBar.setProgress(waterProgress);
            waterTextViewProgress.setText("Water\n" + waterProgress + "%\n+");

            if (waterProgress == 100 && waterFlag) {
                activitiesDoneToday++;
                fishFood++;
                waterFlag = false;
            }
            else if(waterProgress == 50 && water50Flag){
                fishFood++;
                water50Flag = false;
            }

            sleepProgressBar.setProgress(sleepProgress);
            sleepTextViewProgress.setText("Sleep\n" + sleepProgress + "%\n+");

            if (sleepProgress == 100 && sleepFlag) {
                activitiesDoneToday++;
                fishFood++;
                sleepFlag = false;
            }
            else if(sleepProgress == 50 && sleep50Flag){
                fishFood++;
                sleep50Flag = false;
            }

            veggiesProgressBar.setProgress(veggiesProgress);
            veggiesTextViewProgress.setText("Veggies\n" + veggiesProgress + "%\n+");

            if (veggiesProgress == 100 && veggiesFlag) {
                activitiesDoneToday++;
                fishFood++;
                veggiesFlag = false;
            }
            else if(veggiesProgress == 50 && veggies50Flag){
                fishFood++;
                veggies50Flag = false;
            }

            //update the 'allGoalsMet' boolean
            if (activitiesDoneToday == 4) {
                DocumentReference goalsMetRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
                goalsMetRef.update("allGoalsMet", true);
            }
            //update 'food' and  'numGoalsMet' fields & texts
            DocumentReference goalsMetRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
            goalsMetRef.update("food", fishFood);
            goalsMetRef.update("numGoalsMet", activitiesDoneToday);
            updateTextView();

        }

    //aligning firestore data with progress bars
    private void alignProgress() {
        DocumentReference dateRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);

        dateRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {

                long steps = task.getResult().getLong("steps");
                long sleep = task.getResult().getLong("sleep");
                long water = task.getResult().getLong("water");
                long veggies = task.getResult().getLong("veggies");
                fishFood = task.getResult().getLong("food");
                long numFishAte = task.getResult().getLong("fishAte");

                //update step count
                if (steps == 0){
                    stepsProgressBar.setProgress(0);
                }
                else if(steps == 10000){
                    stepsProgressBar.setProgress(100);
                    stepsProgress = 100;
                    activitiesDoneToday++;
                    fishFood += 2;
                    stepsFlag = false;
                    steps50Flag = false;
                    updateProgressBar();
                }
                else if(steps >= 5000 && steps < 10000){
                    steps50Flag = false;
                    fishFood++;
                    stepsProgress += (steps) / 100;
                    stepsProgressBar.setProgress(stepsProgress);
                    updateProgressBar();
                }
                else {
                    stepsProgress += (steps) / 100;
                    stepsProgressBar.setProgress(stepsProgress);
                    updateProgressBar();
                }

                //update sleep
                if(sleep == 0) {
                    sleepProgressBar.setProgress(0);
                }
                else if(sleep == 10){
                    sleepProgressBar.setProgress(100);
                    sleepProgress = 100;
                    activitiesDoneToday++;
                    fishFood += 2;
                    sleepFlag = false;
                    sleep50Flag = false;
                    updateProgressBar();
                }
                else if(sleep >= 5 && sleep < 10){
                    sleep50Flag = false;
                    sleepProgress += sleep * 10;
                    fishFood++;
                    sleepProgressBar.setProgress(sleepProgress);
                    updateProgressBar();
                }
                else {
                    sleepProgress += (sleep) * 10;
                    sleepProgressBar.setProgress(sleepProgress);
                    updateProgressBar();
                }

                //update veggies count
                if (veggies == 0){
                    veggiesProgressBar.setProgress(0);
                }
                else if(veggies == 10){
                    veggiesProgressBar.setProgress(100);
                    veggiesProgress = 100;
                    activitiesDoneToday++;
                    fishFood += 2;
                    veggiesFlag = false;
                    veggies50Flag = false;
                    updateProgressBar();
                }
                else if(veggies >= 5 && veggies < 10){
                    veggies50Flag = false;
                    veggiesProgress += veggies * 10;
                    fishFood++;
                    veggiesProgressBar.setProgress(veggiesProgress);
                    updateProgressBar();
                }
                else {
                    veggiesProgress += veggies * 10;
                    veggiesProgressBar.setProgress(veggiesProgress);
                    updateProgressBar();
                }

                //update water
                if(water == 0) {
                    waterProgressBar.setProgress(0);
                }
                else if(water == 10){
                    waterFlag = false;
                    water50Flag = false;
                    fishFood += 2;
                    waterProgressBar.setProgress(100);
                    waterProgress = 100;
                    activitiesDoneToday++;
                    updateProgressBar();
                }
                else if(water >= 5 && water < 10){
                    water50Flag = false;
                    waterProgress += water * 10;
                    fishFood++;
                    waterProgressBar.setProgress(waterProgress);
                    updateProgressBar();
                }
                else {
                    waterProgress += water * 10;
                    waterProgressBar.setProgress(waterProgress);
                    updateProgressBar();
                }
                updateTextView();
                dateRef.update("food", FieldValue.increment(-numFishAte));
            }

        });
    }

    //feed fish button
    private void feedFish(){
        Button feed = findViewById(R.id.feedButton);
        VideoView bgVideo = findViewById(R.id.bgVideoView);
        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.feeding_fish_bg;
                Uri uri = Uri.parse(videoPath);
                DocumentReference dateRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
                dateRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        //check if user has enough food
                        if (fishFood >= 1) {
                            //set fish feeding video
                            bgVideo.setVideoURI(uri);
                            bgVideo.start();
                            dateRef.update("fishAte", FieldValue.increment(1));
                            dateRef.update("food", FieldValue.increment(-1));
                            fishFood -= 1;
                            updateProgressBar();

                            //migrate to updated instance of database
                            DocumentReference dateRefUpdated = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
                            dateRefUpdated.get().addOnCompleteListener(taskUpdated -> {
                                bgVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    //revert bg
                                    public void onCompletion(MediaPlayer mp) {
                                        if (taskUpdated.getResult().getLong("fishAte") > 3) {
                                            currentBgUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_video);
                                            mVideoView.setVideoURI(currentBgUri);
                                            mVideoView.start(); //loop healthy fish video if fish was fed 4 times
                                        } else {
                                            mVideoView.setVideoURI(currentBgUri);
                                            mVideoView.start(); //loop current bg if user hasn't met goals
                                        }
                                    }
                                });
                            });
                        }
                    }
                });
            }
        });
    }

    //updates 'Today:' and 'Food Available:' texts
    private void updateTextView(){
        DocumentReference dateRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
        dateRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                TextView todayProgress = (TextView) findViewById(R.id.todayProgressTextView);
                foodAvailable = findViewById(R.id.foodAvailableTextView);

                //update activities completed
                long tempProgress = task.getResult().getLong("numGoalsMet");
                todayProgress.setText("Today: " + tempProgress + "/4");

                //update food available
                long tempFood = task.getResult().getLong("food");
                foodAvailable.setText("Food Available: " + tempFood + "/8");
            }
        });
    }
}