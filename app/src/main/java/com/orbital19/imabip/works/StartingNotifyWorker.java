package com.orbital19.imabip.works;

import android.app.Notification;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.NotificationsHelper;
import com.orbital19.imabip.R;
import com.orbital19.imabip.models.Event;

/*
    Helper class to deal with making notifications for upcoming games

    Input data will be the name of the game
 */
public class StartingNotifyWorker extends Worker {
    private String gameID;

    public StartingNotifyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        gameID = getInputData().getString(NotificationsHelper.STARTING_KEY);
        triggerNotification(gameID);

        return Result.success();
    }

    private void triggerNotification(String gameID) {
        FirebaseFirestore.getInstance().collection(Event.availableEventCollection)
                .document(gameID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    String name = (String) doc.get(Event.nameKey);
                    NotificationsHelper notificationsHelper = new NotificationsHelper(getApplicationContext());

                    NotificationCompat.Builder notificationBuilder =
                            notificationsHelper.getNotificationStart(
                                    NotificationsHelper.STARTING_TITLE,
                                    name + NotificationsHelper.STARTING_BODY);

                    notificationsHelper.notify(NotificationsHelper.STARTING_ID, notificationBuilder);
                }
            }
        });
    }


}
