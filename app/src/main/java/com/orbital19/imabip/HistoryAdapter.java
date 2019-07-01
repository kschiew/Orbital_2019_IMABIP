package com.orbital19.imabip;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.edits.EditHostActivity;
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends ArrayAdapter {
    private final Context context;
    private final List<Event> values;

    public HistoryAdapter(Context contxt, List<Event> vals) {
        super(contxt, R.layout.details_event_list_row, vals);
        this.context = contxt;
        this.values = vals;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.details_history_list_row, parent, false);

        TextView nameTV = rowView.findViewById(R.id.eventName_history);
        TextView hostTV = rowView.findViewById(R.id.hostID_history);
        TextView timeTV = rowView.findViewById(R.id.time_history);
        TextView typeTV = rowView.findViewById(R.id.type_history);
        TextView venueTV = rowView.findViewById(R.id.venue_history);

        final Event cur = values.get(position);

        nameTV.setText(cur.getName());
        hostTV.setText(cur.getHost());
        typeTV.setText(cur.getType());
        timeTV.setText(cur.getTime());
        venueTV.setText(cur.getVenue());
        String pax = String.format(Locale.getDefault(),
                "%d / %d", cur.getEnrolled(), cur.getPartySize());

        return rowView;
    }
}
