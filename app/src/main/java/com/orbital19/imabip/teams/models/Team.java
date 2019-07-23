package com.orbital19.imabip.teams.models;

import android.support.annotation.NonNull;
import android.util.Log;

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
    public static String descriptionKey = "Description";
    public static String teamHostingKey = "Hosting"; // array
    public static String teamJoinedKey = "Participating"; // array

    private String name, description, captain, capID;
    private Long size;

    public Team(String name, String captain, String capID, String description,Long size) {
        this.name = name;
        this.captain = captain;
        this.capID = capID;
        this.description = description;
        this.size = size;
    }

    public void formTeam() {
        HashMap<String, Object> map = new HashMap<>();

        map.put(teamNameKey, name);
        map.put(captainKey, captain);
        map.put(capIDKey, capID);
        map.put(descriptionKey, description);
        map.put(sizeKey, size);
        map.put(membersKey, new ArrayList<>());

        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        fs.collection(teamsCollection).document(name).set(map);
        fs.collection(teamsCollection).document(name).update(membersKey, FieldValue.arrayUnion(captain));
        fs.collection(User.usersCollection).document(captain).update(User.joinedTeamsKey, FieldValue.arrayUnion(name),
                User.captainOfKey, FieldValue.arrayUnion(name));
    }

    public void deleteTeam() {
        final FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection(teamsCollection).document(name).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {
                    ArrayList<String> mems = new ArrayList<>();
                    mems.addAll((ArrayList<String>) doc.get(membersKey));
                    for (String email : mems) {
                        Log.d("Deleting team", email);
                        fs.collection(User.usersCollection).document(email)
                                .update(User.joinedTeamsKey, FieldValue.arrayRemove(name));

                    }
                }

                fs.collection(User.usersCollection).document(captain).
                        update(User.captainOfKey, FieldValue.arrayRemove(name));
                fs.collection(teamsCollection).document(name).delete();
            }
        });
    }

    public void newMember(String email) {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        fs.collection(teamsCollection).document(name).update(membersKey, FieldValue.arrayUnion(email),
                sizeKey, FieldValue.increment(1));

        HashMap<String, Object> map = new HashMap<>();

        map.put(teamNameKey, name);
        map.put(captainKey, captain);
        map.put(capIDKey, capID);
        map.put(descriptionKey, description);
        map.put(sizeKey, size);
        fs.collection(User.usersCollection).document(email).update(User.joinedTeamsKey, FieldValue.arrayUnion(name));
    }

    public void leftMember(String email) {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        fs.collection(teamsCollection).document(name).update(membersKey, FieldValue.arrayRemove(email),
                sizeKey, FieldValue.increment(-1));

        fs.collection(User.usersCollection).document(email).update(User.joinedTeamsKey, FieldValue.arrayRemove(name));
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

    public String getDescription() {
        return description;
    }
}
