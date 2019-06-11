package com.orbital19.imabip;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.models.User;

import java.util.ArrayList;


public class Host extends AppCompatActivity {
    private EditText inName, inType, inVenue, inMonth, inDate, inAMorPM, inDesc, inFilled, inPax;
    private Button hostBtn;
    private String[] inputs = new String[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Start an event!");


        inName = findViewById(R.id.input_name);
        inType = findViewById(R.id.input_type);
        inVenue = findViewById(R.id.input_venue);
        inMonth = findViewById(R.id.input_month);
        inDate = findViewById(R.id.input_date);
        inAMorPM = findViewById(R.id.input_AM_or_PM);
        inDesc = findViewById(R.id.input_description);
        inFilled = findViewById(R.id.input_filled);
        inPax = findViewById(R.id.input_pax);


        hostBtn = findViewById(R.id.host_button);

        hostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputs[0] = inName.getText().toString();
                inputs[1] = inType.getText().toString();
                inputs[2] = inVenue.getText().toString();
                inputs[3] = inMonth.getText().toString();
                inputs[4] = inDate.getText().toString();
                inputs[5] = inAMorPM.getText().toString();
                inputs[6] = inDesc.getText().toString();
                inputs[7] = inFilled.getText().toString();
                inputs[8] = inPax.getText().toString();

                boolean valid = false;

                while (!valid) {
                    try {
                        addNewEvent();
                        valid = true;
                    } catch (Exception e) {
                        faultyToast();
                        valid = false;
                    }
                }

                addedToast();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    private void faultyToast() {
        Toast.makeText(getApplicationContext(), "Invalid Data", Toast.LENGTH_LONG).show();
    }

    private boolean isLegitLetter(char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z');
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void addNewEvent() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        fs.collection(User.usersCollection).document(currentUser.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();

                ArrayList<String> contact = new ArrayList<>();
                contact.add((String) doc.get(User.emailKey));
                contact.add((String) doc.get(User.phoneKey));

                Event ev = new Event(contact, inputs[4], (String) doc.get(User.idKey), inputs[0],
                        inputs[1], inputs[2], inputs[3] + "at" + inputs[4] + inputs[5],
                        Long.parseLong(inputs[8]), Long.parseLong(inputs[7]));

                ev.createEntry();

            }
        });



    }

    private void addedToast() {
        Toast.makeText(getApplicationContext(), "Game hosted successfully!", Toast.LENGTH_LONG)
            .show();
    }
}


