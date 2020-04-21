package com.orbital19.imabip.booking.model;

import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.models.User;

import java.io.Serializable;
import java.util.HashMap;

public class BookingObject implements Serializable, Comparable<BookingObject> {
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

    public void deleteEntry() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        fs.collection(bookingsCollection).document(Venue).collection(Unit).document(Name).delete();
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

    public void deleteFromUser(String email) {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        fs.collection(User.usersCollection).document(email).collection(User.bookingsCollection)
                .document(Name).delete();
    }

    public String getSport() {
        return Sport;
    }

    public String getName() {
        return Name;
    }

    public String getHour() {
        return Hour;
    }

    public String getVenue() {
        return Venue;
    }

    public String getUnit() {
        return Unit;
    }

    public String getDate() {
        return Date;
    }

    @Override
    public int compareTo(BookingObject o) {
        String[] time1 = this.Date.split("\\.");
        String[] time2 = o.Date.split("\\.");

        if (!this.Sport.equals(o.Sport))
            return this.Sport.compareTo(o.Sport);
        else if (!this.Venue.equals(o.Venue))
            return this.Venue.compareTo(o.Venue);
        else if (!this.Unit.equals(o.Unit))
            return this.Unit.compareTo(o.Unit);
        else if (!time2[2].equals(time1[2]))
            return time1[2].compareTo(time2[2]);
        else if (!time2[1].equals(time1[1]))
            return time1[1].compareTo(time2[1]);
        else
            return time1[0].compareTo(time2[0]);

    }
}
