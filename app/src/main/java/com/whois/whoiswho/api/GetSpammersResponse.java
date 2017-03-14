package com.whois.whoiswho.api;

import com.whois.whoiswho.model.Phone;
import java.util.ArrayList;

public class GetSpammersResponse extends BaseResponse {
    private ArrayList<Phone> global_spammers;
    private ArrayList<Phone> user_spammers;

    public ArrayList<Phone> getGlobalSpammers() {
        return this.global_spammers;
    }

    public ArrayList<Phone> getUserSpammers() {
        return this.user_spammers;
    }
}
