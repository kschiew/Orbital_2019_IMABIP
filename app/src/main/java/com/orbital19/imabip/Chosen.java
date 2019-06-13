package com.orbital19.imabip;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.models.User;

import java.util.ArrayList;
import java.util.Locale;

public class Chosen extends AppCompatActivity {

    private Button toJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Event chosen");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen);

        final Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        final Event ev = (Event) bundle.getSerializable("Event");
        View view = findViewById(R.id.chosen_act);

        TextView nameTV = view.findViewById(R.id.ev_name);
        TextView hostTV = view.findViewById(R.id.ev_host);
        TextView timeTV = view.findViewById(R.id.ev_time);
        TextView venueTV = view.findViewById(R.id.ev_venue);
        TextView descriptionTV = view.findViewById(R.id.ev_description);
        TextView partyTV = view.findViewById(R.id.ev_party);
        final TextView joinedSignTV = view.findViewById(R.id.joined_sign);

        nameTV.setText(ev.getName());
        hostTV.setText(ev.getHost());
        timeTV.setText(ev.getTime());
        venueTV.setText(ev.getVenue());
        descriptionTV.setText(ev.getDescription());
        partyTV.setText(String.format(Locale.getDefault(),
                "%d / %d", ev.getEnrolled(), ev.getPartySize()));

        toJoin = findViewById(R.id.ev_join);

        toJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                DocumentReference user = db.collection(User.usersCollection).document(current.getEmail());
                user.update(User.enrolledKey, FieldValue.arrayUnion(ev.getID()));

                DocumentReference event = db.collection(Event.availableEventCollection).document(ev.getID());
                event.update(Event.enrolledKey, FieldValue.increment(1));
                event.update(Event.playersKey, FieldValue.arrayUnion(current.getEmail()));

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                finish();
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore.getInstance().collection(User.usersCollection).document(user.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<String> lst = (ArrayList<String>) task.getResult().get(User.enrolledKey);

                if (lst.contains(ev.getID())) {
                    joinedSignTV.setVisibility(View.VISIBLE);
                    toJoin.setVisibility(View.GONE);
                } else {
                    joinedSignTV.setVisibility(View.GONE);
                    toJoin.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
