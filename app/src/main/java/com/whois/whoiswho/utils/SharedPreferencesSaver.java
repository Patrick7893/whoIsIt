package com.whois.whoiswho.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.whois.whoiswho.app.App;

/**
 * Created by stasenkopavel on 4/6/16.
 */
public class SharedPreferencesSaver {

    private static SharedPreferencesSaver instance;


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor ed;

    private static final String PREFSNAME = "truecallerPrefs";

    private static final String COORDINATES = "COORDNATES";
    private static final String TUTORIAL = "TUTORIAL";
    private static final String TOKEN = "TOKEN";

    private SharedPreferencesSaver(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFSNAME, Context.MODE_PRIVATE);
        ed = sharedPreferences.edit();
    }

    public static SharedPreferencesSaver get() {
        if (instance == null){
            instance = new SharedPreferencesSaver(App.getContext());
        }
        return instance;
    }

    public void saveCallDialogPosition(int position) {
        ed.putInt(COORDINATES, position).commit();
    }

    public int getCallDialogPosition() {
        int position = sharedPreferences.getInt(COORDINATES, -1);
        return position;
    }

    public void saveTutorialDone() {
        ed.putBoolean(TUTORIAL, true).commit();
    }

    public boolean getTutorialDone() {
        boolean isDone = sharedPreferences.getBoolean(TUTORIAL, false);
        return isDone;
    }

    public void saveToken(String token) {
        ed.putString(TOKEN, token).commit();
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN, "");
    }





}
