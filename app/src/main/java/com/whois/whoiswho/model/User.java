package com.whois.whoiswho.model;


import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.File;

/**
 * Created by stasenkopavel on 4/26/16.
 */
@Table(database = Database.class)
public class User extends BaseModel {

    @Column
    @PrimaryKey
    @SerializedName("id")
    private int serverId;
    @Column
    private String firstname;
    @Column
    private String surname;
    @Column
    private String number;
    @Column
    private String email;
    @Column
    private String countyIso;
    @Column
    @SerializedName("avatar_url")
    private String avatarPath;
    @Column
    private String token;

    private File avatarFile;

    public User() {

    }

    public User(int serverId, String firstName, String surname, String phone, String email, File avatar) {
        this.serverId = serverId;
        this.firstname = firstName;
        this.surname = surname;
        this.number = phone;
        this.email = email;
        this.avatarFile = avatar;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String name) {
        this.firstname = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }


    public String getCountyIso() {
        return countyIso;
    }

    public void setCountyIso(String countyIso) {
        this.countyIso = countyIso;
    }

    public File getAvatarFile() {
        return avatarFile;
    }

    public void setAvatarFile(File avatarFile) {
        this.avatarFile = avatarFile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
