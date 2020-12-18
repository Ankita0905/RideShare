package com.ankita.rideshare.others;

import android.text.TextUtils;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.List;

public class Util {

    public static void setError(TextInputLayout til, String error) {
        til.setErrorEnabled(true);
        til.setError(error);
    }

    public static boolean isEmptyList(List items){
        return null == items || items.size() <=0;
    }

    public static void removeError(TextInputLayout til) {
        til.setError(null);
        til.setErrorEnabled(false);
    }

    public static boolean isEmpty(String value){
        return TextUtils.isEmpty(value) || TextUtils.isEmpty(value.trim()) || TextUtils.equals(value, "null");
    }

    public static void gone(View... views){
        if (!isEmptyList(Arrays.asList(views))){
            for (View v: views){
                if (v.getVisibility()== View.VISIBLE){
                    v.setVisibility(View.GONE);
                }
            }
        }
    }

    public static void visible(View... views){
        if (!isEmptyList(Arrays.asList(views))){
            for (View v: views){
                if (v.getVisibility()== View.GONE){
                    v.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
