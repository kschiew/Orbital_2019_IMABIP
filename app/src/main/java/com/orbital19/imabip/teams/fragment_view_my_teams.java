package com.orbital19.imabip.teams;

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
import android.widget.SearchView;

import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.orbital19.imabip.Chosen;
import com.orbital19.imabip.EventAdapter;
import com.orbital19.imabip.R;
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.models.User;
import com.orbital19.imabip.teams.models.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class fragment_view_my_teams extends Fragment {
    private List<Team> teams = new ArrayList<>();
    private ListView list_View;
    private TeamAdapter teamAdapter;

    public fragment_view_my_teams() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_teams_list, container, false);

        loadData();
        teamAdapter = new TeamAdapter(getContext(), teams);

        list_View = view.findViewById(R.id.teams_joined_list);
        list_View.setAdapter(teamAdapter);

        // on click listener
        list_View.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Team team = (Team) parent.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), ChosenTeamView.class);
                intent.putExtra("Team", team);
                intent.putExtra("Joined", true);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        list_View = getView().findViewById(R.id.teams_joined_list);
        list_View.setAdapter(teamAdapter);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        list_View = getView().findViewById(R.id.teams_joined_list);
        list_View.setAdapter(teamAdapter);
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

    public void loadData() {
        final FirebaseFirestore fs = FirebaseFirestore.getInstance();
        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        teams.removeAll(teams);

        fs.collection(User.usersCollection).document(email).collection(User.joinedTeamsCollection).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> lst = task.getResult().getDocuments();

                        for (DocumentSnapshot doc : lst) {
                            Team team = new Team((String) doc.get(Team.teamNameKey),
                                    (String) doc.get(Team.captainKey),
                                    (String) doc.get(Team.capIDKey),
                                    (Long) doc.get(Team.sizeKey));

                            teams.add(team);
                        }

                        Collections.sort(teams);

                        teamAdapter.notifyDataSetChanged();
                    }
                });
    }

}
