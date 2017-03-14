package com.whois.whoiswho.api;

import com.whois.whoiswho.model.Phone;
import java.util.ArrayList;

public class FindPhoneResponse {
    private ArrayList<Phone> data;
    private int error;

    public int getError() {
        return this.error;
    }

    public ArrayList<Phone> getData() {
        return this.data;
    }
}
