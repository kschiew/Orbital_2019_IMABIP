package com.orbital19.imabip.booking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.orbital19.imabip.R;

import java.util.ArrayList;
import java.util.Calendar;

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
    }

    private void showHourDialog() {
        AlertDialog.Builder hoursDialog = new AlertDialog.Builder(this);

        

    }

    private void showUnitDialog() {
        AlertDialog.Builder unitDialog = new AlertDialog.Builder(this);

        String ve = venueTV.getText().toString();

        unitDialog.setTitle("Units");

        if (ve.equals("Kent Ridge - Tennis courts")) {
            unitDialog.setSingleChoiceItems(tCourts, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    venueTV.setText(tCourts[which]);
                }
            });
        } else if (ve.equals("Kent Ridge - Squash courts")) {
            unitDialog.setSingleChoiceItems(sqCourts, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    venueTV.setText(sqCourts[which]);
                }
            });
        } else if (ve.equals("Kent Ridge - MPSH2")) {
            unitDialog.setSingleChoiceItems(ttTables, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    venueTV.setText(ttTables[which]);
                }
            });
        } else if (ve.equals("Kent Ridge - MPSH5")) {
            unitDialog.setSingleChoiceItems(bCourts_KR, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    venueTV.setText(bCourts_KR[which]);
                }
            });
        } else if (ve.equals("UTown - Sports Hall 1")) {
            unitDialog.setSingleChoiceItems(bCourts_UT, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    venueTV.setText(bCourts_UT[which]);
                }
            });
        } else {
            unitDialog.setSingleChoiceItems(new CharSequence[1], -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }

        unitDialog.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        unitDialog.create().show();
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
                }
            });
        } else if (sp.equals("Table tennis")) {
            venueDialog.setSingleChoiceItems(TTVenues, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    venueTV.setText(TTVenues[which]);
                }
            });
        } else if (sp.equals("Badminton")) {
            venueDialog.setSingleChoiceItems(BdVenues, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    venueTV.setText(BdVenues[which]);
                }
            });
        } else if (sp.equals("Squash")) {
            venueDialog.setSingleChoiceItems(SqVenues, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    venueTV.setText(SqVenues[which]);
                }
            });
        } else {
            venueDialog.setSingleChoiceItems(new CharSequence[1], -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }


        venueDialog.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        venueDialog.create().show();
    }

    private void showSportDialog() {
        AlertDialog.Builder sportDialog = new AlertDialog.Builder(this);

        final CharSequence[] sports = {"Tennis", "Table tennis", "Badminton", "Squash"};

        sportDialog.setTitle("Sports").setSingleChoiceItems(sports, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sportTV.setText(sports[which]);
            }
        });

        sportDialog.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        sportDialog.create().show();
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
        dateTV.setText("" + dayOfMonth + month + year);
    }

}
