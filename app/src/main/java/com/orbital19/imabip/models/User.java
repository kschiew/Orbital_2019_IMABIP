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
    public static String hostingKey = "Hosting";


    private String email;
    private String name;
    private String phone;
    private String ID;

    public User(String email, String name, String phone, String ID) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.ID = ID;
    }

    public User() {
        //No arg constructor needed
    }

    public void createEntry() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> map = new HashMap<>();

        map.put(emailKey, email);
        map.put(nameKey, name);
        map.put(phoneKey, phone);
        map.put(idKey, ID);
        map.put(enrolledKey, new ArrayList<String>());
        map.put(hostingKey, new ArrayList<String>());

        db.collection(usersCollection).document(email).set(map);
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
