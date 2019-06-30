package com.orbital19.imabip;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private List<Event> events = new ArrayList<>();
    private ListView list_View;
    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("HISTORY");

        loadHistory();
        historyAdapter = new HistoryAdapter(getApplicationContext(), events);

        list_View = findViewById(R.id.history_list_view);
        list_View.setAdapter(historyAdapter);

        // on click listener
        list_View.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Event ev = (Event) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), Chosen.class);
                intent.putExtra("Event", ev);
                intent.putExtra("History", true);
                startActivity(intent);
            }
        });

    }

    private void loadHistory() {
        final FirebaseFirestore fs = FirebaseFirestore.getInstance();

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        events.removeAll(events);

        fs.collection(User.usersCollection).document(currentUser.getEmail()).collection(User.historyCollection)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

                    events.add(EV);
                }

                Collections.sort(events, Collections.<Event>reverseOrder());

                historyAdapter.notifyDataSetChanged();
            }
        });

    }
}
