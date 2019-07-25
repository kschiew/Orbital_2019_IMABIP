package com.orbital19.imabip;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.edits.EditHostActivity;
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.models.User;
import com.orbital19.imabip.models.user.DisplayUser;
import com.orbital19.imabip.teams.models.Team;
import com.orbital19.imabip.works.StartingNotifyWorker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Chosen extends AppCompatActivity {

    private Button toJoin, toEdit, toRehost, toDrop, pickIdentity;

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
        final TextView descriptionTV = view.findViewById(R.id.ev_description);
        TextView partyTV = view.findViewById(R.id.ev_party);
        final TextView joinedSignTV = view.findViewById(R.id.joined_sign);
        TextView saveToCalendarTV = view.findViewById(R.id.save_to_calendar);

        nameTV.setText(ev.getName());
        hostTV.setText(ev.getHost());
        hostTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().collection(User.usersCollection).document(ev.getContact().get(0))
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();
                        User user = new User((String) doc.get(User.emailKey), (String) doc.get(User.nameKey),
                                (String) doc.get(User.phoneKey), (String) doc.get(User.idKey));

                        Intent intent = new Intent(getApplicationContext(), DisplayUser.class);

                        intent.putExtra("toViewUser", user);
                        startActivity(intent);

                        finish();
                    }
                });
            }
        });
        saveToCalendarTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", ev.getTimeInMilis());
                intent.putExtra("allDay", false);
                intent.putExtra("endTime", ev.getTimeInMilis()+60*60*1000);
                intent.putExtra("title", ev.getName());
                startActivity(intent);
            }
        });
        timeTV.setText(ev.getTime());
        venueTV.setText(ev.getVenue());
        descriptionTV.setText(ev.getDescription());
        partyTV.setText(String.format(Locale.getDefault(),
                "%d / %d", ev.getEnrolled(), ev.getPartySize()));

        toJoin = findViewById(R.id.ev_join);

        toJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ev.getEnrolled() < ev.getPartySize()) {
                    Intent intent1 = new Intent(getApplicationContext(), JoinConfirmActivity.class);
                    intent1.putExtra("toConfirm", ev);
                    startActivity(intent1);

                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "No more slots!", Toast.LENGTH_LONG).show();
                }
            }
        });

        toEdit = findViewById(R.id.ev_edit);

        toEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditHostActivity.class);
                intent.putExtra("toEditEvent", ev);
                startActivity(intent);

                finish();
            }
        });

        toRehost = findViewById(R.id.ev_rehost);

        toRehost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditHostActivity.class);
                intent.putExtra("toEditEvent", ev);
                startActivity(intent);

                finish();
            }
        });

        toDrop = findViewById(R.id.ev_drop_out);

        toDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection(User.usersCollection).document(currentUser.getEmail())
                        .update(User.enrolledKey, FieldValue.arrayRemove(ev.getID()));

                db.collection(Event.availableEventCollection).document(ev.getID())
                        .update(Event.enrolledKey, FieldValue.increment(-1));

                db.collection(Event.availableEventCollection).document(ev.getID())
                        .update(Event.playersKey, FieldValue.arrayRemove(currentUser.getEmail()));

                db.collection(User.usersCollection).document(currentUser.getEmail())
                        .collection(User.historyCollection).document(ev.getID()).delete();

                WorkManager.getInstance().cancelAllWorkByTag(ev.getID());

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        if (bundle.getBoolean("hosting")) {
            joinedSignTV.setVisibility(View.GONE);
            toDrop.setVisibility(View.GONE);
            toJoin.setVisibility(View.GONE);
            toEdit.setVisibility(View.VISIBLE);
            toRehost.setVisibility(View.GONE);
        } else if (!bundle.getBoolean("History")){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore.getInstance().collection(User.usersCollection).document(user.getEmail())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    ArrayList<String> lst = (ArrayList<String>) task.getResult().get(User.enrolledKey);

                    if (lst.contains(ev.getID())) { // joined
                        joinedSignTV.setVisibility(View.VISIBLE);
                        toDrop.setVisibility(View.VISIBLE);
                        toJoin.setVisibility(View.GONE);
                        toRehost.setVisibility(View.GONE);
                    } else {
                        joinedSignTV.setVisibility(View.GONE);
                        toDrop.setVisibility(View.GONE);
                        toJoin.setVisibility(View.VISIBLE);
                        toRehost.setVisibility(View.GONE);
                    }
                }
            });
        } else { // History
            toJoin.setVisibility(View.GONE);
            toEdit.setVisibility(View.GONE);
            joinedSignTV.setVisibility(View.GONE);
            toDrop.setVisibility(View.GONE);
            toRehost.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
