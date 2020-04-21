package com.orbital19.imabip.teams;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.Chosen;
import com.orbital19.imabip.R;
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.teams.models.Team;
import com.orbital19.imabip.works.FilteringDataWorker;

import java.util.ArrayList;
import java.util.List;

public class ViewTeamActivities extends AppCompatActivity {
    private ListView listView;
    private List<Event> games = new ArrayList<>();
    private ViewTeamActivitiesAdapter viewTeamActivitiesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_team_activities);

        getSupportActionBar().setTitle("Team's activities");

        WorkManager workManager = WorkManager.getInstance();
        OneTimeWorkRequest update = new OneTimeWorkRequest.Builder(FilteringDataWorker.class)
                .addTag("Filter").build();
        workManager.enqueue(update);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        final String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final Team tm = (Team) bundle.getSerializable("Team");
        final ArrayList<String> unmodGamesList = (ArrayList<String>) bundle.getSerializable("Game List");
        final ArrayList<String> gameList = new ArrayList<>();
        final Integer[] slotsList = new Integer[unmodGamesList.size()];

        listView = findViewById(R.id.team_games_list);

        int i = 0;
        for (String gmID : unmodGamesList) {
            String[] part = gmID.split("__", 0); // part[0] = "%d", part[1] = gameID
            slotsList[i] = Integer.parseInt(part[0]);
            gameList.add(part[1]);
            i++;
        }

        loadGames(gameList, tm.getName());
        viewTeamActivitiesAdapter = new ViewTeamActivitiesAdapter(getApplicationContext(), games, tm);

        listView.setAdapter(viewTeamActivitiesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = (Event) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), Chosen.class);
                intent.putExtra("Event", event);
                intent.putExtra("FromTeam", true);
                intent.putExtra("Needed slots", slotsList[position]);
                intent.putExtra("Encoded item", unmodGamesList.get(position));
                intent.putExtra("AsCaptain", userEmail.equals(tm.getCaptain()));
                intent.putExtra("TeamInfo", tm);

                startActivity(intent);
            }
        });
    }

    private void loadGames(ArrayList<String> gameList, final String tmName) {
        final FirebaseFirestore fs = FirebaseFirestore.getInstance();

        games.removeAll(games);

        for (final String s : gameList) {
            fs.collection(Event.availableEventCollection).document(s)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot doc = task.getResult();

                    if (doc.exists()) {
                        Event EV = new Event((ArrayList<String>) doc.get(Event.contactKey), (String) doc.get(Event.descriptionKey),
                                (String) doc.get(Event.hostIDKey), (String) doc.get(Event.nameKey),
                                (String) doc.get(Event.typeKey), (String) doc.get(Event.venueKey),
                                (String) doc.get(Event.evTimeKey), (Long) doc.get(Event.partySizeKey),
                                (Long) doc.get(Event.enrolledKey), (boolean) doc.get(Event.byTeamKey));

                        games.add(EV);

                        viewTeamActivitiesAdapter.notifyDataSetChanged();
                    } else {
                        fs.collection(Team.teamsCollection).document(tmName)
                                .update(Team.teamJoinedKey, FieldValue.arrayRemove(s),
                                        Team.teamHostingKey, FieldValue.arrayRemove(s));
                    }
                }
            });
        }
    }
}
