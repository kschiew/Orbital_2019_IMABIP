package com.orbital19.imabip;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.common.util.Strings;
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
import com.orbital19.imabip.teams.models.Team;
import com.orbital19.imabip.works.StartingNotifyWorker;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JoinConfirmActivity extends AppCompatActivity {
    private TextView idenTV;
    private EditText partiET;
    private Button idenSelBtn, cfmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_confirm);

        getSupportActionBar().setTitle("Confirm");

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        final Event ev = (Event) bundle.getSerializable("toConfirm");

        idenTV = findViewById(R.id.identity);
        partiET = findViewById(R.id.participants);
        idenSelBtn = findViewById(R.id.identity_select);
        cfmBtn = findViewById(R.id.confirm_join);

        final AlertDialog.Builder picker = new AlertDialog.Builder(this);
        idenSelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ArrayList<CharSequence> options = new ArrayList<>();

                options.add("Individual");
                String curEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                FirebaseFirestore.getInstance().collection(User.usersCollection).document(curEmail)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();

                        if (doc.exists()) {
                            Log.d("Identity to join", "Begin");

                            ArrayList<String> caps = new ArrayList<>();
                            caps.addAll((ArrayList<String>) doc.get(User.captainOfKey));

                            if (caps.size() > 0) {
                                for (final String team : caps) {
                                    FirebaseFirestore.getInstance().collection(Team.teamsCollection)
                                            .document(team).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot doc = task.getResult();

                                            if (doc.exists()) {
                                                Log.d("Identity to join", "Working");
                                                ArrayList<String> teamJoined = (ArrayList<String>)
                                                        doc.get(Team.teamJoinedKey);
                                                ArrayList<String> decoded = new ArrayList<>();
                                                for (String s : teamJoined)
                                                    decoded.add(s.split("__", 0)[1]);

                                                if (!decoded.contains(ev.getID())) {
                                                    Log.d("Identity", team);
                                                    options.add(team);
                                                }
                                            }

                                            final CharSequence[] arr = new CharSequence[options.size()];
                                            options.toArray(arr);

                                            picker.setTitle("Pick identity").setSingleChoiceItems(arr, -1, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    idenTV.setText(arr[which]);
                                                    if (which > 0) {
                                                        partiET.setHint("Max: " + (ev.getPartySize() - ev.getEnrolled()));
                                                        partiET.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            }).setPositiveButton("Choose", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });

                                            picker.create().show();
                                        }
                                    });
                                }
                            } else {
                                final CharSequence[] arr = new CharSequence[options.size()];
                                options.toArray(arr);

                                picker.setTitle("Pick identity").setSingleChoiceItems(arr, -1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        idenTV.setText(arr[which]);
                                        if (which > 0) {
                                            partiET.setHint("Max: " + (ev.getPartySize() - ev.getEnrolled()));
                                            partiET.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }).setPositiveButton("Choose", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                                picker.create().show();
                            }
                        }
                    }
                });
            }
        });

        cfmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idenTV.getText().toString().equals("Identity")) {
                    Toast.makeText(getApplicationContext(), "Missing information", Toast.LENGTH_LONG).show();
                } else if (idenTV.getText().toString().equals("Individual")) {
                    // individual player
                    FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    DocumentReference user = db.collection(User.usersCollection).document(current.getEmail());
                    user.update(User.enrolledKey, FieldValue.arrayUnion(ev.getID()));

                    DocumentReference event = db.collection(Event.availableEventCollection).document(ev.getID());
                    event.update(Event.enrolledKey, FieldValue.increment(1));
                    event.update(Event.playersKey, FieldValue.arrayUnion(current.getEmail()));

                    ev.toUserHistory(current.getEmail());

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

                    workManager.enqueue(workThree);

                    if (workOne != null) workManager.enqueue(workOne);
                    if (workTwo != null) workManager.enqueue(workTwo);


                    Log.d("Noti queued", "Planned notifications");

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                    finish();
                } else if (Strings.isEmptyOrWhitespace(partiET.getText().toString())) { // as a team
                    Toast.makeText(getApplicationContext(), "Missing information", Toast.LENGTH_LONG).show();
                } else if (Integer.parseInt(partiET.getText().toString()) > ev.getPartySize()-ev.getEnrolled()) {
                    Toast.makeText(getApplicationContext(), "Not enough player slots", Toast.LENGTH_LONG).show();
                } else {
                    // team
                    final Long toPlay = Long.parseLong(partiET.getText().toString());
                    final String team = idenTV.getText().toString();

                    final FirebaseFirestore fs = FirebaseFirestore.getInstance();
                    String encoder = String.format(Locale.getDefault(), "%d__", toPlay);
                    fs.collection(Team.teamsCollection).document(team)
                            .update(Team.teamJoinedKey, FieldValue.arrayUnion(encoder + ev.getID()));
                    fs.collection(Event.availableEventCollection).document(ev.getID()).update(Event.enrolledKey, FieldValue.increment(toPlay));
                    fs.collection(Event.availableEventCollection).document(ev.getID())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Map<String, Object> teamSlots = (Map<String, Object>) task.getResult().get(Event.teamSlotsKey);

                            teamSlots.put(team, toPlay);
                            fs.collection(Event.availableEventCollection).document(ev.getID())
                                    .update(Event.teamSlotsKey, teamSlots);
                        }
                    });

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
                    workManager.enqueue(workThree);
                    if (workOne != null) workManager.enqueue(workOne);
                    if (workTwo != null) workManager.enqueue(workTwo);
                    Log.d("Noti queued", "Planned notifications");

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
