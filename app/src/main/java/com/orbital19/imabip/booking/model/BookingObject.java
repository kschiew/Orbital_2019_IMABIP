package com.orbital19.imabip.booking.model;

import android.support.v4.widget.NestedScrollView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.models.User;

import java.io.Serializable;
import java.util.HashMap;

public class BookingObject implements Serializable {
    private String Name, Sport, Date, Hour, Venue, Unit, Email;

    public static String bookingsCollection = "Bookings";
    public static String nameKey = "Name";
    public static String sportKey = "Sport";
    public static String dateKey = "Date";
    public static String hourKey = "Hour";
    public static String venueKey = "Venue";
    public static String unitKey = "Unit";
    public static String emailKey = "Booker";

    public BookingObject(String sport, String date, String hour, String venue, String unit, String email) {
        Sport = sport;
        Date = date;
        Hour = hour;
        Venue = venue;
        Unit = unit;
        Name = Hour + Unit + Venue + Date;
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public String getHour() {
        return Hour;
    }

    public void newEntry() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        HashMap<String, Object> map = new HashMap<>();

        map.put(nameKey, Name);
        map.put(sportKey, Sport);
        map.put(dateKey, Date);
        map.put(hourKey, Hour);
        map.put(venueKey, Venue);
        map.put(unitKey, Unit);
        map.put(emailKey, Email);

        fs.collection(bookingsCollection).document(Venue).collection(Unit).document(Name).set(map);
    }

    public static void deleteEntry(String booking) {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        fs.collection(bookingsCollection).document(booking).delete();
    }

    public void addToUser(String email) {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        HashMap<String, Object> map = new HashMap<>();

        map.put(nameKey, Name);
        map.put(sportKey, Sport);
        map.put(dateKey, Date);
        map.put(hourKey, Hour);
        map.put(venueKey, Venue);
        map.put(unitKey, Unit);

        fs.collection(User.usersCollection).document(email).collection(User.bookingsCollection)
                .document(Name).set(map);
    }
}
