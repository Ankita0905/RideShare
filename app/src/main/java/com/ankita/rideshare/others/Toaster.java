package com.ankita.rideshare.others;

import android.widget.Toast;

import com.ankita.rideshare.App;

public class Toaster {

    public static void shortToast(String msg){
        Toast.makeText(App.getApp(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void longToast(String msg){
        Toast.makeText(App.getApp(), msg, Toast.LENGTH_LONG).show();
    }

    public static void somethingWrong(){
        Toast.makeText(App.getApp(), "Something went wrong...", Toast.LENGTH_SHORT).show();
    }
}
