package com.orbital19.imabip.booking;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.orbital19.imabip.R;

public class BookingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle("BOOKING");


    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}
