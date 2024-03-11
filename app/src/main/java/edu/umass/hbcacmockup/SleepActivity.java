package edu.umass.hbcacmockup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SleepActivity extends AppCompatActivity {

    public int totalPlaceholder; //TEMPORARY --> NEEDS TO BE PULLED FROM SETTINGS
    public long progressPlaceholder; //firebase data is assigned to this
    public TextView goalSentence;
    public TextView currentProgress;
    public TextView remainingProgress;
    public int itemsPerRow = 3;

    public ProgressBar progressSubpage;

    FirebaseUser user;
    FirebaseAuth auth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Date currentDate = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    String formattedDate = dateFormat.format(currentDate);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        DocumentReference dateRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
        dateRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                totalPlaceholder = task.getResult().getLong("sleepMax").intValue();
            }
        });

        updateSubpage();
        updateSleepColors();

    }

    public void clickedItem(View view){
        String taggedOfClicked = (String) view.getTag();
        Log.i("tagCheck", taggedOfClicked);
        DocumentReference sleepRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
        ImageView test = (ImageView) view;

        //turns grey icon orange
        if(taggedOfClicked.equals("notCompletedItemTag")){
            test.setImageResource(R.drawable.orangebed);
            test.setTag("completedItemTag");
            sleepRef.update("sleep", FieldValue.increment(1));
            progressPlaceholder++;
            updateSubpage();
        }

        /*
        //reverts orange icon to grey if user clicks again
        else if(taggedOfClicked.equals("completedItemTag")){
            test.setImageResource(R.drawable.greybed);
            test.setTag("notCompletedItemTag");
            sleepRef.update("sleep", FieldValue.increment(-1))
                    .addOnSuccessListener(aVoid -> {
                       progressPlaceholder--;
                       updateSubpage();
                    });
        }
        */
        updateSleepColors();

    }

    public void updateSleepColors(){
        LinearLayout mainLayout = findViewById(R.id.sleepRowHolderLinearLayout);
        mainLayout.removeAllViews(); // Clear the layout before adding new views

        DocumentReference dateRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
        dateRef.get().addOnCompleteListener(task ->{
            if(task.isSuccessful()){
                long progress = task.getResult().getLong("sleep");

                int numOfRows;
                boolean remainder = false;
                if(totalPlaceholder % itemsPerRow != 0){
                    numOfRows = (totalPlaceholder / itemsPerRow) + 1;
                    remainder = true;
                }else{
                    numOfRows = totalPlaceholder / itemsPerRow;
                }

                int completedItemsProcessed = 0;
                for(int i = 0; i < numOfRows; i++){
                    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    ConstraintLayout layout;
                    if(remainder && i == numOfRows - 1){
                        if(totalPlaceholder % itemsPerRow == 2){
                            layout = (ConstraintLayout) inflater.inflate(R.layout.sleeprow2, null);
                        }else{
                            layout = (ConstraintLayout) inflater.inflate(R.layout.sleeprow1, null);
                        }
                    }else{
                        layout = (ConstraintLayout) inflater.inflate(R.layout.sleeprow3, null);
                    }

                    for(int j = 1; j <= itemsPerRow; j++){
                        String idString = "sleepImageView" + j;
                        int resID = getResources().getIdentifier(idString, "id", getPackageName());
                        ImageView item = (ImageView) layout.findViewById(resID);

                        if(completedItemsProcessed < progress){
                            item.setImageResource(R.drawable.orangebed);
                            item.setTag("completedItemTag");
                            completedItemsProcessed++;
                        }
                    }
                    mainLayout.addView(layout);
                }
            }
        });

        //updates textViews and whatnots
        updateSubpage();
    }

    public void updateSubpage(){
        DocumentReference dateRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
        dateRef.get().addOnCompleteListener(task -> {
            progressPlaceholder = task.getResult().getLong("sleep");

            //update progress bar
            currentProgress = findViewById(R.id.currentBedsTextView);
            currentProgress.setText(Long.toString(progressPlaceholder));
            progressSubpage = findViewById(R.id.sleepProgressBarSubPage);
            double currentProgress = (double) progressPlaceholder / (double) totalPlaceholder;
            int progressInt = (int) (currentProgress * 100);
            progressSubpage.setProgress(progressInt);

            //updatae progress bar text
            TextView progressTextSubpage = (TextView) findViewById(R.id.sleepProgressTextSubpage);
            progressTextSubpage.setText("Sleep\n" + progressInt + "%");

            //update # of hours to sleep textView
            goalSentence = findViewById(R.id.goalSentenceTextView);
            goalSentence.setText("of " + totalPlaceholder + " hours of sleep");

            //update remaining progress textView
            remainingProgress = findViewById(R.id.remainingProgressSentenceTextView);

            //updates # of sleep left
            if(totalPlaceholder > progressPlaceholder) {
                remainingProgress.setText("You need to sleep " + (totalPlaceholder - progressPlaceholder) + " more hours to reach your goal");
            }
            else{
                remainingProgress.setText("You hit your sleep goal for the day!");
            }
        });
    }

    public void goSettings(View view){
        Intent intent = new Intent(SleepActivity.this,SettingsActivity.class);
        startActivity(intent);
    }

    public void goCalendar(View view){
        Intent intent = new Intent(SleepActivity.this,CalendarActivity.class);
        startActivity(intent);
    }

    public void goMain(View view){
        Intent intent = new Intent(SleepActivity.this,HomePage.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}