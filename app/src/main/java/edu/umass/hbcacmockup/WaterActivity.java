package edu.umass.hbcacmockup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WaterActivity extends AppCompatActivity {

    public int cupsOfWaterPlaceholder = 8; //TEMPORARY --> NEEDS TO BE PULLED FROM SETTINGS

    public int progressNumOfCupsPlaceholder = 6; //TEMPORARY --> STARTS AT 0 EACH DAY

    public int cupsPerRow = 3;

    public ProgressBar waterProgressSubpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        //*********************************PROGRESS BAR******************************************//

        waterProgressSubpage = findViewById(R.id.waterProgressBarSubPage);
        double currentProgress = (double) progressNumOfCupsPlaceholder / (double) cupsOfWaterPlaceholder;
        int progressInt = (int) (currentProgress * 100);
        waterProgressSubpage.setProgress(progressInt);

        TextView waterProgressTextSubpage = (TextView) findViewById(R.id.waterProgressTextSubpage);
        waterProgressTextSubpage.setText("Water\n" + progressInt + "%");

        //*********************************CUPS******************************************//

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.waterRowHolderLinearLayout);

        int numOfRows;
        boolean remainder = false;
        if(cupsOfWaterPlaceholder % cupsPerRow != 0){
            numOfRows = (cupsOfWaterPlaceholder / cupsPerRow) + 1;
            remainder = true;
        }else{
            numOfRows = cupsOfWaterPlaceholder / cupsPerRow;
        }

        int fullCupsProcessed = 0;
        for(int i = 0; i < numOfRows; i++){
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            ConstraintLayout threeCupsLayout = (ConstraintLayout) inflater.inflate(R.layout.waterrow3, null);
            ConstraintLayout twoCupLayout = (ConstraintLayout) inflater.inflate(R.layout.waterrow2, null);
            ConstraintLayout oneCupLayout = (ConstraintLayout) inflater.inflate(R.layout.waterrow1, null);

            if(remainder && i == numOfRows - 1){ //last row if not 3 full cups
                if(cupsOfWaterPlaceholder % cupsPerRow == 2){
                    if(fullCupsProcessed < progressNumOfCupsPlaceholder){
                        if(progressNumOfCupsPlaceholder - fullCupsProcessed == 1){
                            ImageView cup = (ImageView) twoCupLayout.findViewById(R.id.cupImageView1);
                            cup.setImageResource(R.drawable.bluecup);
                            cup.setTag("blueCup");
                            fullCupsProcessed++;
                        }else{
                            ImageView cup1 = (ImageView) twoCupLayout.findViewById(R.id.cupImageView1);
                            cup1.setImageResource(R.drawable.bluecup);
                            cup1.setTag("blueCup");
                            ImageView cup2 = (ImageView) twoCupLayout.findViewById(R.id.cupImageView2);
                            cup2.setImageResource(R.drawable.bluecup);
                            cup2.setTag("blueCup");
                            fullCupsProcessed += 2;
                        }
                    }
                    mainLayout.addView(twoCupLayout);
                }else if(cupsOfWaterPlaceholder % cupsPerRow == 1){
                    if(fullCupsProcessed < progressNumOfCupsPlaceholder){
                        if(progressNumOfCupsPlaceholder - fullCupsProcessed == 1){
                            ImageView cup = (ImageView) oneCupLayout.findViewById(R.id.cupImageView1);
                            cup.setImageResource(R.drawable.bluecup);
                            cup.setTag("blueCup");
                            fullCupsProcessed++;
                        }
                    }
                    mainLayout.addView(oneCupLayout);
                }
            }else{
                if (fullCupsProcessed < progressNumOfCupsPlaceholder){
                    if(progressNumOfCupsPlaceholder - fullCupsProcessed >= 3){
                        for(int j = 1; j <= 3; j++){
                            String idString = "cupImageView"+j;
                            int resID = getResources().getIdentifier(idString, "id", getPackageName());
                            ImageView cup = (ImageView) threeCupsLayout.findViewById(resID);
                            cup.setImageResource(R.drawable.bluecup);
                            cup.setTag("blueCup");
                            fullCupsProcessed++;
                        }
                    }else{
                        int fullCupsBeforeRun = fullCupsProcessed;
                        for(int j = 1; j <= progressNumOfCupsPlaceholder - fullCupsBeforeRun; j++){
                            String idString = "cupImageView"+j;
                            int resID = getResources().getIdentifier(idString, "id", getPackageName());
                            ImageView cup = (ImageView) threeCupsLayout.findViewById(resID);
                            cup.setImageResource(R.drawable.bluecup);
                            cup.setTag("blueCup");
                            fullCupsProcessed++;
                        }
                    }
                }
                mainLayout.addView(threeCupsLayout);
            }
        }
    }

    public void clickedItem(View view){
        String taggedOfClicked = (String) view.getTag();
        Log.i("tagCheck", taggedOfClicked);
        if(taggedOfClicked.equals("greyCup")){
            ImageView test = (ImageView) view;
            test.setImageResource(R.drawable.bluecup);
        }
    }

    public void goSettings(View view){
        Intent intent = new Intent(WaterActivity.this,SettingsActivity.class);
        startActivity(intent);
    }

    public void goCalendar(View view){
        Intent intent = new Intent(WaterActivity.this,CalendarActivity.class);
        startActivity(intent);
    }

    public void goMain(View view){
        Intent intent = new Intent(WaterActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}