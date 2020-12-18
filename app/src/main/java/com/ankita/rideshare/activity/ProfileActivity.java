package com.ankita.rideshare.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ankita.rideshare.R;
import com.ankita.rideshare.firebasePaths.FirebasePath;
import com.ankita.rideshare.others.ToolbarHelper;
import com.ankita.rideshare.others.Util;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileActivity extends AppCompatActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, ProfileActivity.class));
    }

    private TextView tvInitial, tvName, tvEmail, tvTotalRides;
    private long count = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        RelativeLayout toolbar = findViewById(R.id.rlToolbar);
        ToolbarHelper.get().setTitle(ProfileActivity.this, toolbar, "My Profile");


        tvInitial = findViewById(R.id.tvInitial);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvTotalRides = findViewById(R.id.tvTotalRides);

        findViewById(R.id.tvEditProfile).setOnClickListener(v -> {
            EditProfileActivity.start(ProfileActivity.this);
        });

        findViewById(R.id.tvNewRequests).setOnClickListener(v -> {
            RequestListActivity.start(ProfileActivity.this, count);
        });

        findViewById(R.id.tvLogout).setOnClickListener(v -> {
            showLogoutDialog();
        });

        getUserRideCount();
        setRideCount();

    }




    private void setRideCount(){
        Util.gone(tvTotalRides);
        if (count>0){
            Util.visible(tvTotalRides);
            tvTotalRides.setText("Total Rides : "+count);
        }
    }
    private void getUserRideCount() {
        FirebasePath.get().rideCounter().document(FirebasePath.get().getUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        if (doc != null && doc.contains("count")){
                            count = doc.getLong("count");
                        }
                    }
                    setRideCount();
                });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure, you want to Logout from Ride Share")
                .setPositiveButton("Yes", (dialog1, which) -> {
                    dialog1.dismiss();
                    FirebasePath.get().getAuth().signOut();
                    LoginActivity.start(ProfileActivity.this);
                    ProfileActivity.this.finishAffinity();
                })
                .setNegativeButton("No",(dialog1, which) -> {
                    dialog1.dismiss();
                })
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebasePath.get().getUser();
        String name = user.getDisplayName();
        String email = user.getEmail();

        tvInitial.setText(name.substring(0, 1).toUpperCase());
        tvName.setText(name);
        tvEmail.setText(email);

        getUserRideCount();
        setRideCount();
    }
}
