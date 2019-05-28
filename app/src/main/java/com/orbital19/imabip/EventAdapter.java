package com.orbital19.imabip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.orbital19.imabip.models.Event;

import org.w3c.dom.Text;

import java.util.List;

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

        Event cur = values.get(position);

        nameTV.setText(cur.getName());
        hostTV.setText(cur.getHost());
        timeTV.setText(cur.getTime().toString());
        String pax = String.format("%d / %d", cur.getEnrolled().longValue(), cur.getPartySize().longValue());
        paxTV.setText(pax);


        return rowView;
    }

}
