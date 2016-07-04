package com.whois.whoiswho.api;

import com.whois.whoiswho.model.Phone;

/**
 * Created by stasenkopavel on 6/15/16.
 */
public class GetRecordByNumberResponse {

    private int error;
    private Phone data;

    public int getError() {
        return error;
    }

    public Phone getData() {
        return data;
    }
}
