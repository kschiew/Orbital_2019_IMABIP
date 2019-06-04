package com.orbital19.imabip;

import android.content.Context;
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
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.models.User;

import java.util.ArrayList;
import java.util.List;

public class EnrolledAdapter extends ArrayAdapter<Event> {

    private final Context context;
    private final List<Event> values;

    public EnrolledAdapter(Context contxt, List<Event> vals) {
        super(contxt, R.layout.details_enrolled_list_row, vals);
        this.context = contxt;
        this.values = vals;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.details_enrolled_list_row, parent, false);

        TextView nameTV = rowView.findViewById(R.id.eventName_enrolled);
        TextView hostTV = rowView.findViewById(R.id.hostID_enrolled);
        TextView paxTV = rowView.findViewById(R.id.pax_enrolled);
        TextView timeTV = rowView.findViewById(R.id.time_enrolled);
        TextView dropTV = rowView.findViewById(R.id.drop_ev);

        final Event cur = values.get(position);

        dropTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore.getInstance().collection(User.usersCollection)
                        .document(currentUser.getEmail()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();

                        User user = new User((String) doc.get(User.emailKey), (String) doc.get(User.nameKey),
                                (String) doc.get(User.phoneKey), (String) doc.get(User.idKey));

                        ArrayList<String> evs = (ArrayList<String>) doc.get(User.enrolledKey);
                        evs.remove(cur);
                        FirebaseFirestore.getInstance().collection(User.usersCollection)
                                .document(user.getID()).update(User.enrolledKey, evs);

                        cur.partyDown(user);
                    }
                });
            }
        });

        nameTV.setText(cur.getName());
        hostTV.setText(cur.getHost());
        timeTV.setText(cur.getTime().toString());
        String pax = String.format("%d / %d", cur.getEnrolled().longValue(), cur.getPartySize().longValue());
        paxTV.setText(pax);


        return rowView;
    }
}
