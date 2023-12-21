package edu.umass.hbcacmockup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class CalendarActivity extends AppCompatActivity {
    ImageView settings;
    ImageView home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        //click to view home page
        home = findViewById(R.id.lotusImageView);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });
        //click to view settings page
        settings = findViewById(R.id.calendarImageView);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}