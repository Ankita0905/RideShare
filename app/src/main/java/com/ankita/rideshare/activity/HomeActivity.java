package com.ankita.rideshare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ankita.rideshare.R;
import com.ankita.rideshare.others.ToolbarHelper;

public class HomeActivity  extends AppCompatActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, HomeActivity.class));
        ((AppCompatActivity)context).finish();
    }

    private TextView tvFind, tvCreate;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ToolbarHelper.get().setTitle(HomeActivity.this, findViewById(R.id.rlToolbar), "Home", false);
        tvCreate = findViewById(R.id.tvCreate);
        tvCreate.setOnClickListener(v -> {
            CreateRideActivity.start(HomeActivity.this);
        });

        tvFind = findViewById(R.id.tvFind);
        tvFind.setOnClickListener(v -> {
            RideListActvitty.start(HomeActivity.this);
        });
    }
}
