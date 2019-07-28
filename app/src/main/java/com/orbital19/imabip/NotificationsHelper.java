package com.orbital19.imabip;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

/** Helper class to manage notification channels, and create notifications. */
public class NotificationsHelper extends ContextWrapper {
    private NotificationManager notificationManager;
    public static final String PARTICIPANT_CHANNEL = "participant";
    public static final String PARTICIPANT_KEY = "New Participant";
    public static final String PARTICIPANT_JOIN = "New player";
    public static final String PARTICIPANT_JOIN_BODY = "A player has joined game ";
    public static final String PARTICIPANT_DROP = "Dropped player";
    public static final String PARTICIPANT_DROP_BODY = "A player has dropped out of game ";
    public static final int PARTICIPANT_JOIN_ID = 1100;
    public static final int PARTICIPANT_DROP_ID = 1101;

    public static final String STARTING_CHANNEL = "starting";
    public static final String STARTING_KEY = "Starting game";
    public static final String STARTING_TITLE = "Game upcoming";
    public static final String STARTING_BODY = " is starting soon!";
    public static final int STARTING_ID = 1200;
    public static final int STARTED_ID = 1201;

    public static final String GAME_REMOVED_CHANNEL = "game_removed";
    public static final String GAME_REMOVED_KEY = "Game removed";
    public static final String GAME_REMOVED_TITLE = "Game cancelled";
    public static final String GAME_REMOVED_BODY = " has been cancelled";
    public static final int GAME_REMOVED_ID = 1300;

    public static final String TEAM_CHANNEL = "team";
    public static final String TEAM_KEY = "Team update";
    public static final String TEAM_MEM_JOIN = "New team member";
    public static final String TEAM_MEM_JOIN_BODY = "A new member has joined team ";
    public static final String TEAM_MEM_DROP = "Dropped team member";
    public static final String TEAM_MEM_DROP_BODY = "A member has dropped out of team ";
    public static final String TEAM_GAMES = "Team Games";
    public static final String TEAM_GAMES_BODY = "Update on games of team ";
    public static final String TEAM_DISMISSED = "Team Dismissal";
    public static final String TEAM_DISMISSED_BODY = " has been dismissed :(";
    public static final int TEAM_MEM_JOIN_ID = 1400;
    public static final int TEAM_MEM_DROP_ID = 1401;
    public static final int TEAM_DISMISSED_ID = 1402;
    public static final int TEAM_GAMES_ID = 1403;

    /**
     * Registers notification channels, which can be used later by individual notifications.
     *
     * @param context The application context
     */
    public NotificationsHelper(Context context) {
        super(context);

        // participant notification channel
        // will notify when a player joins user's hosting game
        NotificationChannel participantChannel =
                new NotificationChannel(
                        PARTICIPANT_CHANNEL,
                        getString(R.string.notification_channel_participant),
                        NotificationManager.IMPORTANCE_HIGH);

        participantChannel.setLightColor(Color.GREEN);
        participantChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        participantChannel.enableVibration(true);

        getNotificationManager().createNotificationChannel(participantChannel);

        // starting game notification channel
        // will notify when a game is 1 hour or 30 minutes away from starting
        NotificationChannel startingChannel =
                new NotificationChannel(
                        STARTING_CHANNEL,
                        getString(R.string.notification_channel_starting),
                        NotificationManager.IMPORTANCE_HIGH);

        startingChannel.setLightColor(Color.BLUE);
        startingChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        startingChannel.enableVibration(true);

        getNotificationManager().createNotificationChannel(startingChannel);

        // game removed notification channel
        // will notify when a game is cancelled
        NotificationChannel gameRemovedChannel =
                new NotificationChannel(
                        GAME_REMOVED_CHANNEL,
                        getString(R.string.notification_channel_game_removed),
                        NotificationManager.IMPORTANCE_HIGH);

        gameRemovedChannel.setLightColor(Color.RED);
        gameRemovedChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        gameRemovedChannel.enableVibration(true);

        getNotificationManager().createNotificationChannel(gameRemovedChannel);

        // game removed notification channel
        // will notify when a game is cancelled
        NotificationChannel teamChannel =
                new NotificationChannel(
                        TEAM_CHANNEL,
                        getString(R.string.notification_channel_team),
                        NotificationManager.IMPORTANCE_HIGH);

        teamChannel.setLightColor(Color.RED);
        teamChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        teamChannel.enableVibration(true);

        getNotificationManager().createNotificationChannel(teamChannel);
    }

    /**
     * Get a Participant message notification
     *
     * <p>Provide the builder rather than the notification it's self as useful for making
     * notification changes.
     *
     * @param title Title for notification.
     * @param body Message for notification.
     * @return A Notification.Builder configured with the selected channel and details
     */
    public Notification.Builder getNotificationTeam(String title, String body) {
        return new Notification.Builder(getApplicationContext(), TEAM_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(getPendingIntent());
    }

    /**
     * Get a Participant message notification
     *
     * <p>Provide the builder rather than the notification it's self as useful for making
     * notification changes.
     *
     * @param title Title for notification.
     * @param body Message for notification.
     * @return A Notification.Builder configured with the selected channel and details
     */
    public Notification.Builder getNotificationGmRmved(String title, String body) {
        return new Notification.Builder(getApplicationContext(), GAME_REMOVED_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(getPendingIntent());
    }

    /**
     * Get a Participant message notification
     *
     * <p>Provide the builder rather than the notification it's self as useful for making
     * notification changes.
     *
     * @param title Title for notification.
     * @param body Message for notification.
     * @return A Notification.Builder configured with the selected channel and details
     */
    public Notification.Builder getNotificationParti(String title, String body) {
        return new Notification.Builder(getApplicationContext(), PARTICIPANT_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(getPendingIntent());
    }

    /**
     * Get a Starting message notification
     *
     * <p>Provide the builder rather than the notification it's self as useful for making
     * notification changes.
     *
     * @param title Title for notification.
     * @param body Message for notification.
     * @return A Notification.Builder configured with the selected channel and details
     */
    public Notification.Builder getNotificationStart(String title, String body) {
        return new Notification.Builder(getApplicationContext(), STARTING_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(getPendingIntent());
    }

    /**
     * Create a PendingIntent for opening up the MainActivity when the notification is pressed
     *
     * @return A PendingIntent that opens the MainActivity
     */
    private PendingIntent getPendingIntent() {
        Intent openMainIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(openMainIntent);
        return stackBuilder.getPendingIntent(1, PendingIntent.FLAG_ONE_SHOT);
    }

    /**
     * Send a notification.
     *
     * @param id The ID of the notification
     * @param notification The notification object
     */
    public void notify(int id, Notification.Builder notification) {
        getNotificationManager().notify(id, notification.build());
    }

    /**
     * Get the notification notificationManager.
     *
     * <p>Utility method as this helper works with it a lot.
     *
     * @return The system service notificationManager
     */
    private NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }
}
