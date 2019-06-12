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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.models.User;

import java.util.ArrayList;
import java.util.List;


public class fragment_eventList extends Fragment {
    private List<Event> events = new ArrayList<>();
    private ListView list_View;
    private EventAdapter eventAdapter;

    public fragment_eventList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        loadUserData();
        eventAdapter = new EventAdapter(getContext(), events);

        list_View = view.findViewById(R.id.listView);
        list_View.setAdapter(eventAdapter);

        // on click listener
        list_View.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Event ev = (Event) parent.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), Chosen.class);
                intent.putExtra("Event", ev);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        list_View = getView().findViewById(R.id.listView);
        list_View.setAdapter(eventAdapter);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        list_View = getView().findViewById(R.id.listView);
        list_View.setAdapter(eventAdapter);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        list_View = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void loadUserData() {
        final FirebaseFirestore fs = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        fs.collection(Event.availableEventCollection).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                // clean up the list to prevent double copies
                events.removeAll(events);

                for (DocumentSnapshot doc : documents) {
                    // filter the enrolled event



                    // should perform checking
                    Event EV = new Event((ArrayList<String>) doc.get(Event.contactKey), (String) doc.get(Event.descriptionKey),
                            (String) doc.get(Event.hostIDKey), (String) doc.get(Event.nameKey),
                            (String) doc.get(Event.typeKey), (String) doc.get(Event.venueKey),
                            (String) doc.get(Event.evTimeKey), (Long) doc.get(Event.partySizeKey),
                            (Long) doc.get(Event.enrolledKey));

                    if (!EV.getContact().get(0).equals(user.getEmail()))
                        events.add(EV);
                }

                eventAdapter.notifyDataSetChanged();
            }
        });
    }
}
