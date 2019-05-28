package com.orbital19.imabip.models;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.sql.Timestamp;
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

    private String ID;
    private String[] Contact = new String[2];
    private String Description;
    private String HostID;
    private String Name;
    private String Type;
    private String Venue;
    private Timestamp EvTime;
    private Long PartySize;
    private Long Enrolled;


    public Event(String[] contact, String desc, String host, String name, String type, String venue,
                 Timestamp time, Long size, Long enrolled) {
        Contact[0] = contact[0];
        Contact[1] = contact[1];
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

    public String[] getContact() { return Contact; }
    public String getDescription() { return Description; }
    public String getHost() { return HostID; }
    public String getName() { return Name; }
    public String getVenue() { return Venue; }
    public String getType() { return Type; }
    public Timestamp getTime() { return EvTime; }
    public Long getPartySize() { return PartySize; }
    public Long getEnrolled() { return Enrolled; }

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

    public void partyUp() {
        PartySize++;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(availableEventCollection).document(ID).update(partySizeKey, PartySize);
    }
}
