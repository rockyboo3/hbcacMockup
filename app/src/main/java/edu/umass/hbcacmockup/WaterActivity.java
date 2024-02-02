package edu.umass.hbcacmockup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class WaterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);
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

}