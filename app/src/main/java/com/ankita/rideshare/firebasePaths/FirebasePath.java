package com.ankita.rideshare.firebasePaths;

import android.util.Log;

import com.ankita.rideshare.others.Toaster;
import com.ankita.rideshare.others.AppCallBack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FirebasePath {
    private static final String TAG = "FirebasePath";

    private static FirebasePath path;


    private FirebasePath() { }

    public synchronized static FirebasePath get() {
        if (path == null) {
            path = new FirebasePath();
        }
        return path;
    }

    public FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }



    public CollectionReference getRidesPath() {
        return FirebaseFirestore.getInstance().collection("rides");
    }

    public CollectionReference rideCounter() {
        return FirebaseFirestore.getInstance().collection("user_rides_count");
    }

    public FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static void deletePreviousRides(AppCallBack<Boolean> callBack) {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference itemsRef = rootRef.collection("rides");
        Query query = itemsRef.whereEqualTo("user_id", FirebasePath.get().getUser().getUid());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    itemsRef.document(document.getId()).delete();
                }
                callBack.onSuccess(true);
                Toaster.shortToast("Ride deleted successfully....");
            } else {
                callBack.onSuccess(false);
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }
}