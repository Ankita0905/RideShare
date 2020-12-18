package com.ankita.rideshare.bean;

import android.text.TextUtils;
import android.util.Patterns;

import java.io.Serializable;

public class UserBean implements Serializable {
    public String name;
    public String email;
    public String pswd;
    public String rePswd;

    public boolean isValidEmail(){
        return !Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidName(){
        return TextUtils.isEmpty(name);
    }

    public boolean isValidPswd(){
        return TextUtils.isEmpty(pswd) || pswd.length() <= 5;
    }

    public boolean isValidRePswd(){
        return !TextUtils.equals(pswd, rePswd);
    }

}
