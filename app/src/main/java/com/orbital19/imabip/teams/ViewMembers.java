package com.orbital19.imabip.teams;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.R;
import com.orbital19.imabip.models.User;
import com.orbital19.imabip.models.user.DisplayUser;
import com.orbital19.imabip.teams.models.Team;

import java.util.ArrayList;
import java.util.List;

public class ViewMembers extends AppCompatActivity {
    private ListView listView;
    private List<User> members = new ArrayList<>();
    private ViewMembersAdapter viewMembersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_members);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle("LIST OF MEMBERS");

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        Team tm = (Team) bundle.getSerializable("ViewingTeam");

        loadMembers(tm);
        viewMembersAdapter = new ViewMembersAdapter(getApplicationContext(), members);

        listView = findViewById(R.id.mem_list);
        listView.setAdapter(viewMembersAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), DisplayUser.class);
                intent.putExtra("toViewUser", user);
                startActivity(intent);
            }
        });
    }

    private void loadMembers(Team tm) {
        final FirebaseFirestore fs = FirebaseFirestore.getInstance();

        members.removeAll(members);

        fs.collection(Team.teamsCollection).document(tm.getName())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {
                    Log.d("Mem list view", "Found team");
                    ArrayList<String> mems = (ArrayList<String>) doc.get(Team.membersKey);

                    for (final String email : mems) {
                        Log.d("Member", email);
                        fs.collection(User.usersCollection).document(email)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot doc = task.getResult();

                                if (doc.exists()) {
                                    Log.d("user found", email);
                                    User user = new User((String) doc.get(User.emailKey), (String) doc.get(User.nameKey),
                                            (String) doc.get(User.phoneKey), (String) doc.get(User.idKey));


                                    members.add(user);
                                }

                                viewMembersAdapter.notifyDataSetChanged();
                            }
                        });

                    }

                }
            }
        });
    }
}
