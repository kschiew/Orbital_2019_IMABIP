package com.orbital19.imabip.models;

import android.widget.ArrayAdapter;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event implements Serializable {

    public static String availableEventCollection = "AvailableEvents";
    public static String contactKey = "Contact";
    public static String descriptionKey = "Description";
    public static String hostIDKey = "Host";
    public static String nameKey = "Name";
    public static String typeKey = "Type";
    public static String venueKey = "Venue";
    public static String evTimeKey = "Time";
    public static String partySizeKey = "PartySize";
    public static String enrolledKey = "Enrolled";
    public static String playersKey = "Players";

    private String ID;
    private ArrayList<String> Contact = new ArrayList<>();
    private String Description;
    private String HostID;
    private String Name;
    private String Type;
    private String Venue;
    private Date EvTime;
    private Long PartySize;
    private Long Enrolled;
    private ArrayList<User> Players;


    public Event(ArrayList<String> contact, String desc, String host, String name, String type, String venue,
                 Date time, Long size, Long enrolled) {
        Contact.add(0, contact.get(0)); // email
        Contact.add(1, contact.get(1)); // phone
        Description = desc;
        HostID = host;
        Name = name;
        Type = type;
        Venue = venue;
        EvTime = time;
        PartySize = size;
        Enrolled = enrolled;
        ID = "" + (HostID.hashCode() + Name.hashCode() + EvTime.hashCode());
    }

    public ArrayList<String> getContact() { return Contact; }
    public String getDescription() { return Description; }
    public String getHost() { return HostID; }
    public String getName() { return Name; }
    public String getVenue() { return Venue; }
    public String getType() { return Type; }
    public Date getTime() { return EvTime; }
    public Long getPartySize() { return PartySize; }
    public Long getEnrolled() { return Enrolled; }
    public String getID() { return ID; }

    public void createEntry() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> event = new HashMap<>();

        event.put(contactKey, Contact);
        event.put(descriptionKey, Description);
        event.put(hostIDKey, HostID);
        event.put(nameKey, Name);
        event.put(venueKey, Venue);
        event.put(typeKey, Type);
        event.put(evTimeKey, EvTime);
        event.put(partySizeKey, PartySize);
        event.put(enrolledKey, Enrolled);

        db.collection(availableEventCollection).document(ID).set(event);
    }

    public void partyUp(User user) {
        PartySize++;
        Players.add(user);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(availableEventCollection).document(ID).update(partySizeKey, PartySize);
        db.collection(availableEventCollection).document(ID).update(playersKey, Players);
    }

    public void partyDown(User user) {
        PartySize--;
        Players.remove(user);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(availableEventCollection).document(ID).update(partySizeKey, PartySize);
        db.collection(availableEventCollection).document(ID).update(playersKey, Players);
    }
}
