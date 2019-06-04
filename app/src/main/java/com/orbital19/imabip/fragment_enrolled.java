package com.orbital19.imabip;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.Date;


public class fragment_enrolled extends Fragment {

    private ArrayList<Event> events = new ArrayList<>();
    private ListView listView;
    private EnrolledAdapter enrolledAdapter;

    public fragment_enrolled() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enrolled_view, container, false);

        enrolledAdapter = new EnrolledAdapter(getContext(), events);

        listView = view.findViewById(R.id.enrolled_list);
        listView.setAdapter(enrolledAdapter);

        loadEvents();

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

    private void loadEvents() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        db.collection(User.usersCollection).document(currentUser.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    events.removeAll(events);

                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        ArrayList<String> evs = (ArrayList<String>) document.get(User.enrolledKey);

                        for (String eventID : evs) {
                            db.collection(Event.availableEventCollection).document(eventID)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot doc = task.getResult();

                                    Event EV = new Event((ArrayList<String>) doc.get(Event.contactKey), (String) doc.get(Event.descriptionKey),
                                            (String) doc.get(Event.hostIDKey), (String) doc.get(Event.nameKey),
                                            (String) doc.get(Event.typeKey), (String) doc.get(Event.venueKey),
                                            (Date) doc.get(Event.evTimeKey), (Long) doc.get(Event.partySizeKey),
                                            (Long) doc.get(Event.enrolledKey));

                                    events.add(EV);
                                }
                            });
                        }
                    }
                }
            }
        });
    }
}
