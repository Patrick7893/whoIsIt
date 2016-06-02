package com.unteleported.truecaller.api;

import com.unteleported.truecaller.model.Phone;

import java.util.ArrayList;

/**
 * Created by stasenkopavel on 6/1/16.
 */
public class GetSpammersResponse extends BaseResponse {

    private ArrayList<Phone> spammersOfUser;

    private ArrayList<Phone> globalSpammers;

    public ArrayList<Phone> getGlobalSpammers() {
        return globalSpammers;
    }

    public ArrayList<Phone> getSpammersOfUser() {
        return spammersOfUser;
    }
}
