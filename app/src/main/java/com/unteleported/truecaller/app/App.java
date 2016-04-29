package com.unteleported.truecaller.app;

import android.app.Application;
import android.content.Context;

import com.orm.SugarContext;

/**
 * Created by stasenkopavel on 4/4/16.
 */
public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        App.context = super.getApplicationContext();
        SugarContext.init(this);
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}
