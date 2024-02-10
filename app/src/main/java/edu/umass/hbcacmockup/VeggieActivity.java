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

public class VeggieActivity extends AppCompatActivity {

    public int totalPlaceholder = 8; //TEMPORARY --> NEEDS TO BE PULLED FROM SETTINGS

    public int progressPlaceholder = 6; //TEMPORARY --> STARTS AT 0 EACH DAY

    public int itemsPerRow = 3;

    public ProgressBar progressSubpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veggie);

        //*********************************PROGRESS BAR******************************************//

        progressSubpage = findViewById(R.id.veggieProgressBarSubPage);
        double currentProgress = (double) progressPlaceholder / (double) totalPlaceholder;
        int progressInt = (int) (currentProgress * 100);
        progressSubpage.setProgress(progressInt);

        TextView progressTextSubpage = (TextView) findViewById(R.id.veggieProgressTextSubpage);
        progressTextSubpage.setText("Veggies\n" + progressInt + "%");

        //*********************************VEGGIES******************************************//

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.veggieRowHolderLinearLayout);

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

            ConstraintLayout threeLayout = (ConstraintLayout) inflater.inflate(R.layout.veggierow3, null);
            ConstraintLayout twoLayout = (ConstraintLayout) inflater.inflate(R.layout.veggierow2, null);
            ConstraintLayout oneLayout = (ConstraintLayout) inflater.inflate(R.layout.veggierow1, null);

            if(remainder && i == numOfRows - 1){ //last row if not 3 full veggies
                if(totalPlaceholder % itemsPerRow == 2){
                    if(completedItemsProcessed < progressPlaceholder){
                        if(progressPlaceholder - completedItemsProcessed == 1){
                            ImageView item = (ImageView) twoLayout.findViewById(R.id.veggieImageView1);
                            item.setImageResource(R.drawable.greenveggie);
                            item.setTag("completedItemTag");
                            completedItemsProcessed++;
                        }else{
                            ImageView item1 = (ImageView) twoLayout.findViewById(R.id.veggieImageView1);
                            item1.setImageResource(R.drawable.greenveggie);
                            item1.setTag("completedItemTag");
                            ImageView item2 = (ImageView) twoLayout.findViewById(R.id.veggieImageView2);
                            item2.setImageResource(R.drawable.greenveggie);
                            item2.setTag("completedItemTag");
                            completedItemsProcessed += 2;
                        }
                    }
                    mainLayout.addView(twoLayout);
                }else if(totalPlaceholder % itemsPerRow == 1){
                    if(completedItemsProcessed < progressPlaceholder){
                        if(progressPlaceholder - completedItemsProcessed == 1){
                            ImageView item = (ImageView) oneLayout.findViewById(R.id.veggieImageView1);
                            item.setImageResource(R.drawable.greenveggie);
                            item.setTag("completedItemTag");
                            completedItemsProcessed++;
                        }
                    }
                    mainLayout.addView(oneLayout);
                }
            }else{
                if (completedItemsProcessed < progressPlaceholder){
                    if(progressPlaceholder - completedItemsProcessed >= 3){
                        for(int j = 1; j <= 3; j++){
                            String idString = "veggieImageView"+j;
                            int resID = getResources().getIdentifier(idString, "id", getPackageName());
                            ImageView item = (ImageView) threeLayout.findViewById(resID);
                            item.setImageResource(R.drawable.greenveggie);
                            item.setTag("completedItemTag");
                            completedItemsProcessed++;
                        }
                    }else{
                        int completedBeforeRun = completedItemsProcessed;
                        for(int j = 1; j <= progressPlaceholder - completedBeforeRun; j++){
                            String idString = "veggieImageView"+j;
                            int resID = getResources().getIdentifier(idString, "id", getPackageName());
                            ImageView item = (ImageView) threeLayout.findViewById(resID);
                            item.setImageResource(R.drawable.greenveggie);
                            item.setTag("completedItemTag");
                            completedItemsProcessed++;
                        }
                    }
                }
                mainLayout.addView(threeLayout);
            }
        }
    }

    public void clickedItem(View view){
        String taggedOfClicked = (String) view.getTag();
        Log.i("tagCheck", taggedOfClicked);
        if(taggedOfClicked.equals("notCompletedItemTag")){
            ImageView test = (ImageView) view;
            test.setImageResource(R.drawable.greenveggie);
        }
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
        Intent intent = new Intent(VeggieActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}