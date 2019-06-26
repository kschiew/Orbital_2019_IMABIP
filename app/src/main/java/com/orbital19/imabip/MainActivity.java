package com.orbital19.imabip;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.models.User;
import com.orbital19.imabip.works.FilteringDataWorker;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private FloatingActionButton mAddEvent;
    private TabLayout tabLayout;
    private WorkManager workManager = WorkManager.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
        }

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle("HOME");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set refresh rate to 15mins
        PeriodicWorkRequest refresh = new PeriodicWorkRequest.Builder(FilteringDataWorker.class, 15, TimeUnit.MINUTES).build();
        workManager.enqueue(refresh);

        mAddEvent = findViewById(R.id.addEvent);

        mAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Host.class));
                onPause();
            }
        });

        displaySelectedScreen(new fragment_eventList());

        tabLayout = findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int ind = tab.getPosition();
                Fragment fragment = null;
                switch (ind) {
                    case 0:
                        fragment = new fragment_eventList();
                        break;
                    case 1:
                        fragment = new fragment_enrolled();
                        break;
                    case 2:
                        fragment = new fragment_hostingList();
                        break;
                    case 3:
                        fragment = new fragment_view_Account();
                        break;
                }
                displaySelectedScreen(fragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
        }
        OneTimeWorkRequest update = new OneTimeWorkRequest.Builder(FilteringDataWorker.class).build();
        workManager.enqueue(update);
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
        }
        OneTimeWorkRequest update = new OneTimeWorkRequest.Builder(FilteringDataWorker.class).build();
        workManager.enqueue(update);
    }

    private boolean displaySelectedScreen(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
