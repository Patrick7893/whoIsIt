package com.unteleported.truecaller.model;


import android.text.TextUtils;

import com.unteleported.truecaller.api.ApiInterface;

import java.util.ArrayList;


/**
 * Created by stasenkopavel on 4/1/16.
 */
public class Contact {

    private long id;
    private String name;
    private ArrayList<Phone> phones = new ArrayList<>();
    private String title;
    private String avatar;
    private boolean isLiked;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Phone> getPhones() {
        return phones;
    }

    public void setPhones(ArrayList<Phone> phones) {
        this.phones = phones;
    }

    public void addPhone(Phone phone) {
        phones.add(phone);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public Contact phoneToContact(Phone phone) {
        this.setName(phone.getName());
        ArrayList<Phone> phones = new ArrayList<>();
        phones.add(phone);
        this.id = phone.getServerId();
        this.setPhones(phones);
        if (!TextUtils.isEmpty(phone.getAvatar().getUrl())) {
            this.setAvatar(ApiInterface.SERVICE_ENDPOINT + phone.getAvatar().getUrl());
        }
        return this;
    }


}
