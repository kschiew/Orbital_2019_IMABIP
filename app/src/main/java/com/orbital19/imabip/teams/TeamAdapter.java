package com.orbital19.imabip.teams;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.orbital19.imabip.R;
import com.orbital19.imabip.teams.models.Team;

import java.util.List;

public class TeamAdapter extends ArrayAdapter<Team> {
    private Context context;
    private List<Team> values;

    public TeamAdapter(Context context, List<Team> vals) {
        super(context, R.layout.details_joined_teams_list_row, vals);
        this.context = context;
        this.values = vals;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.details_joined_teams_list_row, parent, false);

        final TextView nameTV = rowView.findViewById(R.id.name_team);
        final TextView captainTV = rowView.findViewById(R.id.captain_team);
        final ImageView starIM = rowView.findViewById(R.id.star_for_cap_img);

        final Team team = values.get(position);

        nameTV.setText(team.getName());
        captainTV.setText(team.getCapID());

        String curEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (curEmail.equals(team.getCaptain()))
            starIM.setVisibility(View.VISIBLE);
        else
            starIM.setVisibility(View.INVISIBLE);

        return rowView;
    }
}
