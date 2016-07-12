package com.whois.whoiswho.api;

import com.whois.whoiswho.model.Phone;

import java.util.ArrayList;

/**
 * Created by stasenkopavel on 6/1/16.
 */
public class GetSpammersResponse extends BaseResponse {

    private ArrayList<Phone> user_spammers;

    private ArrayList<Phone> global_spammers;

    public ArrayList<Phone> getGlobalSpammers() {
        return global_spammers;
    }

    public ArrayList<Phone> getUserSpammers() {
        return user_spammers;
    }
}
