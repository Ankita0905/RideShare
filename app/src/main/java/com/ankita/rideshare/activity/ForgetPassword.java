package com.ankita.rideshare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ankita.rideshare.R;
import com.ankita.rideshare.bean.UserBean;
import com.ankita.rideshare.firebasePaths.FirebasePath;
import com.ankita.rideshare.others.Toaster;
import com.ankita.rideshare.others.ToolbarHelper;
import com.ankita.rideshare.others.Util;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

public class ForgetPassword extends AppCompatActivity {
    public static void start(Context context) {
        context.startActivity(new Intent(context, ForgetPassword.class));
    }

    private TextInputLayout tilEmail;
    private EditText etEmail;
    private static final String TAG = "ForgetPassword";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ToolbarHelper.get().setTitle(ForgetPassword.this, findViewById(R.id.rlToolbar), "Forget Password");
        tilEmail = findViewById(R.id.tilEmail);
        etEmail = findViewById(R.id.etEmail);
        findViewById(R.id.btnSend).setOnClickListener(v -> {
            UserBean bean = new UserBean();
            bean.email = etEmail.getText().toString();
            if (bean.isValidEmail()) {
                Util.setError(tilEmail, "Please fill valid email...");
                onBackPressed();
            } else {
                Util.removeError(tilEmail);
                sendPasswordEmail(bean.email);

            }
        });
    }

    private void sendPasswordEmail(String email) {
        FirebasePath.get().getAuth().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toaster.shortToast("Please check your email to reset password...");
                    }else{
                        Toaster.shortToast("Something went wrong");
                    }
                    Log.d(TAG, "sendPasswordEmail: "+new Gson().toJson(task));
                });
    }
}
