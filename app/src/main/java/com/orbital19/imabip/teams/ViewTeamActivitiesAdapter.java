package com.orbital19.imabip.teams;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.R;
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.teams.models.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ViewTeamActivitiesAdapter extends ArrayAdapter {
    private final Context context;
    private final List<Event> values;
    private final Team team;

    public ViewTeamActivitiesAdapter(Context contxt, List<Event> vals, Team tm) {
        super(contxt, R.layout.details_teams_activities_list, vals);
        this.context = contxt;
        this.values = vals;
        this.team = tm;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.details_teams_activities_list, parent, false);

        final Event ev = values.get(position);

        final TextView nameTV = rowView.findViewById(R.id.team_eventName);
        final TextView typeTV = rowView.findViewById(R.id.team_type);
        final TextView hostTV = rowView.findViewById(R.id.team_host);
        final TextView timeTV = rowView.findViewById(R.id.team_time);
        final TextView venuTV = rowView.findViewById(R.id.team_venue);
        final TextView slotTV = rowView.findViewById(R.id.team_player_slots);

        nameTV.setText(ev.getName());
        typeTV.setText(ev.getType());
        hostTV.setText(ev.getHost());
        timeTV.setText(ev.getTime());
        venuTV.setText(ev.getVenue());
        FirebaseFirestore.getInstance().collection(Event.availableEventCollection)
                .document(ev.getID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {
                    HashMap<String, Long> teamSlots = (HashMap<String, Long>) doc.get(Event.teamSlotsKey);
                    slotTV.setText(
                            String.format(
                                    Locale.getDefault(),
                                    "Players needed: %d", teamSlots.get(team.getName())));
                }
            }
        });

        return rowView;
    }
}
