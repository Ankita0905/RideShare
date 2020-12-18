package com.ankita.rideshare.bean;

import com.google.firebase.auth.FirebaseUser;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RequestUser implements Serializable {
    @SerializedName("name")
    public String name;
    @SerializedName("id")
    public String id;
    @SerializedName("email")
    public String email;

    public RequestUser() {
    }

    public RequestUser(FirebaseUser user) {
        this.name = user.getDisplayName();
        this.id = user.getUid();
        this.email = user.getEmail();
    }
}
