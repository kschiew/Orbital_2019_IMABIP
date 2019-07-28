package com.orbital19.imabip.edits;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.MainActivity;
import com.orbital19.imabip.R;
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.models.User;
import com.orbital19.imabip.teams.models.Team;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


/*
    A clone of Host class, with info filled from the viewing chosen game
 */
public class EditHostActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private EditText inName, inType, inVenue, inHours, inAMorPM, inDesc, inFilled, inPax;
    private TextView inMonth, inDate, pickDate, pickIdentity;
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

        Intent intent = this.getIntent();
        final Event event = (Event) intent.getExtras().getSerializable("toEditEvent");

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
        pickDate = findViewById(R.id.Time_show);
        pickIdentity = findViewById(R.id.input_identity);

        inName.setText(event.getName());
        inType.setText(event.getType());
        inVenue.setText(event.getVenue());
        String time = event.getTime();
        inHours.setText(time.substring(10,15));
        inAMorPM.setText(time.substring(15));
        inDesc.setText(event.getDescription());
        inFilled.setText(String.format(Locale.getDefault(),"%d", event.getEnrolled()));
        inPax.setText(String.format(Locale.getDefault(), "%d", event.getPartySize()));

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        pickIdentity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIdentityDialog();
            }
        });

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

                validate(event);

            }
        });
    }

    private void showIdentityDialog() {
        final AlertDialog.Builder identityDialog = new AlertDialog.Builder(this);
 
        final ArrayList<CharSequence> options = new ArrayList<>();

        options.add("Individual");
        String curEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseFirestore.getInstance().collection(User.usersCollection).document(curEmail)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {


                    ArrayList<String> caps = (ArrayList<String>) doc.get(User.captainOfKey);

                    for (String team : caps) {
                        options.add(team);
                    }

                    final CharSequence[] arr = new CharSequence[options.size()];
                    options.toArray(arr);

                    identityDialog.setTitle("Pick identity").setSingleChoiceItems(arr, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pickIdentity.setText(arr[which]);
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

                    identityDialog.create().show();
                }
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }


    private void validate(Event event) {
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
        }  else if (Strings.isEmptyOrWhitespace(inputs[8])
                || !inputs[8].matches("[0-9]+")) {
            Toast.makeText(getApplicationContext(), "Invalid filled slots", Toast.LENGTH_SHORT).show();
        } else if (Strings.isEmptyOrWhitespace(inputs[9])
                || !inputs[9].matches("[0-9]+")) {
            Toast.makeText(getApplicationContext(), "Invalid party size", Toast.LENGTH_SHORT).show();
        } else if (pickIdentity.getText().toString().equals("Choose Identity")) {
            Toast.makeText(getApplicationContext(), "Invalid identity", Toast.LENGTH_SHORT).show();
        } else if (lastInvalidCheck()) {
            Toast.makeText(getApplicationContext(), "Invalid game", Toast.LENGTH_SHORT).show();
        } else if (pickIdentity.getText().toString().equals("Individual")) {
            addNewEvent();
            addedToast();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else { // as a team
            String tmName = pickIdentity.getText().toString();
            addTeamEvent(tmName);
            addedToast();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    private void addTeamEvent(final String tmName) {
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

                Event ev = new Event(contact, inputs[7], tmName, inputs[0],
                        inputs[1], inputs[2], inputs[3] + " " + inputs[4] + " at " + inputs[5] + inputs[6],
                        Long.parseLong(inputs[9]), Long.parseLong(inputs[8]), true);

                ev.createEntry();
                String encoder = inputs[8] + "__";
                fs.collection(Team.teamsCollection).document(tmName)
                        .update(Team.teamHostingKey, FieldValue.arrayUnion(encoder+ev.getID()));
            }
        });
    }

    private boolean lastInvalidCheck() {
        ArrayList<String> contact = new ArrayList<>();
        contact.add("d@mail.com");
        contact.add("12345678");
        Event ev = new Event(contact, inputs[7], "D", inputs[0],
                inputs[1], inputs[2], inputs[3] + " " + inputs[4] + " at " + inputs[5] + inputs[6],
                Long.parseLong(inputs[9]), Long.parseLong(inputs[8]), false);

        if (ev.getTimeInMilis() < Calendar.getInstance().getTimeInMillis())
            return true;
        else
            return false;
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
                        Long.parseLong(inputs[9]), Long.parseLong(inputs[8]), false);

                ev.createEntry();
                ev.toUserHistory(currentUser.getEmail());

                fs.collection(User.usersCollection).document(currentUser.getEmail())
                        .update(User.hostingKey, FieldValue.arrayUnion(ev.getID()));
            }
        });
    }

    private void addedToast() {
        Toast.makeText(getApplicationContext(), "Game hosted successfully!", Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        HashMap<Integer, String> months = new HashMap<>();
        months.put(0, "Jan");
        months.put(1, "Feb");
        months.put(2, "Mar");
        months.put(3, "Apr");
        months.put(4, "May");
        months.put(5, "Jun");
        months.put(6, "Jul");
        months.put(7, "Aug");
        months.put(8, "Sep");
        months.put(9, "Oct");
        months.put(10, "Nov");
        months.put(11, "Dec");

        inMonth.setText(months.get(month));
        inDate.setText(dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth);
    }
}
