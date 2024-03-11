package edu.umass.hbcacmockup;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StepActivity2 extends AppCompatActivity implements SensorEventListener {


    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private int stepCount = 0;
    private ProgressBar progressSubpage;
    private boolean isPause = false;
    private int stepCountTarget = 10000;
    private long startTime;
    private TextView stepCounterTextView;

    @Override
    protected void onResume(){
        super.onResume();
        if(stepCounterSensor != null){
            sensorManager.registerListener(this,stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2);

        //*********************************PROGRESS BAR******************************************//

        progressSubpage = findViewById(R.id.stepProgressBarSubPage);

        stepCounterTextView = (TextView) findViewById(R.id.stepProgressTextSubpage);

        startTime = System.currentTimeMillis();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        progressSubpage.setMax(stepCountTarget);

        if(stepCounterSensor == null){
            stepCounterTextView.setText("Step counter not available");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
         if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
             stepCount = (int) sensorEvent.values[0];
             stepCounterTextView.setText("Step Count : + stepCount");
             progressSubpage.setProgress(stepCount);

             if(stepCount >= stepCountTarget){
                 progressSubpage.setProgress(stepCountTarget);
             }
         }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void goSettings(View view){
        Intent intent = new Intent(StepActivity2.this,SettingsActivity.class);
        startActivity(intent);
    }

    public void goCalendar(View view){
        Intent intent = new Intent(StepActivity2.this,CalendarActivity.class);
        startActivity(intent);
    }

    public void goMain(View view){
        Intent intent = new Intent(StepActivity2.this,HomePage.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}