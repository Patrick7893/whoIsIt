package com.whois.whoiswho.api;

import com.whois.whoiswho.model.Phone;

import java.util.ArrayList;

/**
 * Created by stasenkopavel on 4/27/16.
 */
public class LoadContactsRequest {

    private String token;
    private ArrayList<Phone> phone;

    public LoadContactsRequest(String token, ArrayList<Phone> phone) {
        this.token = token;
        this.phone = phone;
    }


}
