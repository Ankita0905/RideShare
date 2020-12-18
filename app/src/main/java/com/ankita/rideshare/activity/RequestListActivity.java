package com.ankita.rideshare.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ankita.rideshare.R;
import com.ankita.rideshare.adapter.RequestAdapter;
import com.ankita.rideshare.bean.RequestUser;
import com.ankita.rideshare.bean.RideBean;
import com.ankita.rideshare.firebasePaths.FirebasePath;
import com.ankita.rideshare.others.AppCallBack;
import com.ankita.rideshare.others.Toaster;
import com.ankita.rideshare.others.ToolbarHelper;
import com.ankita.rideshare.others.Util;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestListActivity extends AppCompatActivity {

    private static final String EXTRA_COUNT = "extra.count";

    public static void start(Context context, long count) {
        context.startActivity(new Intent(context, RequestListActivity.class)
                .putExtra(EXTRA_COUNT, count));
    }

    private Gson gson = new GsonBuilder().setLenient().create();
    private ArrayList<RequestUser> rides = new ArrayList();
    private RecyclerView rvRequestList;
    private RequestAdapter adapter;

    private static final String TAG = "RequestListActivity";
    private TextView tvRiderName, tvRideDate, tvEstTime, tvEndPoint,
            tvStartPoint, tvSendRequest;
    private RelativeLayout rlItemView;
    private ProgressBar progressBar;
    private RideBean bean;
    private long count;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);
        ToolbarHelper.get().setTitle(RequestListActivity.this, findViewById(R.id.rlToolbar), "Request List");
        count = getIntent().getLongExtra(EXTRA_COUNT, 0);
        initView();
        //  no need of this button here
        tvSendRequest.setVisibility(View.GONE);

        adapter = new RequestAdapter(rides, listener);
        rvRequestList.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.gone(rlItemView);
        Util.visible(progressBar);
        getUserRides();
    }


    private void initView() {
        rlItemView = findViewById(R.id.rlItemView);
        progressBar = findViewById(R.id.progressBar);

        tvRiderName = findViewById(R.id.tvRiderName);
        tvRideDate = findViewById(R.id.tvRideDate);
        tvEstTime = findViewById(R.id.tvEstTime);
        tvEndPoint = findViewById(R.id.tvEndPoint);
        tvStartPoint = findViewById(R.id.tvStartPoint);
        tvSendRequest = findViewById(R.id.tvSendRequest);
        rvRequestList = findViewById(R.id.rvRequestList);
    }

    private void getUserRides() {
        HashMap<String, RequestUser> map = new HashMap<>();

        FirebasePath.get().getRidesPath()
                .whereEqualTo("user_id", FirebasePath.get().getUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    Util.gone(progressBar);
                    if (task.isSuccessful()) {

                        if (task.getResult() == null || task.getResult().isEmpty()) {
                            Toaster.shortToast("You haven't created any rides yet.");
                            return;
                        }
                        Util.visible(rlItemView);

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            bean = gson.fromJson(gson.toJson(document.getData()), RideBean.class);
                            bean.id = document.getId();
                            setupRideDetail(bean);
                            if (Util.isEmptyList(bean.requestUser)) {
                                Toaster.shortToast("You didn't get any request yet.");
                            } else {
                                rides.addAll(bean.requestUser);
                                adapter.notifyDataSetChanged();
                            }
                            break;
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void setupRideDetail(RideBean bean) {
        if (bean != null) {
            setText(tvRiderName, bean.name);
            setText(tvRideDate, bean.dateTime);
            setText(tvEstTime, "Est Time(hrs) : " + bean.estimateTime);
            setText(tvEndPoint, "To : " + bean.destPlace);
            setText(tvStartPoint, "From : " + bean.originPlace);
        }
    }

    private void setText(TextView tv, String value) {
        tv.setText(value);
    }

    private final RequestAdapter.OnItemClickListener listener = (position) -> {
        RequestUser user = rides.get(position);
        FirebasePath.deletePreviousRides(new AppCallBack<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if (aBoolean) {
                    updateRideCount();
                    Toaster.longToast("You accepted " + user.name + "'s request. " +
                            "Please contact on their email for further discussion.\nHappy Journey.");
                    sendEmail(user);
                } else {
                    Toaster.somethingWrong();
                }
            }
        });

    };

    private void updateRideCount() {
        Map<String, Object> map = new HashMap<>();
        count += 1;
        map.put("count", count);
        FirebasePath.get().rideCounter().document(FirebasePath.get().getUser().getUid())
                .set(map)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toaster.shortToast("There is an issue with updating rides count...");
                    }
                });
    }

    @SuppressLint("IntentReset")
    private void sendEmail(RequestUser user) {

        String[] TO = {"" + user.email};
        String msg = String.format("Dear %s\n\n" +
                        "I'm glad to tell you that your request for ride from\n%s to %s on %s\nis accepted\n\n\nThank you",
                user.name, bean.originPlace, bean.destPlace, bean.dateTime);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Ride Share Request Accept");
        emailIntent.putExtra(Intent.EXTRA_TEXT, msg);

        try {
            startActivityForResult(Intent.createChooser(emailIntent, "Send mail..."), 1001);
        } catch (android.content.ActivityNotFoundException ex) {
            Toaster.shortToast("There is no email client installed.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001){
            onBackPressed();
        }
    }
}
