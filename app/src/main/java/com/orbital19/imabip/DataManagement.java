package com.orbital19.imabip;

import android.support.annotation.NonNull;

import androidx.work.Data;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.models.User;
import com.orbital19.imabip.teams.models.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class DataManagement {
    private String userEmail;
    private static HashMap<String, Event> enrolled = new HashMap<>();
    private static HashMap<String, Event> hosting = new HashMap<>();
    private static HashMap<String, Team> teams = new HashMap<>();

    public DataManagement(String email) {
        userEmail = email;

        final FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection(User.usersCollection).document(userEmail)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    for (final String gameID : ((ArrayList<String>) document.get(User.enrolledKey))) {
                        fs.collection(Event.availableEventCollection).document(gameID)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot doc = task.getResult();

                                if (doc.exists())
                                    enrolled.put(gameID, new Event((ArrayList<String>) doc.get(Event.contactKey), (String) doc.get(Event.descriptionKey),
                                            (String) doc.get(Event.hostIDKey), (String) doc.get(Event.nameKey),
                                            (String) doc.get(Event.typeKey), (String) doc.get(Event.venueKey),
                                            (String) doc.get(Event.evTimeKey), (Long) doc.get(Event.partySizeKey),
                                            (Long) doc.get(Event.enrolledKey), (boolean) doc.get(Event.byTeamKey)));
                            }
                        });
                    }

                    for (final String hostingGame : (ArrayList<String>) document.get(User.hostingKey)) {
                        fs.collection(Event.availableEventCollection).document(hostingGame)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot doc = task.getResult();

                                if (doc.exists())
                                    hosting.put(hostingGame, new Event((ArrayList<String>) doc.get(Event.contactKey), (String) doc.get(Event.descriptionKey),
                                            (String) doc.get(Event.hostIDKey), (String) doc.get(Event.nameKey),
                                            (String) doc.get(Event.typeKey), (String) doc.get(Event.venueKey),
                                            (String) doc.get(Event.evTimeKey), (Long) doc.get(Event.partySizeKey),
                                            (Long) doc.get(Event.enrolledKey), (boolean) doc.get(Event.byTeamKey)));
                            }
                        });
                    }

                    for (final String teamName : (ArrayList<String>) document.get(User.joinedTeamsKey)) {
                        fs.collection(Team.teamsCollection).document(teamName)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot doc = task.getResult();

                                if (doc.exists()) {
                                    teams.put(teamName, new Team((String) doc.get(Team.teamNameKey),
                                            (String) doc.get(Team.captainKey),
                                            (String) doc.get(Team.capIDKey),
                                            (String) doc.get(Team.descriptionKey),
                                            (Long) doc.get(Team.sizeKey)));
                                }
                            }
                        });
                    }

                    for (final String teamName : (ArrayList<String>) document.get(User.captainOfKey)) {
                        fs.collection(Team.teamsCollection).document(teamName)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot doc = task.getResult();

                                if (doc.exists()) {
                                    teams.put(teamName, new Team((String) doc.get(Team.teamNameKey),
                                            (String) doc.get(Team.captainKey),
                                            (String) doc.get(Team.capIDKey),
                                            (String) doc.get(Team.descriptionKey),
                                            (Long) doc.get(Team.sizeKey)));
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public int changedTeam(String teamName, DocumentSnapshot doc, boolean still) {
        if (still) {
            if (teams.containsKey(teamName)) {
                Team localData = teams.get(teamName);
                if (localData.getSize().compareTo((Long) doc.get(Team.sizeKey)) > 0) {
                    teams.put(teamName, new Team((String) doc.get(Team.teamNameKey),
                            (String) doc.get(Team.captainKey),
                            (String) doc.get(Team.capIDKey),
                            (String) doc.get(Team.descriptionKey),
                            (Long) doc.get(Team.sizeKey)));

                    return 1;
                } else if (localData.getSize().compareTo((Long) doc.get(Team.sizeKey)) < 0) {
                    teams.put(teamName, new Team((String) doc.get(Team.teamNameKey),
                            (String) doc.get(Team.captainKey),
                            (String) doc.get(Team.capIDKey),
                            (String) doc.get(Team.descriptionKey),
                            (Long) doc.get(Team.sizeKey)));

                    return 0;
                } else {
                    return 2;
                }
            } else return -2;
        } else {
            teams.remove(teamName);
            return -1;
        }
    }

    public String changedJoinedGame(String ID, DocumentSnapshot doc, boolean still) {
        if (still) {
            enrolled.put(ID, new Event((ArrayList<String>) doc.get(Event.contactKey), (String) doc.get(Event.descriptionKey),
                    (String) doc.get(Event.hostIDKey), (String) doc.get(Event.nameKey),
                    (String) doc.get(Event.typeKey), (String) doc.get(Event.venueKey),
                    (String) doc.get(Event.evTimeKey), (Long) doc.get(Event.partySizeKey),
                    (Long) doc.get(Event.enrolledKey), (boolean) doc.get(Event.byTeamKey)));

            return "none";
        } else {
            Event game = enrolled.get(ID);
            enrolled.remove(ID);
            return game == null ? "A game" : game.getName();
        }
    }

    public int changedHostingGame(String ID, DocumentSnapshot doc, boolean still) {
        if (still) {

            if (hosting.containsKey(ID)) {
                Event localData = hosting.get(ID);
                Long local_enrolled = localData.getEnrolled();
                Long updated_enrolled = (Long) doc.get(Event.enrolledKey);

                hosting.put(ID, new Event((ArrayList<String>) doc.get(Event.contactKey), (String) doc.get(Event.descriptionKey),
                        (String) doc.get(Event.hostIDKey), (String) doc.get(Event.nameKey),
                        (String) doc.get(Event.typeKey), (String) doc.get(Event.venueKey),
                        (String) doc.get(Event.evTimeKey), (Long) doc.get(Event.partySizeKey),
                        (Long) doc.get(Event.enrolledKey), (boolean) doc.get(Event.byTeamKey)));

                if (local_enrolled < updated_enrolled) {
                    return 1;
                } else if (local_enrolled > updated_enrolled){
                    return 0;
                } else return -2;
            } else return -2;
        } else {
            hosting.remove(ID);
            return -1;
        }
    }

    public void update() {
        final FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection(User.usersCollection).document(userEmail)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    for (final String gameID : ((ArrayList<String>) document.get(User.enrolledKey))) {
                        fs.collection(Event.availableEventCollection).document(gameID)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot doc = task.getResult();

                                if (doc.exists())
                                    enrolled.put(gameID, new Event((ArrayList<String>) doc.get(Event.contactKey), (String) doc.get(Event.descriptionKey),
                                            (String) doc.get(Event.hostIDKey), (String) doc.get(Event.nameKey),
                                            (String) doc.get(Event.typeKey), (String) doc.get(Event.venueKey),
                                            (String) doc.get(Event.evTimeKey), (Long) doc.get(Event.partySizeKey),
                                            (Long) doc.get(Event.enrolledKey), (boolean) doc.get(Event.byTeamKey)));
                            }
                        });
                    }

                    for (final String hostingGame : (ArrayList<String>) document.get(User.hostingKey)) {
                        fs.collection(Event.availableEventCollection).document(hostingGame)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot doc = task.getResult();

                                if (doc.exists())
                                    hosting.put(hostingGame, new Event((ArrayList<String>) doc.get(Event.contactKey), (String) doc.get(Event.descriptionKey),
                                            (String) doc.get(Event.hostIDKey), (String) doc.get(Event.nameKey),
                                            (String) doc.get(Event.typeKey), (String) doc.get(Event.venueKey),
                                            (String) doc.get(Event.evTimeKey), (Long) doc.get(Event.partySizeKey),
                                            (Long) doc.get(Event.enrolledKey), (boolean) doc.get(Event.byTeamKey)));
                            }
                        });
                    }

                    for (final String teamName : (ArrayList<String>) document.get(User.joinedTeamsKey)) {
                        fs.collection(Team.teamsCollection).document(teamName)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot doc = task.getResult();

                                if (doc.exists()) {
                                    teams.put(teamName, new Team((String) doc.get(Team.teamNameKey),
                                            (String) doc.get(Team.captainKey),
                                            (String) doc.get(Team.capIDKey),
                                            (String) doc.get(Team.descriptionKey),
                                            (Long) doc.get(Team.sizeKey)));
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
