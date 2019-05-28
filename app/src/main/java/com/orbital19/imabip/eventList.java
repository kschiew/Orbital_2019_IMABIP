package com.orbital19.imabip;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.orbital19.imabip.models.Event;

import java.util.ArrayList;
import java.util.List;


public class eventList extends Fragment {
    private List<Event> events = new ArrayList<>();
    private ListView listView;
    private EventAdapter eventAdapter;

    public eventList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventAdapter = new EventAdapter(getContext(), events);

        listView.setAdapter(eventAdapter);

        //on click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Event ev = (Event) parent.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), Chosen.class);
                intent.putExtra("Event", ev);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        listView = (ListView) view.findViewById(R.id.eventList);
        return view;
    }

    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
