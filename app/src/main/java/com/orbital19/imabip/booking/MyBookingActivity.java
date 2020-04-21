package com.orbital19.imabip.booking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.orbital19.imabip.R;
import com.orbital19.imabip.booking.model.BookingObject;
import com.orbital19.imabip.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyBookingActivity extends AppCompatActivity {
    private List<BookingObject> bookings = new ArrayList<>();
    private ListView listView;
    private MyBookingAdapter myBookingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("MY BOOKINGS");

        loadUserBookings();
        myBookingAdapter = new MyBookingAdapter(getApplicationContext(), bookings);

        listView = findViewById(R.id.my_booking_list_view);
        listView.setAdapter(myBookingAdapter);

    }

    private void loadUserBookings() {
        final FirebaseFirestore fs = FirebaseFirestore.getInstance();

        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        bookings.removeAll(bookings);

        fs.collection(User.usersCollection).document(currentUser).collection(User.bookingsCollection)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                List<DocumentSnapshot> documents = task.getResult().getDocuments();


                for (DocumentSnapshot doc : documents) {
                    BookingObject booking = new BookingObject(doc.get(BookingObject.sportKey).toString(),
                                            doc.get(BookingObject.dateKey).toString(),
                                            doc.get(BookingObject.hourKey).toString(),
                                            doc.get(BookingObject.venueKey).toString(),
                                            doc.get(BookingObject.unitKey).toString(),
                                            currentUser);

                    bookings.add(booking);
                }

                Collections.sort(bookings);

                myBookingAdapter.notifyDataSetChanged();
            }
        });

    }
}
