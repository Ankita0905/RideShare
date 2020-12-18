package com.ankita.rideshare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ankita.rideshare.R;
import com.ankita.rideshare.bean.UserBean;
import com.ankita.rideshare.firebasePaths.FirebasePath;
import com.ankita.rideshare.others.Toaster;
import com.ankita.rideshare.others.ToolbarHelper;
import com.ankita.rideshare.others.Util;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditProfileActivity extends AppCompatActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, EditProfileActivity.class));
    }

    private TextInputLayout
            tilName,
            tilEmail;

    private EditText
            etName,
            etEmail;
    private UserBean bean = new UserBean();
    private static final String TAG = "EditProfileActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        RelativeLayout toolbar = findViewById(R.id.rlToolbar);
        ToolbarHelper.get().setTitle(EditProfileActivity.this, toolbar, "Edit Profile");


        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);

        findViewById(R.id.btnUpdate).setOnClickListener(v->{
            if (validateSignUpFields()){
                updateProfile();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebasePath.get().getUser();
        String name = user.getDisplayName();
        String email = user.getEmail();

        etName.setText(name);
        etEmail.setText(email);
    }

    private void updateProfile() {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(bean.name)
                .build();

        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                Toaster.longToast("User sign up successfully...");
                onBackPressed();
            } else {
                Toaster.shortToast("Something went wrong...");
            }
        });
    }

    private boolean validateSignUpFields() {
        boolean isValid = true;
        bean.name = etName.getText().toString().trim();
        bean.email = etEmail.getText().toString().trim();
        if (bean.isValidName()) {
            isValid = false;
            setError(tilName, "Please enter your name");
        }

        return isValid;
    }

    private void setError(TextInputLayout til, String error) {
        Util.setError(til, error);
    }


}
