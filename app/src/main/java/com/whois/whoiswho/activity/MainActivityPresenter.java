package com.whois.whoiswho.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.whois.whoiswho.model.User;
import com.whois.whoiswho.screens.login.LoginFragment;
import com.whois.whoiswho.screens.mainscreen.TabFragment;
import com.whois.whoiswho.utils.SharedPreferencesSaver;

/**
 * Created by stasenkopavel on 4/28/16.
 */
public class MainActivityPresenter {

    private MainActivity view;

    public MainActivityPresenter(MainActivity view) {
        this.view = view;
    }

    public void requestPermissions() {
        try {
            if ((ContextCompat.checkSelfPermission(view, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)&&
                    (ContextCompat.checkSelfPermission(view, Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED)&&
                    (ContextCompat.checkSelfPermission(view, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)&&
                    (ContextCompat.checkSelfPermission(view, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(view, new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CALL_LOG, Manifest.permission.CALL_PHONE, Manifest.permission.SYSTEM_ALERT_WINDOW}, view.MY_PERMISSIONS_REQUEST);
            }
        }
        catch (RuntimeException e) {

        }

        view.initActivity();
    }


    public void getUserInfo() {
        User user = new Select().from(User.class).querySingle();
        if (user != null) {
           view.displayUserInfo(user);
        }
    }



}
