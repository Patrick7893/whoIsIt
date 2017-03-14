package com.whois.whoiswho.api;

import com.whois.whoiswho.model.Phone;

public class GetRecordByNumberResponse {
    private Phone data;
    private int error;

    public int getError() {
        return this.error;
    }

    public Phone getData() {
        return this.data;
    }
}
