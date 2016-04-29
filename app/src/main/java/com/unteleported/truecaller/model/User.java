package com.unteleported.truecaller.model;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

/**
 * Created by stasenkopavel on 4/26/16.
 */
public class User extends SugarRecord {

    private String name;
    private String number;
    private String email;
    private int type;
    private String countryIso;

    public User() {

    }

    public User(String name, String phone, String email, String countryIso) {
        this.name = name;
        this.number = phone;
        this.email = email;
        this.countryIso = countryIso;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


}
