package com.ankita.rideshare.others;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ankita.rideshare.R;
import com.ankita.rideshare.activity.ProfileActivity;

public class ToolbarHelper {
    private static ToolbarHelper singleton;

    private ToolbarHelper() {
    }

    public static ToolbarHelper get() {
        if (singleton == null) {
            singleton = new ToolbarHelper();
        }
        return singleton;
    }


    public void setTitle(Context context, View view, String title, boolean hideHamburg) {
        ((TextView) view.findViewById(R.id.tvToolbarText)).setText(title);
        ((ImageView) view.findViewById(R.id.ivToolbarBack)).setOnClickListener(v -> {
            ((AppCompatActivity) context).onBackPressed();
        });

        ImageView image = view.findViewById(R.id.ivHamburg);
        image.setOnClickListener(v -> ProfileActivity.start(context));
        image.setVisibility(hideHamburg ? View.GONE : View.VISIBLE);

    }

    public void setTitle(Context context, View view, String title) {
        setTitle(context, view, title, true);
    }
}
