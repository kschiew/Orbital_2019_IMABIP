package com.orbital19.imabip.teams.models;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;

public class Team implements Serializable {
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
        map.put(sizeKey, 1);

        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        fs.collection(teamsCollection).document(name).set(map);
        fs.collection(teamsCollection).document(name).update(membersKey, FieldValue.arrayUnion(captain));
    }

    public void deleteTeam() {
        FirebaseFirestore.getInstance().collection(teamsCollection).document(name).delete();
    }

    public void newMember(String email) {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        fs.collection(teamsCollection).document(name).update(membersKey, FieldValue.arrayUnion(email),
                sizeKey, FieldValue.increment(1));
    }

    public void leftMember(String email) {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        fs.collection(teamsCollection).document(name).update(membersKey, FieldValue.arrayRemove(email),
                sizeKey, FieldValue.increment(-1));
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
}
