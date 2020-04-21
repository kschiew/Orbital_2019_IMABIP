package com.orbital19.imabip;

import android.app.Notification;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.models.User;
import com.orbital19.imabip.teams.models.Team;
import com.orbital19.imabip.works.FilteringDataWorker;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;


public class MainActivity extends AppCompatActivity {

    private FloatingActionButton mAddEvent, mRefreshEvent;
    private TabLayout tabLayout;
    private WorkManager workManager = WorkManager.getInstance();
    private DataManagement dataManagement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle("HOME");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
        } else {

            dataManagement = new DataManagement(user.getEmail());

            attachRealtimeListenersToGames(user.getEmail());

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {

                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();

                            FirebaseFirestore.getInstance().collection(User.usersCollection)
                                    .document(user.getEmail()).update("Token", token);

                        }
                    });

            // set refresh rate to 15mins
            PeriodicWorkRequest refresh = new PeriodicWorkRequest.Builder(FilteringDataWorker.class, 15, TimeUnit.MINUTES)
                    .addTag("Filter").build();
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

            // refresh on User's request
            mRefreshEvent = findViewById(R.id.refreshButt);

            mRefreshEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OneTimeWorkRequest update = new OneTimeWorkRequest.Builder(FilteringDataWorker.class).build();
                    workManager.cancelAllWorkByTag("Filter");
                    workManager.enqueue(update);
                    dataManagement.update();
                    attachRealtimeListenersToGames(user.getEmail());
                    Fragment frag = null;
                    switch (tabLayout.getSelectedTabPosition()) {
                        case 0:
                            frag = new fragment_eventList();
                            break;
                        case 1:
                            frag = new fragment_enrolled();
                            break;
                        case 2:
                            frag = new fragment_hostingList();
                            break;
                        case 3:
                            frag = new fragment_view_Account();
                            break;
                    }

                    displaySelectedScreen(frag);
                }
            });


        }

    }

    private void attachRealtimeListenersToGames(final String userEmail) {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        final ArrayList<String> enrolledList = new ArrayList<>();
        final ArrayList<String> hostingList = new ArrayList<>();
        final ArrayList<String> teamsList = new ArrayList<>();

        fs.collection(User.usersCollection).document(userEmail)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {
                    enrolledList.addAll((ArrayList<String>) doc.get(User.enrolledKey));
                    hostingList.addAll((ArrayList<String>) doc.get(User.hostingKey));
                    teamsList.addAll((ArrayList<String>) doc.get(User.joinedTeamsKey));

                    listen(enrolledList, userEmail, 1);
                    listen(hostingList, userEmail,2);
                    listen(teamsList, userEmail, 3);
                }
            }
        });


    }

    private void listen(ArrayList<String> lst, final String email, int type) {
        // type: 1 - enrolled, 2 - hosting, 3 - team

        switch (type) {
            case 1:
                for (final String item : lst) {
                    FirebaseFirestore.getInstance().collection(Event.availableEventCollection)
                            .document(item).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("Listener", "Failed. " + e);
                                return;
                            }

                            if (snapshot != null && snapshot.exists()) {
                                dataManagement.changedJoinedGame(item, snapshot, true);
                            } else {
                                String name = dataManagement.changedJoinedGame(item, snapshot, false);
                                startGameRemovedNoti(name);
                            }
                        }
                    });
                }
                break;
            case 2:
                for (final String item : lst) {
                    FirebaseFirestore.getInstance().collection(Event.availableEventCollection)
                            .document(item).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("Listener", "Failed. " + e);
                                return;
                            }

                            if (snapshot != null && snapshot.exists()) {
                                Log.d("Listener", "Updated data" + snapshot.getData());
                                switch(dataManagement.changedHostingGame(item, snapshot, true)) {
                                    case 0: // player left
                                        startPlayerLeftNoti((String) snapshot.get(Event.nameKey));
                                        break;
                                    case 1:
                                        startPlayerJoinedNoti((String) snapshot.get(Event.nameKey));
                                        break;
                                }
                            } else {
                                dataManagement.changedHostingGame(item, snapshot, false);
                            }
                        }
                    });
                }
                break;
            case 3: // team
                for (final String tm : lst) {
                    FirebaseFirestore.getInstance().collection(Team.teamsCollection)
                            .document(tm).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("Listener", "Failed. " + e);
                                return;
                            }

                            if (snapshot != null && snapshot.exists()) {
                                switch (dataManagement.changedTeam(tm, snapshot, true)) {
                                    case 0: //
                                        startTeamSizeUpNoti(tm);
                                        break;
                                    case 1:
                                        startTeamSizeDownNoti(tm);
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                dataManagement.changedTeam(tm, snapshot, false);
                                startTeamDismissedNoti(tm);
                            }
                        }
                    });
                }
                break;
        }
    }

    private void startTeamDismissedNoti(String tm) {
        NotificationsHelper notificationsHelper = new NotificationsHelper(getApplicationContext());

        Notification.Builder notificationBuilder =
                notificationsHelper.getNotificationTeam(
                        NotificationsHelper.TEAM_DISMISSED,
                        tm + NotificationsHelper.TEAM_DISMISSED_BODY );

        notificationsHelper.notify(NotificationsHelper.TEAM_DISMISSED_ID, notificationBuilder);
    }


    private void startTeamSizeDownNoti(String tm) {
        NotificationsHelper notificationsHelper = new NotificationsHelper(getApplicationContext());

        Notification.Builder notificationBuilder =
                notificationsHelper.getNotificationTeam(
                        NotificationsHelper.TEAM_MEM_DROP,
                        NotificationsHelper.TEAM_MEM_DROP_BODY + tm);

        notificationsHelper.notify(NotificationsHelper.TEAM_MEM_DROP_ID, notificationBuilder);
    }

    private void startTeamSizeUpNoti(String tm) {
        NotificationsHelper notificationsHelper = new NotificationsHelper(getApplicationContext());

        Notification.Builder notificationBuilder =
                notificationsHelper.getNotificationTeam(
                        NotificationsHelper.TEAM_MEM_JOIN,
                        NotificationsHelper.TEAM_MEM_JOIN_BODY + tm);

        notificationsHelper.notify(NotificationsHelper.TEAM_MEM_JOIN_ID, notificationBuilder);
    }

    private void startGameRemovedNoti(String name) {
        NotificationsHelper notificationsHelper = new NotificationsHelper(getApplicationContext());

        Notification.Builder notificationBuilder =
                notificationsHelper.getNotificationGmRmved(
                        NotificationsHelper.GAME_REMOVED_TITLE,
                        name + NotificationsHelper.GAME_REMOVED_BODY);

        notificationsHelper.notify(NotificationsHelper.GAME_REMOVED_ID, notificationBuilder);
    }

    private void startPlayerJoinedNoti(String name) {
        NotificationsHelper notificationsHelper = new NotificationsHelper(getApplicationContext());

        Notification.Builder notificationBuilder =
                notificationsHelper.getNotificationParti(
                        NotificationsHelper.PARTICIPANT_JOIN,
                        NotificationsHelper.PARTICIPANT_JOIN_BODY + name);

        notificationsHelper.notify(NotificationsHelper.PARTICIPANT_JOIN_ID, notificationBuilder);
    }

    private void startPlayerLeftNoti(String name) {
        NotificationsHelper notificationsHelper = new NotificationsHelper(getApplicationContext());

        Notification.Builder notificationBuilder =
                notificationsHelper.getNotificationParti(
                        NotificationsHelper.PARTICIPANT_DROP,
                        NotificationsHelper.PARTICIPANT_DROP_BODY + name);

        notificationsHelper.notify(NotificationsHelper.PARTICIPANT_DROP_ID, notificationBuilder);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
        }
        OneTimeWorkRequest update = new OneTimeWorkRequest.Builder(FilteringDataWorker.class).
                addTag("Filter").build();
        workManager.enqueue(update);

        dataManagement.update();
        attachRealtimeListenersToGames(user.getEmail());
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
        }
        OneTimeWorkRequest update = new OneTimeWorkRequest.Builder(FilteringDataWorker.class)
                .addTag("Filter").build();
        workManager.enqueue(update);

        dataManagement.update();
        attachRealtimeListenersToGames(user.getEmail());
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
