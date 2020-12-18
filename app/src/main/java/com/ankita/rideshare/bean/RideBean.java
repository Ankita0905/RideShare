package com.ankita.rideshare.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RideBean implements Serializable {
    @SerializedName("phone_number")
    public String phoneNumber;
    @SerializedName("ride_id")
    public String id;
    @SerializedName("origin_place")
    public String originPlace;
    @SerializedName("dest_place")
    public String destPlace;
    @SerializedName("date_time")
    public String dateTime;
    @SerializedName("dateLong")
    public long dateLong;
    @SerializedName("est_time")
    public String estimateTime;
    @SerializedName("car_brand")
    public String carBrand;
    @SerializedName("car_model")
    public String carModel;
    @SerializedName("car_number")
    public String carNumber;
    @SerializedName("any_inst")
    public String anyInstructions;
    @SerializedName("name")
    public String name;
    @SerializedName("user_id")
    public String userId;
    @SerializedName("email")
    public String email;

    @SerializedName("request_users")
    public List<RequestUser> requestUser;

    @SerializedName("request_ids")
    public List<String> requestIds;

    public RideBean() {
    }
}
