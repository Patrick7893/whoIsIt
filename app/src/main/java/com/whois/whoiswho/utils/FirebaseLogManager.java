package com.whois.whoiswho.utils;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by stasenkopavel on 7/28/16.
 */
public class FirebaseLogManager {

    public static void sendLogToFirebase(FirebaseAnalytics firebaseAnalytics, String event, String key, String value) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, key);
        bundle.putString(FirebaseAnalytics.Param.VALUE, value);
        firebaseAnalytics.logEvent(event, bundle);
    }
}
