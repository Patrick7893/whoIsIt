package com.whois.whoiswho.api;

import com.google.gson.annotations.SerializedName;
import com.whois.whoiswho.model.User;

public class RegistrationResponse {
    @SerializedName("avatarPath")
    private String avatarPath;
    @SerializedName("data")
    private User data;
    @SerializedName("error")
    private int error;
    @SerializedName("id")
    private int id;
    @SerializedName("sms")
    private int sms;
    @SerializedName("token")
    private String token;

    public int getError() {
        return this.error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getData() {
        return this.data;
    }

    public void setData(User data) {
        this.data = data;
    }

    public int getSms() {
        return this.sms;
    }

    public void setSms(int sms) {
        this.sms = sms;
    }

    public String getAvatarPath() {
        return this.avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }
}
