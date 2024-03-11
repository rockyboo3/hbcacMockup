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

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VeggieActivity extends AppCompatActivity {

    public int totalPlaceholder; //TEMPORARY --> NEEDS TO BE PULLED FROM SETTINGS

    public long progressPlaceholder; //TEMPORARY --> STARTS AT 0 EACH DAY

    public int itemsPerRow = 3;

    public ProgressBar progressSubpage;
    TextView goalSentence;
    TextView remainingProgress;

    FirebaseUser user;
    FirebaseAuth auth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView currentProgress;

    Date currentDate = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    String formattedDate = dateFormat.format(currentDate);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veggie);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        DocumentReference dateRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
        dateRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                totalPlaceholder = task.getResult().getLong("veggiesMax").intValue();
            }
        });

        updateSubpage();
        updateVeggieColors();
    }

    public void clickedItem(View view){
        String taggedOfClicked = (String) view.getTag();
        Log.i("tagCheck", taggedOfClicked);
        DocumentReference veggieRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
        ImageView test = (ImageView) view;

        //turns grey icon green
        if(taggedOfClicked.equals("notCompletedItemTag")){
            test.setImageResource(R.drawable.greenveggie);
            test.setTag("completedItemTag");
            veggieRef.update("veggies", FieldValue.increment(1));
            progressPlaceholder++;
            updateSubpage();
        }

        /*
        //reverts green cup to grey if user clicks again
        else if(taggedOfClicked.equals("completedItemTag")){
            test.setImageResource(R.drawable.greyveggie);
            test.setTag("notCompletedItemTag");
            veggieRef.update("veggies", FieldValue.increment(-1))
                    .addOnSuccessListener(aVoid -> {
                       progressPlaceholder--;
                       updateSubpage();
                    });
        }
        */
        updateVeggieColors();

    }


    public void updateVeggieColors(){
        LinearLayout mainLayout = findViewById(R.id.veggieRowHolderLinearLayout);
        mainLayout.removeAllViews(); // Clear the layout before adding new views

        DocumentReference dateRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
        dateRef.get().addOnCompleteListener(task ->{
           if(task.isSuccessful()){
               long progress = task.getResult().getLong("veggies");

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
                           layout = (ConstraintLayout) inflater.inflate(R.layout.veggierow2, null);
                       }else{
                           layout = (ConstraintLayout) inflater.inflate(R.layout.veggierow1, null);
                       }
                   }else{
                       layout = (ConstraintLayout) inflater.inflate(R.layout.veggierow3, null);
                   }

                   for(int j = 1; j <= itemsPerRow; j++){
                       String idString = "veggieImageView" + j;
                       int resID = getResources().getIdentifier(idString, "id", getPackageName());
                       ImageView item = (ImageView) layout.findViewById(resID);

                       if(completedItemsProcessed < progress){
                           item.setImageResource(R.drawable.greenveggie);
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
    //*********************************UPDATE VEGGIES******************************************//
    public void updateSubpage(){
        DocumentReference dateRef = db.collection("users").document(user.getUid()).collection("days").document(formattedDate);
        dateRef.get().addOnCompleteListener(task -> {
            progressPlaceholder = task.getResult().getLong("veggies");

            currentProgress = findViewById(R.id.currentVeggiesTextView);
            currentProgress.setText(Long.toString(progressPlaceholder));
            progressSubpage = findViewById(R.id.veggieProgressBarSubPage);
            double currentProgress = (double) progressPlaceholder / (double) totalPlaceholder;
            int progressInt = (int) (currentProgress * 100);
            progressSubpage.setProgress(progressInt);

            TextView progressTextSubpage = (TextView) findViewById(R.id.veggieProgressTextSubpage);
            progressTextSubpage.setText("Veggies\n" + progressInt + "%");

            //update # of veggies to eat textView
            goalSentence = findViewById(R.id.goalSentenceTextView);
            goalSentence.setText("of " + totalPlaceholder + " cups of vegetables");

            //update remaining progress textView
            remainingProgress = findViewById(R.id.remainingProgressSentenceTextView);

            if(totalPlaceholder > progressPlaceholder) {
                remainingProgress.setText("You need to eat " + (totalPlaceholder - progressPlaceholder) + " more cups of vegetables to reach your goal");
            }
            else{
                remainingProgress.setText("You hit your vegetable goal for the day!");
            }
        });

    }

    public void goSettings(View view){
        Intent intent = new Intent(VeggieActivity.this,SettingsActivity.class);
        startActivity(intent);
    }

    public void goCalendar(View view){
        Intent intent = new Intent(VeggieActivity.this,CalendarActivity.class);
        startActivity(intent);
    }

    public void goMain(View view){
        Intent intent = new Intent(VeggieActivity.this,HomePage.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}