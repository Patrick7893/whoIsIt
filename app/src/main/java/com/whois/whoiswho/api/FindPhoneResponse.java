package com.whois.whoiswho.api;

import com.whois.whoiswho.model.Phone;

import java.util.ArrayList;

/**
 * Created by stasenkopavel on 4/27/16.
 */
public class FindPhoneResponse {

    private int error;
    private ArrayList<Phone> data;

    public int getError() {
        return error;
    }

    public ArrayList<Phone> getData() {
        return data;
    }
    
}
