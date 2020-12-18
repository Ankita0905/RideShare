package com.ankita.rideshare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.ankita.rideshare.R;
import com.ankita.rideshare.bean.RequestUser;
import com.ankita.rideshare.bean.RideBean;
import com.ankita.rideshare.firebasePaths.FirebasePath;
import com.ankita.rideshare.others.Toaster;
import com.ankita.rideshare.others.ToolbarHelper;
import com.ankita.rideshare.others.Util;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RideDetailActivity extends AppCompatActivity {

    private static final String EXTRA_DATA = "extra.data";

    public static void start(Context context, RideBean bean) {
        context.startActivity(new Intent(context, RideDetailActivity.class)
                .putExtra(EXTRA_DATA, bean));
    }

    private RideBean bean;
    private static final String TAG = "RideDetailActivity";
    private Button btnSendRequest;
    private RequestUser user = new RequestUser(FirebasePath.get().getUser());
    private TextView tvRiderName, tvRideDate, tvEstTime, tvEndPoint,
            tvStartPoint, tvSendRequest, tvCarBrand,
            tvCarModel, tvCarNumber, tvInstructions, tvContact;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_detail);

        ToolbarHelper.get().setTitle(RideDetailActivity.this, findViewById(R.id.rlToolbar), "Ride Detail");

        bean = (RideBean) getIntent().getSerializableExtra(EXTRA_DATA);
        initViews();

        //  no need of this button here
        tvSendRequest.setVisibility(View.GONE);

        setUpData();

        setUpRequestButton();
        btnSendRequest.setOnClickListener(v -> {
            serviceToSendRequest();
        });
    }

    private void initViews() {
        tvRiderName = findViewById(R.id.tvRiderName);
        tvRideDate = findViewById(R.id.tvRideDate);
        tvEstTime = findViewById(R.id.tvEstTime);
        tvEndPoint = findViewById(R.id.tvEndPoint);
        tvStartPoint = findViewById(R.id.tvStartPoint);
        tvSendRequest = findViewById(R.id.tvSendRequest);

        tvCarBrand = findViewById(R.id.tvCarBrand);
        tvCarModel = findViewById(R.id.tvCarModel);
        tvCarNumber = findViewById(R.id.tvCarNumber);
        tvInstructions = findViewById(R.id.tvInstructions);
        tvContact = findViewById(R.id.tvContact);

        btnSendRequest = findViewById(R.id.btnSendRequest);
    }

    private void setUpData() {
        setText(tvRiderName, bean.name);
        setText(tvRideDate, bean.dateTime);
        setText(tvEstTime, "Est Time(hrs) : " + bean.estimateTime);
        setText(tvEndPoint, "To : " + bean.destPlace);
        setText(tvStartPoint, "From : " + bean.originPlace);

        setHtmlText(tvCarBrand, "Car Brand", bean.carBrand);
        setHtmlText(tvCarModel, "Car Model", bean.carModel);
        setHtmlText(tvCarNumber, "Car Number", bean.carNumber);
        setHtmlText(tvInstructions, "Instructions", Util.isEmpty(bean.anyInstructions)
                ? "-No Instructions-"
                : bean.anyInstructions);

        tvContact.setText(String.format("You can contact me on %s for further inquires or discussion", bean.phoneNumber));


    }

    private void setText(TextView tv, String value) {
        tv.setText(value);
    }

    private void setHtmlText(TextView tv, String title, String value) {
        String htmlText = String.format("<b>%s</b><br/>%s", title, value);
        tv.setText(HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_COMPACT));
    }

    private void setUpRequestButton() {
        boolean isRequestSent = isRequestSent();
        btnSendRequest.setText(isRequestSent ? " Request  Sent" : "Send Request");
        btnSendRequest.setEnabled(!isRequestSent);
    }

    private boolean isRequestSent() {
        return !Util.isEmptyList(bean.requestIds) && bean.requestIds.contains(user.id);
    }

    private void serviceToSendRequest() {
        List<RequestUser> users = new ArrayList<>();
        if (!Util.isEmptyList(bean.requestUser)) {
            users.addAll(bean.requestUser);
        }
        users.add(user);

        Set<String> ids = new HashSet<>();
        if (!Util.isEmptyList(bean.requestIds)) {
            ids.addAll(bean.requestIds);
        }
        ids.add(user.id);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        DocumentReference db_rides = db.collection("rides").document(bean.id);

        batch.update(db_rides, "request_users", users);
        batch.update(db_rides, "request_ids", new ArrayList<>(ids));

        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                bean.requestIds = new ArrayList<>(ids);
                bean.requestUser = users;
                setUpRequestButton();
                Toaster.shortToast("Request Send...");
            } else {
                Log.d(TAG, "serviceToSendRequest: " + new Gson().toJson(task));
                Toaster.somethingWrong();
            }
        });
    }
}
