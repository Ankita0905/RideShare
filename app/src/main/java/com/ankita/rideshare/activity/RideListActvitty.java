package com.ankita.rideshare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ankita.rideshare.R;
import com.ankita.rideshare.adapter.RideAdapter;
import com.ankita.rideshare.bean.RideBean;
import com.ankita.rideshare.firebasePaths.FirebasePath;
import com.ankita.rideshare.others.Toaster;
import com.ankita.rideshare.others.ToolbarHelper;
import com.ankita.rideshare.others.Util;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class RideListActvitty extends AppCompatActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, RideListActvitty.class));
    }

    private RecyclerView rvRideList;
    private RideAdapter adapter;
    private static final String TAG = "RideListActvitty";

    private Gson gson = new GsonBuilder().setLenient().create();
    private ArrayList<RideBean> rides = new ArrayList();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ToolbarHelper.get().setTitle(RideListActvitty.this, findViewById(R.id.rlToolbar), "Rides");
        rvRideList = findViewById(R.id.rvRideList);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.visible(progressBar);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        adapter = new RideAdapter(rides);
        rvRideList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvRideList.setAdapter(adapter);

        HashMap<String, RideBean> map = new HashMap<>();
        rides.clear();
        FirebasePath.get().getRidesPath()
                .whereNotEqualTo("user_id", FirebasePath.get().getUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    Util.gone(progressBar);
                    if (task.isSuccessful()) {
                        if (task.getResult() == null || task.getResult().size()<=0){
                            Toaster.shortToast("No rides are available...");
                            return;
                        }
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            RideBean bean = gson.fromJson(gson.toJson(document.getData()), RideBean.class);
                            bean.id = document.getId();
                            map.put(document.getId(), bean);
                        }
                        rides.addAll(map.values());
                        Collections.sort(rides, (o1, o2) -> o1.dateLong > o2.dateLong ? 1 : -1);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toaster.shortToast("No rides are available...");
                    }
                });


    }
}
