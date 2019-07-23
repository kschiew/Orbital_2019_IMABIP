package com.orbital19.imabip.teams;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.orbital19.imabip.R;
import com.orbital19.imabip.models.User;

import java.util.List;

public class ViewMembersAdapter extends ArrayAdapter {
    private final Context context;
    private final List<User> values;

    public ViewMembersAdapter(Context contxt, List<User> vals) {
        super(contxt, R.layout.details_teams_members_list, vals);
        this.context = contxt;
        this.values = vals;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.details_teams_members_list, parent, false);

        TextView memID = rowView.findViewById(R.id.mem_id);
        TextView memEM = rowView.findViewById(R.id.mem_email);

        final User user = values.get(position);

        memID.setText(user.getID());
        memEM.setText(user.getEmail());

        return rowView;
    }
}
