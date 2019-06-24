package com.orbital19.imabip.works;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.orbital19.imabip.models.Event;
import com.orbital19.imabip.models.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class FilteringDataWorker extends Worker {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    HashMap<String, Integer> monthVal = Event.setMonthsMap();
    Calendar calendar = Calendar.getInstance();
    private static final String TAG = "FilteringDataWorker";

    public FilteringDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        checkHostList(user.getEmail());
        checkEnrolledList(user.getEmail());
        checkGeneralCollection();

        return ListenableWorker.Result.success();
    }

    private void checkHostList(String email) {
        db.collection(User.usersCollection).document(email)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot userDoc = task.getResult();
                @NonNull ArrayList<String> hosting = (ArrayList<String>) userDoc.get(User.hostingKey);
                for (final String game : hosting) {
                    db.collection(Event.availableEventCollection).document(game)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot ev = task.getResult();
                            String time = (String) ev.get(Event.evTimeKey);
                            if (!gameYetToCome(time))
                                db.collection(Event.availableEventCollection).document(game)
                                    .delete();
                        }
                    });
                }
            }
        });
    }

    private void checkEnrolledList(String email) {
        db.collection(User.usersCollection).document(email)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot userDoc = task.getResult();
                @NonNull ArrayList<String> hosting = (ArrayList<String>) userDoc.get(User.enrolledKey);
                for (final String game : hosting) {
                    db.collection(Event.availableEventCollection).document(game)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot ev = task.getResult();
                            String time = (String) ev.get(Event.evTimeKey);
                            if (!gameYetToCome(time))
                                db.collection(Event.availableEventCollection).document(game)
                                        .delete();
                        }
                    });
                }
            }
        });
    }

    private void checkGeneralCollection() {
        db.collection(Event.availableEventCollection)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> docs = task.getResult().getDocuments();
                for (DocumentSnapshot doc : docs) {
                    if (!gameYetToCome((String) doc.get(Event.evTimeKey)))
                        db.collection(Event.availableEventCollection)
                                .document((String) doc.get(Event.idKey)).delete();
                }
            }
        });
    }

    private boolean gameYetToCome(String time) {
        Integer month, date, am_pm;
        Integer mMonth, mDate, mAM_PM;
        month = calendar.get(Calendar.MONTH) + 1;
        mMonth = monthVal.get(time.substring(0, 3));

        if (month.compareTo(mMonth) > 0) // month > mMonth
            return false;

        if (month.compareTo(mMonth) < 0) // month < mMonth
            return true;
        else { // same month
            date = calendar.get(Calendar.DATE);
            mDate = Integer.parseInt(time.substring(4, 6));
            if (date.compareTo(mDate) > 0) // date > mDate
                return false;

            if (date.compareTo(mDate) < 0) // date < mDate
                return true;
            else { // same date
                am_pm = calendar.get(Calendar.AM_PM);
                mAM_PM = time.substring(15).equals("AM") ? 0 : 1;
                if (mAM_PM > am_pm)
                    return true;
                else if (mAM_PM < am_pm)
                    return false;
                else {
                    String hrs = "" + calendar.get(Calendar.HOUR) + "." + calendar.get(Calendar.MINUTE);
                    String mHrs = time.substring(10, 15);
                    if (hrs.compareTo(mHrs) >= 0)
                        return false;
                    else return true;
                }
            }
        }
    }
}
