package com.orbital19.imabip.fservices;

import android.app.Notification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.orbital19.imabip.NotificationsHelper;
import com.orbital19.imabip.models.User;

public class FCMforHosting extends FirebaseMessagingService {
    private String TAG = "FCMforHosting";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            String message = remoteMessage.getData().get("status");

            if (message.equals("New player")) {
                NotificationsHelper notificationsHelper = new NotificationsHelper(getApplicationContext());

                NotificationCompat.Builder notificationBuilder =
                        notificationsHelper.getNotificationParti(
                                NotificationsHelper.PARTICIPANT_JOIN,
                                "A new player" + NotificationsHelper.PARTICIPANT_JOIN_BODY
                        );

                notificationsHelper.notify(NotificationsHelper.PARTICIPANT_JOIN_ID, notificationBuilder);
            } else if (message.equals("Dropped player")) {
                NotificationsHelper notificationsHelper = new NotificationsHelper(getApplicationContext());

                NotificationCompat.Builder notificationBuilder =
                        notificationsHelper.getNotificationParti(
                                NotificationsHelper.PARTICIPANT_DROP,
                                "A player" + NotificationsHelper.PARTICIPANT_DROP_BODY
                        );

                notificationsHelper.notify(NotificationsHelper.PARTICIPANT_DROP_ID, notificationBuilder);
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseFirestore.getInstance().collection(User.usersCollection)
                .document(email).update("Token", token);
    }
}
