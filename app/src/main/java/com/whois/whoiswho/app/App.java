package com.whois.whoiswho.app;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;

/**
 * Created by stasenkopavel on 4/4/16.
 */
public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        App.context = super.getApplicationContext();
        Fabric.with(this, new Crashlytics());
        FlowManager.init(new FlowConfig.Builder(this).build());
        Picasso.Builder builder = new Picasso.Builder(this);
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
