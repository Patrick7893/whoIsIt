package com.whois.whoiswho.api;

import com.google.gson.annotations.SerializedName;
import com.whois.whoiswho.model.User;

/**
 * Created by stasenkopavel on 4/26/16.
 */
public class RegistrationResponse {

    @SerializedName("error")
    private int error;
    @SerializedName("token")
    private String token;
    @SerializedName("id")
    private int id;
    @SerializedName("data")
    private User data;
    @SerializedName("sms")
    private int sms;
    @SerializedName("avatarPath")
    private String avatarPath;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    public int getSms() {
        return sms;
    }

    public void setSms(int sms) {
        this.sms = sms;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }
}
