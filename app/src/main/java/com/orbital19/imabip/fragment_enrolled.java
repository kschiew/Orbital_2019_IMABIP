package com.orbital19.imabip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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


public class fragment_enrolled extends Fragment {

    private ArrayList<Event> events = new ArrayList<>();
    private ListView listView;
    private EnrolledAdapter enrolledAdapter;

    public fragment_enrolled() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enrolled_view, container, false);

        loadEvents();
        enrolledAdapter = new EnrolledAdapter(getContext(), events);

        listView = view.findViewById(R.id.enrolled_list);
        listView.setAdapter(enrolledAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Event ev = (Event) parent.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), Chosen.class);
                intent.putExtra("Event", ev);
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

    private void loadEvents() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        events.removeAll(events);

        db.collection(User.usersCollection).document(currentUser.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot thisUser = task.getResult();
                ArrayList<String> enrolledList = (ArrayList<String>) thisUser.get(User.enrolledKey);
                for (final String id : enrolledList) {
                    db.collection(Event.availableEventCollection).document(id)
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
                                EV.toUserHistory(currentUser.getEmail());
                            } else {
                                db.collection(User.usersCollection).document(currentUser.getEmail())
                                        .update(User.enrolledKey, FieldValue.arrayRemove(id));
                            }

                            Collections.sort(events);

                            enrolledAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}
