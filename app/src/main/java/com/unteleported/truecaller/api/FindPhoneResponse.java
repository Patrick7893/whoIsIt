package com.unteleported.truecaller.api;

import com.unteleported.truecaller.model.Phone;

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

    public ArrayList<Phone> getPhone() {
        return data;
    }
}