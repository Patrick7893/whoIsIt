package com.unteleported.truecaller.api;

import com.unteleported.truecaller.model.Phone;

import java.util.ArrayList;

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
