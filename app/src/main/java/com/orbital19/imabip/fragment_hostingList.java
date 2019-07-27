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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class fragment_hostingList extends Fragment {
    private List<Event> events = new ArrayList<>();
    private ListView list_View;
    private HostingAdapter hostingAdapter;

    public fragment_hostingList() {
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
        View view = inflater.inflate(R.layout.fragment_hosting_list, container, false);

        loadHosting();
        hostingAdapter = new HostingAdapter(getContext(), events);


        list_View = view.findViewById(R.id.host_list_view);
        list_View.setAdapter(hostingAdapter);

        list_View.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event ev = (Event) parent.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), Chosen.class);
                intent.putExtra("Event", ev);
                intent.putExtra("hosting", true);
                intent.putExtra("History", false);
                intent.putExtra("FromTeam", false);
                startActivity(intent);
            }
        });

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

    private void loadHosting() {
        final FirebaseFirestore fs = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        events.removeAll(events);

        fs.collection(User.usersCollection).document(user.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot thisUser = task.getResult();
                ArrayList<String> enrolledList = (ArrayList<String>) thisUser.get(User.hostingKey);
                for (final String id : enrolledList) {
                    fs.collection(Event.availableEventCollection).document(id)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot doc = task.getResult();

                            if (doc.exists()) {
                                Event EV = new Event((ArrayList<String>) doc.get(Event.contactKey), (String) doc.get(Event.descriptionKey),
                                        (String) doc.get(Event.hostIDKey), (String) doc.get(Event.nameKey),
                                        (String) doc.get(Event.typeKey), (String) doc.get(Event.venueKey),
                                        (String) doc.get(Event.evTimeKey), (Long) doc.get(Event.partySizeKey),
                                        (Long) doc.get(Event.enrolledKey), false);

                                events.add(EV);
                            } else {
                                fs.collection(User.usersCollection).document(user.getEmail())
                                        .update(User.hostingKey, FieldValue.arrayRemove(id));
                            }

                            Collections.sort(events);

                            hostingAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

    }

}
