package com.whois.whoiswho.api;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.whois.whoiswho.R;
import com.whois.whoiswho.app.App;
import com.whois.whoiswho.utils.Toaster;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit.Builder;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactory {
    private static ApiFactory instance;
    private ApiInterface apiInterface;

    public ApiFactory() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(Level.BODY);
        this.apiInterface = (ApiInterface) new Builder().baseUrl(ApiInterface.SERVICE_ENDPOINT).client(new OkHttpClient.Builder().addInterceptor(interceptor).readTimeout(100, TimeUnit.SECONDS).connectTimeout(100, TimeUnit.SECONDS).build()).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build().create(ApiInterface.class);
    }

    public static ApiFactory getInstance() {
        if (instance == null) {
            instance = new ApiFactory();
        }
        return instance;
    }

    public static void checkConnection() {
        NetworkInfo netInfo = ((ConnectivityManager) App.getContext().getSystemService("connectivity")).getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
            Toaster.toast(App.getContext(), (int) R.string.checkConnection);
        } else {
            Toaster.toast(App.getContext(), (int) R.string.serverError);
        }
    }

//    private com.squareup.okhttp.OkHttpClient getClient() {
//        com.squareup.okhttp.OkHttpClient client = new com.squareup.okhttp.OkHttpClient();
//        client.setConnectTimeout(5, TimeUnit.MINUTES);
//        client.setReadTimeout(5, TimeUnit.MINUTES);
//        return client;
//    }

    public ApiInterface getApiInterface() {
        return this.apiInterface;
    }
}
