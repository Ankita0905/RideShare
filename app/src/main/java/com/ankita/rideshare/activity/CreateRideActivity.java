package com.ankita.rideshare.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ankita.rideshare.R;
import com.ankita.rideshare.bean.RequestUser;
import com.ankita.rideshare.bean.RideBean;
import com.ankita.rideshare.firebasePaths.FirebasePath;
import com.ankita.rideshare.others.AppCallBack;
import com.ankita.rideshare.others.Toaster;
import com.ankita.rideshare.others.ToolbarHelper;
import com.ankita.rideshare.others.Util;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateRideActivity extends AppCompatActivity {
    public static void start(Context context) {
        context.startActivity(new Intent(context, CreateRideActivity.class));
    }

    private Calendar current = Calendar.getInstance();
    private EditText etDateTime;
    private TextInputLayout tilOriginPlace, tilDestPlace,
            tilDateTime, tilEstimatedTime,
            tilCarBrand, tilCarModel, tilCarNumber, tilPhoneNumber, tilIns;

    private EditText etOriginPlace, etDestPlace,
            etEstimatedTime, etCarBrand,
            etCarModel, etCarNumber, etPhoneNumber, etIns;

    private Button btnSignup;
    private long selectedTime = 0;
    private boolean isValid;
    private RideBean bean = new RideBean();

    private Gson gson = new Gson();
    private static final String TAG = "CreateRideActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_create_ride);
        ToolbarHelper.get().setTitle(CreateRideActivity.this, findViewById(R.id.rlToolbar), "Create Ride");

        checkIfUserHasAlreadyRidesInProgress();
        initViews();
        initListener();
        initObjects();

    }

    private void checkIfUserHasAlreadyRidesInProgress() {
        FirebasePath.get().getRidesPath()
                .whereEqualTo("user_id", FirebasePath.get().getUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()
                            && task.getResult() != null
                            && task.getResult().size() > 0) {
                        showDialogToDeletePreviousRides();
                    }
                });
    }

    private void showDialogToDeletePreviousRides() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setCancelable(false)
                .setMessage("You can't create multiple rides. Do you wanna delete previous ride?")
                .setPositiveButton("Yes", (dialog1, which) -> {
                    dialog1.dismiss();
                    FirebasePath.deletePreviousRides(new AppCallBack<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {

                        }
                    });
                })
                .setNegativeButton("No",(dialog1, which) -> {
                    dialog1.dismiss();
                    onBackPressed();
                })
                .show();
    }



    private void initObjects() {

    }

    private void initListener() {
        etDateTime.setOnClickListener(v -> {
            openDateTimePicker();
        });
        btnSignup.setOnClickListener(v -> {
            if (validateFields()){
                addRideToFirebase();
            }
        });
    }

    private void addRideToFirebase() {
        try {
            RequestUser user = new RequestUser(FirebasePath.get().getUser());
            bean.userId = user.id;
            bean.name = user.name;
            bean.email = user.email;
            Map<String, Object> map = gson.fromJson(gson.toJson(bean), new TypeToken<HashMap<String, Object>>() {}.getType());
            FirebasePath.get().getRidesPath().add(map).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toaster.shortToast("Your ride added successfully...");
                    onBackPressed();
                }else{
                    Toaster.somethingWrong();
                }
            });
        } catch (Exception e) {
            Log.d("TAG", "addRideToFirebase: "+e.toString());
        }

    }

    private boolean validateFields() {
        isValid = true;

        bean.originPlace = getValue(etOriginPlace);
        bean.destPlace = getValue(etDestPlace);
        bean.phoneNumber = getValue(etPhoneNumber);
        bean.anyInstructions = getValue(etIns);
        bean.dateTime = getValue(etDateTime);
        bean.estimateTime = getValue(etEstimatedTime);
        bean.carBrand = getValue(etCarBrand);
        bean.carModel = getValue(etCarModel);
        bean.carNumber = getValue(etCarNumber);
        bean.dateLong = selectedTime;
       /* bean.phoneNumber = "7845896545";
        bean.originPlace = "Origin place";
        bean.destPlace = "Dest place";
        bean.dateTime = "07:30 AM | 14 May";
        bean.dateLong = selectedTime;
        bean.estimateTime = "10";
        bean.carBrand = "TATA";
        bean.carModel = "INDICA";
        bean.carNumber = "PB08-08-7456";
        bean.anyInstructions = "Small bags";*/
        if (Util.isEmpty(bean.phoneNumber)){
            setError(tilPhoneNumber, "Please fill Phone Number");
        } else if (Util.isEmpty(bean.originPlace)) {
            setError(tilOriginPlace, "Please fill Origin Place");
        }else if (Util.isEmpty(bean.destPlace)){
            setError(tilDestPlace, "Please fill Destination Place");
        }else if (Util.isEmpty(bean.dateTime)){
            setError(tilDateTime, "Please fill Date & Time");
        }else if (Util.isEmpty(bean.estimateTime)){
            setError(tilEstimatedTime, "Please fill Estimated Time");
        }else if (Util.isEmpty(bean.carBrand)){
            setError(tilCarBrand, "Please fill Car Brand");
        }else if (Util.isEmpty(bean.carModel)){
            setError(tilCarModel, "Please fill Car Model");
        }else if (Util.isEmpty(bean.carNumber)){
            setError(tilCarNumber, "Please fill Car Number");
        }

        return isValid;
    }

    private void setError(TextInputLayout til, String error) {
        isValid = false;
        Util.setError(til, error);
    }

    private void removeError(TextInputLayout til) {
        Util.removeError(til);
    }


    private String getValue(EditText et) {
        return et.getText().toString().trim();
    }

    private void initViews() {

        tilPhoneNumber = findViewById(R.id.tilPhoneNumber);
        tilOriginPlace = findViewById(R.id.tilOriginPlace);
        tilDestPlace = findViewById(R.id.tilDestPlace);
        tilDateTime = findViewById(R.id.tilDateTime);
        tilEstimatedTime = findViewById(R.id.tilEstimatedTime);
        tilCarBrand = findViewById(R.id.tilCarBrand);
        tilCarModel = findViewById(R.id.tilCarModel);
        tilCarNumber = findViewById(R.id.tilCarNumber);
        tilIns = findViewById(R.id.tilIns);

        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etOriginPlace = findViewById(R.id.etOriginPlace);
        etDestPlace = findViewById(R.id.etDestPlace);
        etDateTime = findViewById(R.id.etDateTime);
        etEstimatedTime = findViewById(R.id.etEstimatedTime);
        etCarBrand = findViewById(R.id.etCarBrand);
        etCarModel = findViewById(R.id.etCarModel);
        etCarNumber = findViewById(R.id.etCarNumber);
        etIns = findViewById(R.id.etIns);

        btnSignup = findViewById(R.id.btnSignup);

    }

    private void openDateTimePicker() {
        // Get Current Date
        int mYear = current.get(Calendar.YEAR);
        int mMonth = current.get(Calendar.MONTH);
        int mDay = current.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DatePickerTheme, (view, year, monthOfYear, dayOfMonth) -> {
            current.set(Calendar.YEAR, year);
            current.set(Calendar.MONTH, monthOfYear);
            current.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            selectTime();
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void selectTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.DatePickerTheme, (view, hourOfDay, minute) -> {
            current.set(Calendar.HOUR_OF_DAY, hourOfDay);
            current.set(Calendar.MINUTE, minute);
            setDateTime();
        }, current.get(Calendar.HOUR_OF_DAY), current.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    private void setDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a '|' dd MMM", Locale.getDefault());
        etDateTime.setText(sdf.format(current.getTime()).toUpperCase());
        selectedTime= current.getTime().getTime();
    }
}
