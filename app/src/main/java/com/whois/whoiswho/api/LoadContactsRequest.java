package com.whois.whoiswho.api;

import com.whois.whoiswho.model.Phone;
import java.util.ArrayList;

public class LoadContactsRequest {
    private ArrayList<Phone> phones;
    private String token;

    public LoadContactsRequest(String token, ArrayList<Phone> phones) {
        this.token = token;
        this.phones = phones;
    }
}
