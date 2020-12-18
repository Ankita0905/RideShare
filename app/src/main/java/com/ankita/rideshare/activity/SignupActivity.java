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
import com.ankita.rideshare.others.Util;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignupActivity extends AppCompatActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, SignupActivity.class));
    }

    private TextInputLayout
            tilName,
            tilEmail,
            tilPassword,
            tilRePassword;

    private EditText
            etName,
            etEmail,
            etPassword,
            etRePassword;


    private UserBean bean = new UserBean();
    private static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_register);
        findViewById(R.id.btnSIgnup).setOnClickListener(v -> {
            if (validateSignUpFields()) {
                firebaseSignUp();
            }
        });

        findViewById(R.id.tvSignIn).setOnClickListener(v -> {
            onBackPressed();
        });
        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilRePassword = findViewById(R.id.tilRePassword);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etRePassword = findViewById(R.id.etRePassword);

    }

    private boolean validateSignUpFields() {
        boolean isValid = true;
        bean.name = etName.getText().toString().trim();
        bean.email = etEmail.getText().toString().trim();
        bean.pswd = etPassword.getText().toString();
        bean.rePswd = etRePassword.getText().toString();
        if (bean.isValidName()) {
            isValid = false;
            setError(tilName, "Please enter your name");
        } else if (bean.isValidEmail()) {
            removeError(tilName);
            isValid = false;
            setError(tilEmail, "Please enter valid email");
        } else if (bean.isValidPswd()) {
            removeError(tilEmail);
            isValid = false;
            setError(tilPassword, "Password should be min of 6 letter");
        } else if (bean.isValidRePswd()) {
            removeError(tilPassword);
            isValid = false;
            setError(tilRePassword, "Re-Password should be same as Password.");
        } else {
            removeError(tilName);
            removeError(tilEmail);
            removeError(tilPassword);
            removeError(tilRePassword);
        }

        return isValid;
    }

    private void setError(TextInputLayout til, String error) {
        Util.setError(til, error);
    }

    private void removeError(TextInputLayout til) {
        Util.removeError(til);
    }


    private void firebaseSignUp() {
        //MyProgressDialog.show("In Progress....");
        FirebasePath.get().getAuth().createUserWithEmailAndPassword(bean.email, bean.pswd)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = FirebasePath.get().getAuth().getCurrentUser();
                        if (user == null) {
                            onError();
                            return;
                        }
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(bean.name)
                                .build();

                        user.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toaster.longToast("User sign up successfully...");
                                HomeActivity.start(SignupActivity.this);
                            } else {
                                Toaster.shortToast("Something went wrong...");
                            }
                        });
                    } else {
                        onError();
                    }

                });
    }

    private void onError() {
        Toaster.shortToast("Authentication failed.");
    }
}
