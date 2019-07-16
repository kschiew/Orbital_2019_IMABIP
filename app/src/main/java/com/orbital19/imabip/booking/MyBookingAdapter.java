package com.orbital19.imabip.booking;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.orbital19.imabip.R;
import com.orbital19.imabip.booking.model.BookingObject;
import com.orbital19.imabip.models.Event;

import java.util.List;
import java.util.Locale;

public class MyBookingAdapter extends ArrayAdapter {
    private final Context context;
    private final List<BookingObject> values;

    public MyBookingAdapter(Context context, List<BookingObject> vals) {
        super(context, R.layout.details_my_bookings_list_row, vals);
        this.context = context;
        this.values = vals;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.details_my_bookings_list_row, parent, false);

        TextView sportTV = rowView.findViewById(R.id.my_booked_sport);
        TextView venueTV = rowView.findViewById(R.id.my_booked_venue);
        TextView unitTV = rowView.findViewById(R.id.my_booked_unit);
        TextView dateTV = rowView.findViewById(R.id.my_booked_date);
        TextView hourTV = rowView.findViewById(R.id.my_booked_hour);
        TextView cancel = rowView.findViewById(R.id.cancel_booking);

        final BookingObject cur = values.get(position);

        sportTV.setText(cur.getSport());
        venueTV.setText(cur.getVenue());
        unitTV.setText(cur.getUnit());
        dateTV.setText(cur.getDate());
        hourTV.setText(cur.getHour());

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cur.deleteEntry();
                cur.deleteFromUser(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                getContext().startActivity(new Intent(getContext(), MyBookingActivity.class));
            }
        });

        return rowView;
    }
}
