package com.whois.whoiswho.api;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.squareup.okhttp.OkHttpClient;
import com.whois.whoiswho.R;
import com.whois.whoiswho.app.App;
import com.whois.whoiswho.utils.Toaster;

import java.util.concurrent.TimeUnit;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;

/**
 * Created by stasenkopavel on 4/29/16.
 */
public class ApiFactory {

    private static ApiFactory instance;

    private ApiInterface apiInterface;

    public ApiFactory() {
        final RestAdapter retrofit = new RestAdapter.Builder().setClient(new OkClient(getClient())).setEndpoint(ApiInterface.SERVICE_ENDPOINT).build();
        retrofit.setLogLevel(RestAdapter.LogLevel.FULL);
        apiInterface = retrofit.create(ApiInterface.class);
    }

    public static ApiFactory getInstance() {
        if (instance == null)
            instance = new ApiFactory();
        return instance;
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

    private OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(5, TimeUnit.MINUTES);
        client.setReadTimeout(5, TimeUnit.MINUTES);
        return client;
    }

    public ApiInterface getApiInterface() {
        return apiInterface;
    }
}
