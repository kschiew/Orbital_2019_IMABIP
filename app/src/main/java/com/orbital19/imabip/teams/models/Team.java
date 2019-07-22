package com.orbital19.imabip.teams.models;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.models.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Team implements Serializable, Comparable<Team> {
    public static String teamsCollection = "ActiveTeams";
    public static String teamNameKey = "Name";
    public static String captainKey = "Captain"; // email
    public static String capIDKey = "CapID";
    public static String membersKey = "Members"; // array of emails
    public static String sizeKey = "Size"; // number
    public static String teamHostingKey = "Hosting"; // array
    public static String teamJoinedKey = "Participating"; // array

    private String name, captain, capID;
    private Long size;

    public Team(String name, String captain, String capID, Long size) {
        this.name = name;
        this.captain = captain;
        this.capID = capID;
        this.size = size;
    }

    public void formTeam() {
        HashMap<String, Object> map = new HashMap<>();

        map.put(teamNameKey, name);
        map.put(captainKey, captain);
        map.put(capIDKey, capID);
        map.put(sizeKey, size);

        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        fs.collection(teamsCollection).document(name).set(map);
        fs.collection(teamsCollection).document(name).update(membersKey, FieldValue.arrayUnion(captain));
        fs.collection(User.usersCollection).document(captain).collection(User.captainOfCollection)
                .document(name).set(map);
        fs.collection(User.usersCollection).document(captain).collection(User.joinedTeamsCollection)
                .document(name).set(map);
    }

    public void deleteTeam() {
        final FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection(teamsCollection).document(name).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();

                ArrayList<String> mems = (ArrayList<String>) doc.get(membersKey);
                for (String email : mems) {
                    fs.collection(User.usersCollection).document(email).collection(User.joinedTeamsCollection)
                            .document(name).delete();
                }
            }
        });

        fs.collection(teamsCollection).document(name).delete();
        fs.collection(User.usersCollection).document(captain).collection(User.captainOfCollection)
                .document(name).delete();
    }

    public void newMember(String email) {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        fs.collection(teamsCollection).document(name).update(membersKey, FieldValue.arrayUnion(email),
                sizeKey, FieldValue.increment(1));

        HashMap<String, Object> map = new HashMap<>();

        map.put(teamNameKey, name);
        map.put(captainKey, captain);
        map.put(capIDKey, capID);
        map.put(sizeKey, size);
        fs.collection(User.usersCollection).document(email).collection(User.joinedTeamsCollection)
                .document(name).set(map);
    }

    public void leftMember(String email) {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        fs.collection(teamsCollection).document(name).update(membersKey, FieldValue.arrayRemove(email),
                sizeKey, FieldValue.increment(-1));

        fs.collection(User.usersCollection).document(email).collection(User.joinedTeamsCollection)
                .document(name).delete();
    }

    public String getName() {
        return name;
    }

    public String getCaptain() {
        return captain;
    }

    public String getCapID() {
        return capID;
    }

    @Override
    public int compareTo(Team t) {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        boolean check1 = userEmail.equals(this.captain);
        boolean check2 = userEmail.equals(t.captain);

        if (check1 && check2)
            return this.name.compareTo(t.name);
        else if (check1)
            return -1;
        else
            return 1;
    }

    public Long getSize() {
        return size;
    }
}
