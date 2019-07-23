package com.orbital19.imabip.teams;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.R;
import com.orbital19.imabip.models.User;
import com.orbital19.imabip.models.user.DisplayUser;
import com.orbital19.imabip.teams.models.Team;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Locale;

public class ChosenTeamView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_team_view);

        getSupportActionBar().setTitle("Team chosen");

        final FirebaseFirestore fs = FirebaseFirestore.getInstance();

        final Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        final Team tm = (Team) bundle.getSerializable("Team");

        TextView nameTV = findViewById(R.id.team_name_view);
        TextView captTV = findViewById(R.id.team_captain_view);
        TextView descTV = findViewById(R.id.team_description_view);
        TextView sizeTV = findViewById(R.id.team_size_view);
        TextView membTV = findViewById(R.id.team_members_view);
        Button leaveBtn = findViewById(R.id.leave_team_btn);
        Button joinBtn = findViewById(R.id.join_team_btn);

        final String curEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final boolean userIsCapt = curEmail.equals(tm.getCaptain());

        nameTV.setText(tm.getName());
        captTV.setText(tm.getCapID());
        descTV.setText(tm.getDescription());
        sizeTV.setText(String.format(Locale.getDefault(), "Size: %d", tm.getSize()));

        membTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewMembers.class);
                intent.putExtra("ViewingTeam", tm);
                startActivity(intent);
            }
        });

        if (bundle.getBoolean("Joined")) {
            leaveBtn.setVisibility(View.VISIBLE);
            joinBtn.setVisibility(View.GONE);

            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            leaveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userIsCapt) {
                        dialog.setTitle("Confirm").setMessage("Leaving means " + tm.getName() + " will be dismissed")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    tm.deleteTeam();
                                    startActivity(new Intent(getApplicationContext(), MyTeamsActivity.class));
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                        }).create().show();
                    } else {
                        dialog.setTitle("Confirm").setMessage("Ready to leave the team?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    tm.leftMember(curEmail);
                                    startActivity(new Intent(getApplicationContext(), MyTeamsActivity.class));
                                    finish();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                        }).create().show();
                    }
                }
            });
        } else {
            leaveBtn.setVisibility(View.GONE);
            joinBtn.setVisibility(View.VISIBLE);

            joinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tm.newMember(curEmail);
                    startActivity(new Intent(getApplicationContext(), MyTeamsActivity.class));
                    finish();
                }
            });
        }
    }
}
