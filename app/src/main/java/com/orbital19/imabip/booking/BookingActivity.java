package com.orbital19.imabip.booking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.orbital19.imabip.MainActivity;
import com.orbital19.imabip.R;
import com.orbital19.imabip.booking.model.BookingObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class BookingActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    private Button chooseDate, chooseSport, chooseVenue, chooseUnit, chooseHour, bookBtn;
    private TextView dateTV, sportTV, venueTV, unitTV, hoursTV;

    private final CharSequence[] TennisVenues = {"Kent Ridge - Tennis courts"};
    private final CharSequence[] tCourts = {"Court 1", "Court 2", "Court 3", "Court 4", "Court 5"};
    private final CharSequence[] TTVenues = {"Kent Ridge - MPSH2"};
    private final CharSequence[] ttTables = {"Table 1", "Table 2", "Table 3", "Table 4", "Table 5",
                                            "Table 6", "Table 7", "Table 8", "Table 9", "Table 10"};
    private final CharSequence[] SqVenues = {"Kent Ridge - Squash courts"};
    private final CharSequence[] sqCourts = {"Court 1", "Court 2"};
    private final CharSequence[] BdVenues = {"Kent Ridge - MPSH5", "UTown - Sports Hall 1"};
    private final CharSequence[] bCourts_KR = {"Court 1", "Court 2", "Court 3", "Court 4", "Court 5"};
    private final CharSequence[] bCourts_UT = {"Court 21", "Court 22"};

    private final CharSequence[] TimeSlots = {"08.00", "09.00", "10.00", "11.00", "12.00", "13.00",
                                                "14.00", "15.00", "16.00", "17.00", "18.00", "19.00", "20.00"};

    private boolean[] check = new boolean[5];

    final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle("NEW BOOKING");

        chooseDate = findViewById(R.id.choose_date);
        chooseSport = findViewById(R.id.choose_sport);
        chooseVenue = findViewById(R.id.choose_venue);
        chooseUnit = findViewById(R.id.choose_unit);
        chooseHour = findViewById(R.id.choose_hour);
        bookBtn = findViewById(R.id.book);

        dateTV = findViewById(R.id.date_booked);
        sportTV = findViewById(R.id.sport_booked);
        venueTV = findViewById(R.id.venue_booked);
        unitTV = findViewById(R.id.unit_booked);
        hoursTV = findViewById(R.id.hour_booked);

        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        chooseSport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSportDialog();
            }
        });

        chooseVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVenueDialog();
            }
        });

        chooseUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUnitDialog();
            }
        });

        chooseHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHourDialog();
            }
        });

        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(check[0] && check[1] && check[2] && check[3] && check[4])) {
                    Toast.makeText(getApplicationContext(), "Invalid booking", Toast.LENGTH_SHORT).show();
                } else {
                    String[] vals = new String[4];
                    vals[0] = sportTV.getText().toString();
                    vals[1] = dateTV.getText().toString();
                    vals[2] = venueTV.getText().toString();
                    vals[3] = unitTV.getText().toString();

                    String[] slots = hoursTV.getText().toString().split("[\\r\\n]++");

                    for (String hr : slots) {
                        BookingObject bkng = new BookingObject(vals[0], vals[1], hr, vals[2], vals[3], email);

                        bkng.newEntry();
                        bkng.addToUser(email);
                    }

                    Toast.makeText(getApplicationContext(), "Booking successful", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        });
    }

    private void showHourDialog() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        final AlertDialog.Builder hoursDialog = new AlertDialog.Builder(this);

        final ArrayList<CharSequence> times = new ArrayList<>(Arrays.asList(TimeSlots));
        final ArrayList<CharSequence> unavailable = new ArrayList<>();
        String ven = venueTV.getText().toString();
        String uni = unitTV.getText().toString();
        final String dat = dateTV.getText().toString();

        if (!(check[0] && check[1] && check[2] && check[3])) {
            Toast.makeText(getApplicationContext(),"Pick a unit", Toast.LENGTH_SHORT).show();
        } else {
            fs.collection(BookingObject.bookingsCollection).document(ven).collection(uni)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<DocumentSnapshot> lst = task.getResult().getDocuments();

                    for (DocumentSnapshot doc : lst) {
                        if (doc.get(BookingObject.dateKey).toString().equals(dat)) {
                            times.remove(doc.get(BookingObject.hourKey).toString());
                            Log.d("Booked slot", doc.get(BookingObject.hourKey).toString());
                        }
                    }

                    final CharSequence[] available = new CharSequence[times.size()];
                    times.toArray(available);
                    final boolean[] picked = new boolean[available.length];

                    hoursDialog.setTitle("Hour slots").setMultiChoiceItems(available, picked, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            picked[which] = isChecked;
                        }
                    });

                    hoursDialog.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String display = "";
                            for (int i = 0; i < available.length; i++)
                                if (picked[i]) {
                                    display = display + available[i] + "\n";
                                }

                            hoursTV.setText(display);
                            check[4] = true;
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    hoursDialog.create().show();

                }
            });
        }
    }

    private void showUnitDialog() {
        AlertDialog.Builder unitDialog = new AlertDialog.Builder(this);

        String ve = venueTV.getText().toString();

        unitDialog.setTitle("Units");

        if (ve.equals("Kent Ridge - Tennis courts")) {
            unitDialog.setSingleChoiceItems(tCourts, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    unitTV.setText(tCourts[which]);
                    check[3] = true;
                }
            }).setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();
        } else if (ve.equals("Kent Ridge - Squash courts")) {
            unitDialog.setSingleChoiceItems(sqCourts, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    unitTV.setText(sqCourts[which]);
                    check[3] = true;
                }
            }).setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();
        } else if (ve.equals("Kent Ridge - MPSH2")) {
            unitDialog.setSingleChoiceItems(ttTables, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    unitTV.setText(ttTables[which]);
                    check[3] = true;
                }
            }).setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();
        } else if (ve.equals("Kent Ridge - MPSH5")) {
            unitDialog.setSingleChoiceItems(bCourts_KR, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    unitTV.setText(bCourts_KR[which]);
                    check[3] = true;
                }
            }).setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();
        } else if (ve.equals("UTown - Sports Hall 1")) {
            unitDialog.setSingleChoiceItems(bCourts_UT, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    unitTV.setText(bCourts_UT[which]);
                    check[3] = true;
                }
            }).setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();
        } else {
            Toast.makeText(getApplicationContext(), "Pick a venue", Toast.LENGTH_SHORT).show();
        }
    }

    private void showVenueDialog() {
        AlertDialog.Builder venueDialog = new AlertDialog.Builder(this);

        String sp = sportTV.getText().toString();

        venueDialog.setTitle("Venues");

        if (sp.equals("Tennis")) {
            venueDialog.setSingleChoiceItems(TennisVenues, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    venueTV.setText(TennisVenues[which]);
                    check[2] = true;
                }
            }).setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();;
        } else if (sp.equals("Table tennis")) {
            venueDialog.setSingleChoiceItems(TTVenues, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    venueTV.setText(TTVenues[which]);
                    check[2] = true;
                }
            }).setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();;
        } else if (sp.equals("Badminton")) {
            venueDialog.setSingleChoiceItems(BdVenues, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    venueTV.setText(BdVenues[which]);
                    check[2] = true;
                }
            }).setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();;
        } else if (sp.equals("Squash")) {
            venueDialog.setSingleChoiceItems(SqVenues, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    venueTV.setText(SqVenues[which]);
                    check[2] = true;
                }
            }).setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();;
        } else {
            Toast.makeText(getApplicationContext(), "Invalid sport", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSportDialog() {
        AlertDialog.Builder sportDialog = new AlertDialog.Builder(this);

        final CharSequence[] sports = {"Tennis", "Table tennis", "Badminton", "Squash"};

        if (check[0]) {
            sportDialog.setTitle("Sports").setSingleChoiceItems(sports, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sportTV.setText(sports[which]);
                    check[1] = true;
                }
            });

            sportDialog.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            sportDialog.create().show();
        } else {
            Toast.makeText(getApplicationContext(), "Pick a date", Toast.LENGTH_SHORT).show();
        }


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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dateTV.setText("" + dayOfMonth + "." + (month+1) + "." + year);
        check[0] = true;
    }

}
