package com.whois.whoiswho.model;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by stasenkopavel on 4/15/16.
 */
@Table(database = Database.class, cachingEnabled = true)
public class Phone extends BaseModel {

    @Column
    @SerializedName("id")
    private int serverId;
    @Column
    @PrimaryKey
    private String number;
    @Column
    private int typeOfNumber;

    @Column
    private String name;
    @Column
    private String countryIso;
    private String operator;
    private Avatar avatar;
    @Column
    @SerializedName("number_of_setted_spam")
    private int numberOfSettedSpam;
    @Column
    private boolean isBlocked;
    @Column
    private boolean isLiked;

    public Phone() {}


    public Phone(String phone, int type) {
        this.number = phone;
        this.typeOfNumber = type;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getNumber() {
        return number;
    }

    public int getTypeOfNumber() {
        return typeOfNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setTypeOfNumber(int typeOfNumber) {
        this.typeOfNumber = typeOfNumber;
    }

    public String getCountryIso() {
        return countryIso;
    }

    public void setCountryIso(String countryIso) {
        this.countryIso = countryIso;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public int getNumberOfSettedSpam() {
        return numberOfSettedSpam;
    }


    public boolean getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public class Avatar {
        String url;

        public String getUrl() {
            return url;
        }
    }

    public void setNumberOfSettedSpam(int numberOfSettedSpam) {
        this.numberOfSettedSpam = numberOfSettedSpam;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean liked) {
        isLiked = liked;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
