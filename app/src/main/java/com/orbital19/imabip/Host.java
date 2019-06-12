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


import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.models.User;

import java.util.ArrayList;

import io.opencensus.internal.StringUtils;


public class Host extends AppCompatActivity {
    private EditText inName, inType, inVenue, inMonth, inDate, inHours, inAMorPM, inDesc, inFilled, inPax;
    private Button hostBtn;
    private String[] inputs = new String[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Start a game!");


        inName = findViewById(R.id.input_name);
        inType = findViewById(R.id.input_type);
        inVenue = findViewById(R.id.input_venue);
        inMonth = findViewById(R.id.input_month);
        inDate = findViewById(R.id.input_date);
        inHours = findViewById(R.id.input_hours);
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
                inputs[3] = inMonth.getText().toString(); // inputs[3] + " " + inputs[4] + " at " + inputs[5] + inputs[6]
                inputs[4] = inDate.getText().toString();
                inputs[5] = inHours.getText().toString();
                inputs[6] = inAMorPM.getText().toString();
                inputs[7] = inDesc.getText().toString();
                inputs[8] = inFilled.getText().toString();
                inputs[9] = inPax.getText().toString();

                validate();
            }
        });
    }


    private void validate() {
        if (Strings.isEmptyOrWhitespace(inputs[0])) {
            Toast.makeText(getApplicationContext(), "Invalid game name", Toast.LENGTH_SHORT).show();
        } else if (Strings.isEmptyOrWhitespace(inputs[1])) {
            Toast.makeText(getApplicationContext(), "Invalid sport", Toast.LENGTH_SHORT).show();
        } else if (Strings.isEmptyOrWhitespace(inputs[2])) {
            Toast.makeText(getApplicationContext(), "Invalid venue", Toast.LENGTH_SHORT).show();
        } else if (Strings.isEmptyOrWhitespace(inputs[3]) || inputs[3].length() != 3) {
            Toast.makeText(getApplicationContext(), "Invalid month", Toast.LENGTH_SHORT).show();
        } else if (Strings.isEmptyOrWhitespace(inputs[4])
                || (inputs[4].length() != 2 || inputs[4].charAt(0) > '3'
                || inputs[4].charAt(1) > '9')) {
            Toast.makeText(getApplicationContext(), "Invalid date", Toast.LENGTH_SHORT).show();
        } else if (Strings.isEmptyOrWhitespace(inputs[5])
                || !inputs[5].matches("\\d+\\.\\d+")) {
            Toast.makeText(getApplicationContext(), "Invalid hours", Toast.LENGTH_SHORT).show();
        } else if (!(inputs[6].equals("AM") || inputs[6].equals("PM"))) {
            Toast.makeText(getApplicationContext(), "Invalid AM/PM", Toast.LENGTH_SHORT).show();
        } else if (Strings.isEmptyOrWhitespace(inputs[7])) {
            Toast.makeText(getApplicationContext(), "Invalid description", Toast.LENGTH_SHORT).show();
        } else if (Strings.isEmptyOrWhitespace(inputs[8])
                || !inputs[8].matches("[0-9]+")) {
            Toast.makeText(getApplicationContext(), "Invalid filled slots", Toast.LENGTH_SHORT).show();
        } else if (Strings.isEmptyOrWhitespace(inputs[9])
                || !inputs[9].matches("[0-9]+")) {
            Toast.makeText(getApplicationContext(), "Invalid party size", Toast.LENGTH_SHORT).show();
        } else {
            addNewEvent();
            addedToast();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
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
        final FirebaseFirestore fs = FirebaseFirestore.getInstance();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        fs.collection(User.usersCollection).document(currentUser.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();

                ArrayList<String> contact = new ArrayList<>();
                contact.add((String) doc.get(User.emailKey));
                contact.add((String) doc.get(User.phoneKey));

                Event ev = new Event(contact, inputs[7], (String) doc.get(User.idKey), inputs[0],
                        inputs[1], inputs[2], inputs[3] + " " + inputs[4] + " at " + inputs[5] + inputs[6],
                        Long.parseLong(inputs[9]), Long.parseLong(inputs[8]));

                ev.createEntry();

                fs.collection(User.usersCollection).document(currentUser.getEmail())
                        .update(User.hostingKey, FieldValue.arrayUnion(ev.getID()));

            }
        });



    }

    private void addedToast() {
        Toast.makeText(getApplicationContext(), "Game hosted successfully!", Toast.LENGTH_LONG)
            .show();
    }
}


