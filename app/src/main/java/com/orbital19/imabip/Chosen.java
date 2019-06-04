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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.models.User;

import java.util.ArrayList;

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

        nameTV.setText(ev.getName());
        hostTV.setText(ev.getHost());
        timeTV.setText(ev.getTime().toString());
        venueTV.setText(ev.getVenue());
        descriptionTV.setText(ev.getDescription());
        partyTV.setText(String.format("%d / %d", ev.getEnrolled().longValue(),
                ev.getPartySize().longValue()));

        toJoin = findViewById(R.id.ev_join);

        toJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(User.usersCollection).document(current.getEmail())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();

                        ArrayList<String> evs = (ArrayList<String>) doc.get(User.enrolledKey);

                        

                        FirebaseFirestore.getInstance().collection(Event.availableEventCollection)
                                .document(ev.getID()).update(Event.enrolledKey, ev.getEnrolled() + 1);
                    }
                });

                Bundle args = new Bundle();
                args.putSerializable("Chosen event", ev);
                Fragment frag = new Fragment();
                frag.setArguments(args);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.chosen_act, frag);
                transaction.commit();
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
