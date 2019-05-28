package com.orbital19.imabip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.orbital19.imabip.models.Event;

import java.sql.Time;
import java.sql.Timestamp;
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

        loadUserData();

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void loadUserData() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();


        fs.collection(Event.availableEventCollection).orderBy(Event.evTimeKey).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                // clean up the list to prevent double copies
                events.removeAll(events);

                for (DocumentSnapshot doc : documents) {

                    // should perform checking
                    Event EV = new Event((String[]) doc.get(Event.contactKey), (String) doc.get(Event.descriptionKey),
                            (String) doc.get(Event.hostIDKey), (String) doc.get(Event.nameKey),
                            (String) doc.get(Event.typeKey), (String) doc.get(Event.venueKey),
                            (Timestamp) doc.get(Event.evTimeKey), (Long) doc.get(Event.partySizeKey),
                            (Long) doc.get(Event.enrolledKey));

                    events.add(EV);
                }

                eventAdapter.notifyDataSetChanged();
            }
        });
    }
}
