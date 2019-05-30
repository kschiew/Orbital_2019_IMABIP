package com.orbital19.imabip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.models.Event;

public class Host extends AppCompatActivity {
    private EditText inName, inVenue, inTime, inDesc, inFilled, inPax;
    private Button hostBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Start an event!");

        inName = findViewById(R.id.input_name);
        inVenue = findViewById(R.id.input_venue);
        inTime = findViewById(R.id.input_time);
        inDesc = findViewById(R.id.input_description);
        inFilled = findViewById(R.id.input_filled);
        inPax = findViewById(R.id.input_pax);

        hostBtn = findViewById(R.id.host_button);
        hostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewEvent();
                addedToast();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    private void addNewEvent() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        Event ev;
    }

    private void addedToast() {
    }
}
