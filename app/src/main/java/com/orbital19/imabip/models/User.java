package com.orbital19.imabip.models;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {
    public static String usersCollection = "Users";
    public static String emailKey = "Email";
    public static String nameKey = "Name";
    public static String phoneKey = "Phone";
    public static String idKey = "ID";
    public static String enrolledKey = "Enrolled";

    private String email;
    private String name;
    private String phone;
    private String ID;
    private ArrayList<String> enrolled;

    public User(String email, String name, String phone, String ID) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.ID = ID;
        enrolled = new ArrayList<>();
    }

    public void createEntry() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> map = new HashMap<>();

        map.put(emailKey, email);
        map.put(nameKey, name);
        map.put(phoneKey, phone);
        map.put(idKey, ID);
        map.put(enrolledKey, enrolled);

        db.collection(usersCollection).document(email).set(map);
    }

    public void enroll(Event event) {
        enrolled.add(event.getID());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(usersCollection).document(email).update(enrolledKey, enrolled);

        event.partyUp(this);
    }

    public void drop(Event event) {
        enrolled.remove(event.getID());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(usersCollection).document(email).update(enrolledKey, enrolled);

        event.partyDown(this);
    }
}
