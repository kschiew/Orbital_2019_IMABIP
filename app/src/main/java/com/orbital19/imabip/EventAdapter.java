package com.orbital19.imabip;

import android.content.Context;
import androidx.annotation.NonNull;
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
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends ArrayAdapter<Event> {
    private final Context context;
    private final List<Event> values;

    public EventAdapter(Context contxt, List<Event> vals) {
        super(contxt, R.layout.details_event_list_row, vals);
        this.context = contxt;
        this.values = vals;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.details_event_list_row, parent, false);

        TextView nameTV = rowView.findViewById(R.id.eventName);
        TextView hostTV = rowView.findViewById(R.id.hostID);
        TextView paxTV = rowView.findViewById(R.id.pax);
        TextView timeTV = rowView.findViewById(R.id.time);
        TextView typeTV = rowView.findViewById(R.id.type);
        TextView venueTV = rowView.findViewById(R.id.venue);
        final TextView joinedTV = rowView.findViewById(R.id.joined);

        final Event cur = values.get(position);

        nameTV.setText(cur.getName());
        hostTV.setText(cur.getHost());
        typeTV.setText(cur.getType());
        timeTV.setText(cur.getTime());
        venueTV.setText(cur.getVenue());
        String pax = String.format(Locale.getDefault(),
                "%d / %d", cur.getEnrolled(), cur.getPartySize());
        paxTV.setText(pax);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore.getInstance().collection(User.usersCollection).document(user.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                joinedTV.setVisibility(View.VISIBLE);

                ArrayList<String> lst = (ArrayList<String>) task.getResult().get(User.enrolledKey);

                if (lst.contains(cur.getID()))
                    joinedTV.setVisibility(View.VISIBLE);
                else
                    joinedTV.setVisibility(View.INVISIBLE);

            }
        });


        return rowView;
    }

}
