package edu.umass.hbcacmockup;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
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
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {
    Date currentDate = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    String formattedDate = dateFormat.format(currentDate);
    String dateSelected;
    TextInputEditText editTextNotes;
    TextView stepCount;
    TextView waterCount;
    TextView sleepCount;
    TextView veggiesCount;
    Button save;
    FirebaseUser user;
    FirebaseAuth auth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView seekBarStress;
    SeekBar stressBar;
    CalendarView calendar;
    int numStressLevel;
    ProgressBar stepBar;
    ProgressBar sleepBar;
    ProgressBar waterBar;
    ProgressBar veggiesBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        //establish firestore connection
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //default date
        dateSelected = formattedDate;

        //xml elements
        calendar = findViewById(R.id.calendarView);
        editTextNotes = findViewById(R.id.notes);
        save = findViewById(R.id.saveButton);
        seekBarStress = findViewById(R.id.seekBarText);
        stressBar = findViewById(R.id.feelBar);

        //user goals
        stepCount = findViewById(R.id.stepCount);
        sleepCount = findViewById(R.id.sleepCount);
        veggiesCount = findViewById(R.id.veggiesCount);
        waterCount = findViewById(R.id.waterCount);

        //progress bars
        stepBar = findViewById(R.id.stepBar);
        sleepBar = findViewById(R.id.sleepBar);
        veggiesBar = findViewById(R.id.veggiesBar);
        waterBar = findViewById(R.id.waterBar);

        //set stress bar max & default values
        stressBar.setMax(4);
        stressBar.setProgress(2);

        //update elements based on date selected
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                //format date
                month += 1;
                String updatedMonth = String.valueOf(month);
                String updatedDay = String.valueOf(dayOfMonth);

                if (month < 10) {
                    updatedMonth = "0" + updatedMonth;
                }

                if (dayOfMonth < 10) {
                    updatedDay = "0" + updatedDay;
                }

                String date = year + "-" + updatedMonth + "-" + updatedDay;
                dateSelected = date;

                DocumentReference docReference = db.collection("users").document(user.getUid()).collection("days").document(date);
                docReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                //set seekBar
                                long level = task.getResult().getLong("stressLevel");
                                int intLevel = (int) level;
                                stressBar.setProgress(intLevel);

                                //set goals' values
                                stepCount.setText("Steps: " + task.getResult().getLong("steps"));
                                sleepCount.setText("Sleep: " + task.getResult().getLong("sleep") + " hours");
                                veggiesCount.setText("Veggies: " + task.getResult().getLong("veggies") + " servings");
                                waterCount.setText("Water: " + task.getResult().getLong("water") + " oz");

                                //progress bar values
                                int stepsTemp = (int) (task.getResult().getLong("steps") / 100);
                                stepBar.setProgress(stepsTemp);
                                int sleepTemp = (int) (task.getResult().getLong("sleep") * 10);
                                sleepBar.setProgress(sleepTemp);
                                int waterTemp = (int) (task.getResult().getLong("water") * 10);
                                waterBar.setProgress(waterTemp);
                                int veggiesTemp = (int) (task.getResult().getLong("veggies") * 10);
                                veggiesBar.setProgress(veggiesTemp);

                                //set notes
                                editTextNotes.setText(task.getResult().getString("dailyNotes"));
                            } else {

                                //default values if date doesn't exist in cloud
                                stepCount.setText("Steps: 0");
                                sleepCount.setText("Sleep: 0 hours");
                                veggiesCount.setText("Veggies: 0 servings");
                                waterCount.setText("Water: 0 oz");

                                stepBar.setProgress(0);
                                sleepBar.setProgress(0);
                                waterBar.setProgress(0);
                                veggiesBar.setProgress(0);

                                stressBar.setProgress(2);
                                editTextNotes.setText("");
                            }
                        }
                    }
                });

            }
        });


        //user's goals & notes
        DocumentReference dateRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
        dateRef.get().addOnCompleteListener(task -> {

            //notes
            editTextNotes.setText(task.getResult().getString("dailyNotes"));

            //text values
            stepCount.setText("Steps: " + task.getResult().getLong("steps"));
            sleepCount.setText("Sleep: " + task.getResult().getLong("sleep") + " hours");
            veggiesCount.setText("Veggies: " + task.getResult().getLong("veggies") + " servings");
            waterCount.setText("Water: " + task.getResult().getLong("water") + " oz");

            //progress bar values
            int stepsTemp = (int) (task.getResult().getLong("steps") / 100);
            stepBar.setProgress(stepsTemp);
            int sleepTemp = (int) (task.getResult().getLong("sleep") * 10);
            sleepBar.setProgress(sleepTemp);
            int waterTemp = (int) (task.getResult().getLong("water") * 10);
            waterBar.setProgress(waterTemp);
            int veggiesTemp = (int) (task.getResult().getLong("veggies") * 10);
            veggiesBar.setProgress(veggiesTemp);
        });

        //seek bar
        stressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case 0:
                        seekBarStress.setTextColor(Color.rgb(144, 238, 144));
                        seekBarStress.setText("Completely relaxed");
                        numStressLevel = 0;
                        break;
                    case 1:
                        seekBarStress.setTextColor(Color.rgb(173, 216, 230));
                        seekBarStress.setText("Mildly relaxed");
                        numStressLevel = 1;
                        break;
                    case 2:
                        seekBarStress.setTextColor(Color.rgb(255, 255, 0));
                        seekBarStress.setText("Balanced");
                        numStressLevel = 2;
                        break;
                    case 3:
                        seekBarStress.setTextColor(Color.rgb(255,127,0));
                        seekBarStress.setText("Mildly stressed");
                        numStressLevel = 3;
                        break;
                    case 4:
                        seekBarStress.setTextColor(Color.rgb(255, 0,0));
                        seekBarStress.setText("Very stressed");
                        numStressLevel = 4;
                        break;
                    default:
                        seekBarStress.setText("Error");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //save progress button
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userNotes = String.valueOf(editTextNotes.getText());
                DocumentReference notesRef = db.collection("users").document(user.getUid()).collection("days").document(dateSelected);

                notesRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //set seekBar
                                notesRef.update("dailyNotes", userNotes);
                                notesRef.update("stressLevel", numStressLevel);
                                Toast.makeText(CalendarActivity.this, "Progress Saved", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = "No data recorded on " + dateSelected;
                                Toast.makeText(CalendarActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
            }
        });
    }

    public void goSettings(View view){
        Intent intent = new Intent(CalendarActivity.this,SettingsActivity.class);
        startActivity(intent);
    }

    public void goMain(View view){
        Intent intent = new Intent(CalendarActivity.this,HomePage.class);
        startActivity(intent);
    }
}