package com.orbital19.imabip.teams;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.orbital19.imabip.R;

public class MyTeamsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private FloatingActionButton refreshBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_teams);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("TEAMS");

        tabLayout = findViewById(R.id.tabs_teams);

        displaySelectedScreen(new fragment_view_my_teams());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int ind = tab.getPosition();
                Fragment fragment = null;
                switch (ind) {
                    case 0:
                        fragment = new fragment_view_my_teams();
                        break;
                    case 1:
                        fragment = new fragment_browse_teams();
                        break;
                    case 2:
                        fragment = new fragment_create_team();
                        break;
                    default:
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

        refreshBtn = findViewById(R.id.refresh_teams_btn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = tabLayout.getSelectedTabPosition();
                Fragment fragment = null;
                switch (pos) {
                    case 0:
                        fragment = new fragment_view_my_teams();
                        break;
                    case 1:
                        fragment = new fragment_browse_teams();
                        break;
                    case 2:
                        fragment = new fragment_create_team();
                        break;
                    default:
                        break;
                }
                displaySelectedScreen(fragment);
            }
        });
    }

    private boolean displaySelectedScreen(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_teams, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
