package edu.umass.hbcacmockup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
    }

    public void goSettings(View view){
        Intent intent = new Intent(CalendarActivity.this,SettingsActivity.class);
        startActivity(intent);
    }

    public void goMain(View view){
        Intent intent = new Intent(CalendarActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}