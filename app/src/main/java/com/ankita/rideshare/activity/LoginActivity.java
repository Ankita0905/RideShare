package com.ankita.rideshare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ankita.rideshare.R;
import com.ankita.rideshare.bean.UserBean;
import com.ankita.rideshare.firebasePaths.FirebasePath;
import com.ankita.rideshare.others.Toaster;
import com.ankita.rideshare.others.Util;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    private TextView tvForgetPassword, tvSignup;
    private Button btnLogin;


    private TextInputLayout tilEmail, tilPassword;
    private EditText etEmail, etPassword;
    private UserBean bean = new UserBean();
    private static final String TAG = "LoginActivity";
    private final int RC_SIGN_IN = 1440;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvForgetPassword = findViewById(R.id.tvForgetPassword);
        tvSignup = findViewById(R.id.tvSignup);
        tvForgetPassword.setOnClickListener(v -> {
            ForgetPassword.start(LoginActivity.this);
        });
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            if (validatFields()) {
                firebaseLogin();
            }

        });

        findViewById(R.id.rlGoogleLogin).setOnClickListener(v -> {
            processForGoogleLogin();
        });
        tvSignup = findViewById(R.id.tvSignup);
        tvSignup.setOnClickListener(v -> {
            SignupActivity.start(LoginActivity.this);
        });

        tvForgetPassword = findViewById(R.id.tvForgetPassword);
        tvSignup = findViewById(R.id.tvSignup);

        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebasePath.get().getAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toaster.shortToast("Google Login Successful...");
                        HomeActivity.start(LoginActivity.this);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toaster.shortToast("Authentication Failed.");
                    }
                    Log.d(TAG, "firebaseAuthWithGoogle: " + new Gson().toJson(task));
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void processForGoogleLogin() {
        // Configure Google Sign In
        // Client id
        // 688293625149-ce6ep4t8ftti2cei1u3g787qq4oqbkdo.apps.googleusercontent.com
        //Client secret
        // kHS9wE1rzvPGm1Eqh9v0R0_q
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("688293625149-ce6ep4t8ftti2cei1u3g787qq4oqbkdo.apps.googleusercontent.com")
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        try {
            mGoogleSignInClient.signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }


    private boolean validatFields() {
        boolean isValid = true;
        bean.email = etEmail.getText().toString();
        bean.pswd = etPassword.getText().toString();

        if (bean.isValidEmail()) {
            isValid = false;
            Util.setError(tilEmail, "Please enter valid email...");
        } else if (bean.isValidPswd()) {
            isValid = false;
            Util.removeError(tilEmail);
            Util.setError(tilPassword, "Please enter valid password...");
        }

        return isValid;
    }


    private void firebaseLogin() {
        FirebasePath.get().getAuth().signInWithEmailAndPassword(bean.email, bean.pswd)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        Toaster.shortToast("Login Successfully...");
                        HomeActivity.start(LoginActivity.this);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toaster.shortToast("Authentication failed.");
                    }
                });
    }
}
