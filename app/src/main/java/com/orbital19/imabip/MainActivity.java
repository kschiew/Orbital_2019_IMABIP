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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private FloatingActionButton mAddEvent;
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, Log_in.class));
            finish();
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Home screen");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAddEvent = findViewById(R.id.addEvent);

        mAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Host.class));
                onPause();
            }
        });

        tabLayout = findViewById(R.id.tabs);
        tabLayout.getTabAt(0);
//        int tab_ind = tabLayout.getSelectedTabPosition();
//        switch (tab_ind) {
//            default:
//                break;
//            case 0:
//                displaySelectedScreen(new fragment_eventList());
//                break;
//        }
        displaySelectedScreen(new fragment_eventList());

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
