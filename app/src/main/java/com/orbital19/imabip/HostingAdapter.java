package com.orbital19.imabip;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.models.User;

import java.util.List;
import java.util.Locale;

class HostingAdapter extends ArrayAdapter<Event> {

    private final Context context;
    private final List<Event> values;

    public HostingAdapter(Context contxt, List<Event> vals) {
        super(contxt, R.layout.details_enrolled_list_row, vals);
        this.context = contxt;
        this.values = vals;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.details_hosting_list_row, container, false);

        TextView nameTV = rowView.findViewById(R.id.eventName_hosting);
        TextView hostTV = rowView.findViewById(R.id.hostID_hosting);
        TextView paxTV = rowView.findViewById(R.id.pax_hosting);
        TextView timeTV = rowView.findViewById(R.id.time_hosting);
        TextView cancelTV = rowView.findViewById(R.id.cancel_ev);
        TextView typeTV = rowView.findViewById(R.id.type_hosting);
        TextView venueTV = rowView.findViewById(R.id.venue_hosting);

        final Event cur = values.get(position);

        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection(User.usersCollection).document(currentUser.getEmail())
                        .update(User.hostingKey, FieldValue.arrayRemove(cur.getID()));

                db.collection(Event.availableEventCollection).document(cur.getID())
                        .delete();

                Intent intent = new Intent(getContext(), MainActivity.class);
                getContext().startActivity(intent);
            }
        });

        nameTV.setText(cur.getName());
        hostTV.setText(cur.getHost());
        timeTV.setText(cur.getTime());
        String pax = String.format(Locale.getDefault(),
                "%d / %d", cur.getEnrolled(), cur.getPartySize());
        paxTV.setText(pax);
        typeTV.setText(cur.getType());
        venueTV.setText(cur.getVenue());


        return rowView;
    }
}
