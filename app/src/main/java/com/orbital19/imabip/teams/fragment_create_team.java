package com.orbital19.imabip.teams;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.R;
import com.orbital19.imabip.models.User;
import com.orbital19.imabip.teams.models.Team;

public class fragment_create_team extends Fragment {

    private EditText inName, cfmEmail, inDesc;

    public fragment_create_team() {
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
        View view = inflater.inflate(R.layout.fragment_create_team, container, false);

        inName = view.findViewById(R.id.create_team_name);
        cfmEmail = view.findViewById(R.id.confirm_captain_email);
        inDesc = view.findViewById(R.id.create_team_description);


        Button createBtn = view.findViewById(R.id.create_btn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });


        return view;
    }

    private void check() {
        final String tmName = inName.getText().toString();
        final String tmCapt = cfmEmail.getText().toString();
        final String tmDesc = inDesc.getText().toString();

        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        final String curUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        final boolean[] valids = new boolean[3];
        valids[0] = true; valids[1] = true; valids[2] = true;

        if (Strings.isEmptyOrWhitespace(tmName)) {
            Toast.makeText(getContext(), "Invalid team name", Toast.LENGTH_SHORT).show();
            valids[0] = false;
        } else {
            fs.collection(Team.teamsCollection).document(tmName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Toast.makeText(getContext(), "Invalid team name", Toast.LENGTH_SHORT).show();
                        valids[0] = false;
                    }
                }
            });
        }

        if (valids[0])
            if (!tmCapt.equals(curUser)) {
                Toast.makeText(getContext(), "Wrong email", Toast.LENGTH_SHORT).show();
                valids[1] = false;
            }

        if (Strings.isEmptyOrWhitespace(tmDesc)) {
            Toast.makeText(getContext(), "Invalid description", Toast.LENGTH_SHORT).show();
            valids[2] = false;
        }

        if (valids[0] && valids[1] && valids[2]) {
            fs.collection(User.usersCollection).document(curUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    String id = (String) task.getResult().get(User.idKey);

                    Team tm = new Team(tmName, curUser, id, tmDesc, Long.valueOf(1));
                    tm.formTeam();
                    Toast.makeText(getContext(), "Team formed successfully!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getContext(), MyTeamsActivity.class));
                }
            });
        }
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
