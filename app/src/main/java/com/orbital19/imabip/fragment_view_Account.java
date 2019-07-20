package com.orbital19.imabip;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.booking.BookingActivity;
import com.orbital19.imabip.booking.MyBookingActivity;
import com.orbital19.imabip.models.User;
import com.orbital19.imabip.models.user.DisplayUser;

public class fragment_view_Account extends Fragment {

    private TextView viewAccount, viewHistory, newBooking, viewBooking, viewTeams;

    private Button signOutBtn;

    public fragment_view_Account() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_view_account, container, false);

        viewAccount = view.findViewById(R.id.view_my_account);
        viewHistory = view.findViewById(R.id.view_history);
        newBooking = view.findViewById(R.id.new_booking);
        viewBooking = view.findViewById(R.id.view_bookings);
        viewTeams = view.findViewById(R.id.view_teams);
        signOutBtn = view.findViewById(R.id.sign_out_button);

        viewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                FirebaseFirestore.getInstance().collection(User.usersCollection).document(user.getEmail())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();
                        User user = new User((String) doc.get(User.emailKey), (String) doc.get(User.nameKey),
                                (String) doc.get(User.phoneKey), (String) doc.get(User.idKey));

                        Intent intent = new Intent(getContext(), DisplayUser.class);
                        intent.putExtra("toViewUser", user);
                        startActivity(intent);
                    }
                });
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(view.getContext(), SignupActivity.class));
            }
        });

        viewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), HistoryActivity.class));
            }
        });

        newBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), BookingActivity.class));
            }
        });

        viewBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MyBookingActivity.class));
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
