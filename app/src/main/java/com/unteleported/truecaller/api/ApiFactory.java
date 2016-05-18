package com.unteleported.truecaller.api;


import retrofit.RestAdapter;

/**
 * Created by stasenkopavel on 4/29/16.
 */
public class ApiFactory {

    public static ApiInterface createRetrofitService() {
        final RestAdapter retrofit = new RestAdapter.Builder().setEndpoint(ApiInterface.SERVICE_ENDPOINT).build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        return apiInterface;
    }
}
