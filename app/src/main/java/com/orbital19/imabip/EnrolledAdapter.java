package com.orbital19.imabip;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.models.User;

import java.util.List;
import java.util.Locale;

public class EnrolledAdapter extends ArrayAdapter<Event> {

    private final Context context;
    private final List<Event> values;

    public EnrolledAdapter(Context contxt, List<Event> vals) {
        super(contxt, R.layout.details_enrolled_list_row, vals);
        this.context = contxt;
        this.values = vals;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.details_enrolled_list_row, container, false);

        TextView nameTV = rowView.findViewById(R.id.eventName_enrolled);
        TextView hostTV = rowView.findViewById(R.id.hostID_enrolled);
        TextView paxTV = rowView.findViewById(R.id.pax_enrolled);
        TextView timeTV = rowView.findViewById(R.id.time_enrolled);
        TextView dropTV = rowView.findViewById(R.id.drop_ev);
        TextView typeTV = rowView.findViewById(R.id.type_enrolled);
        TextView venueTV = rowView.findViewById(R.id.venue_enrolled);

        final Event cur = values.get(position);

        dropTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection(User.usersCollection).document(currentUser.getEmail())
                        .update(User.enrolledKey, FieldValue.arrayRemove(cur.getID()));

                db.collection(Event.availableEventCollection).document(cur.getID())
                        .update(Event.enrolledKey, FieldValue.increment(-1));

                db.collection(Event.availableEventCollection).document(cur.getID())
                        .update(Event.playersKey, FieldValue.arrayRemove(currentUser.getEmail()));

                db.collection(User.usersCollection).document(currentUser.getEmail())
                        .collection(User.historyCollection).document(cur.getID()).delete();

                WorkManager.getInstance().cancelAllWorkByTag(cur.getID());

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
