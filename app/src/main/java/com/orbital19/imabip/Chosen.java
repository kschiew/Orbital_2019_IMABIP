package com.orbital19.imabip;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import com.orbital19.imabip.works.StartingNotifyWorker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Chosen extends AppCompatActivity {

    private Button toJoin, toEdit, toRehost;

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
                FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                DocumentReference user = db.collection(User.usersCollection).document(current.getEmail());
                user.update(User.enrolledKey, FieldValue.arrayUnion(ev.getID()));

                DocumentReference event = db.collection(Event.availableEventCollection).document(ev.getID());
                event.update(Event.enrolledKey, FieldValue.increment(1));
                event.update(Event.playersKey, FieldValue.arrayUnion(current.getEmail()));

                String notiTag = ev.getID();

                Data inputData = new Data.Builder().putString(NotificationsHelper.STARTING_KEY, notiTag).build();

                long dOne = ev.delayOne();
                OneTimeWorkRequest workOne = null;
                if (dOne > 0) {
                    workOne = new OneTimeWorkRequest.Builder(StartingNotifyWorker.class)
                            .setInitialDelay(dOne, TimeUnit.MILLISECONDS)
                            .setInputData(inputData)
                            .addTag(notiTag)
                            .build();
                }

                long dTwo = ev.delayTwo();
                OneTimeWorkRequest workTwo = null;
                if (dTwo > 0) {
                    workTwo = new OneTimeWorkRequest.Builder(StartingNotifyWorker.class)
                            .setInitialDelay(dTwo, TimeUnit.MILLISECONDS)
                            .setInputData(inputData)
                            .addTag(notiTag)
                            .build();
                }

                OneTimeWorkRequest workThree = new OneTimeWorkRequest.Builder(StartingNotifyWorker.class)
                        .setInitialDelay(ev.delayExact(), TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .addTag(notiTag)
                        .build();

                WorkManager workManager = WorkManager.getInstance();

                if (workOne != null) workManager.enqueue(workOne);
                if (workTwo != null) workManager.enqueue(workTwo);

                workManager.enqueue(workThree);
                Log.d("Noti queued", "Planned notifications");

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                finish();
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

        if (bundle.getBoolean("hosting")) {
            joinedSignTV.setVisibility(View.GONE);
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

                    if (lst.contains(ev.getID())) {
                        joinedSignTV.setVisibility(View.VISIBLE);
                        toJoin.setVisibility(View.GONE);
                        toRehost.setVisibility(View.GONE);
                    } else {
                        joinedSignTV.setVisibility(View.GONE);
                        toJoin.setVisibility(View.VISIBLE);
                        toRehost.setVisibility(View.GONE);
                    }
                }
            });
        } else { // History
            toJoin.setVisibility(View.GONE);
            toEdit.setVisibility(View.GONE);
            joinedSignTV.setVisibility(View.GONE);
            toRehost.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
