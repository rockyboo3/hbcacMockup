package edu.umass.hbcacmockup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button button;
    TextView textview;
    FirebaseUser user;
    ImageView home;
    ListView list;
    ImageView calendar;
    String[] settingsOptions = {"Steps", "Sleep", "Water", "Vegetables", "Exercise"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textview = findViewById(R.id.user_details);
        user = auth.getCurrentUser();
        if (user == null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            textview.setText(user.getEmail());
        }

        //show options for settings
        list = findViewById(R.id.settingsListView);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(
                this, android.R.layout.simple_list_item_1, settingsOptions
        );

        list.setAdapter(arrayAdapter);

        //click to view home page
        home = findViewById(R.id.lotusImageView);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });
        //click to view calendar page
        calendar = findViewById(R.id.calendarImageView);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, CalendarActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}