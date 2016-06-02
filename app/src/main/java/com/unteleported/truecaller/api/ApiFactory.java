package com.unteleported.truecaller.api;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.app.App;
import com.unteleported.truecaller.utils.Toaster;

import retrofit.RestAdapter;

/**
 * Created by stasenkopavel on 4/29/16.
 */
public class ApiFactory {

    public static ApiInterface createRetrofitService() {
        final RestAdapter retrofit = new RestAdapter.Builder().setEndpoint(ApiInterface.SERVICE_ENDPOINT).build();
        retrofit.setLogLevel(RestAdapter.LogLevel.FULL);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        return apiInterface;
    }

    public static void checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (!(netInfo != null && netInfo.isConnectedOrConnecting())) {
            Toaster.toast(App.getContext(), R.string.checkConnection);
        }
        else {
            Toaster.toast(App.getContext(), R.string.serverError);
        }
    }
}
