package com.unteleported.truecaller.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;

import com.unteleported.truecaller.activity.MainActivity;
import com.unteleported.truecaller.app.App;
import com.unteleported.truecaller.screens.login.LoginFragment;
import com.unteleported.truecaller.screens.mainscreen.TabFragment;

/**
 * Created by stasenkopavel on 6/23/16.
 */
public class PermissionManager {

    public static void requestPermissions(Activity activity, String permission) {
        if ((ContextCompat.checkSelfPermission(App.getContext(), permission) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG, Manifest.permission.CALL_PHONE, Manifest.permission.SYSTEM_ALERT_WINDOW}, MainActivity.MY_PERMISSIONS_REQUEST);
        }
    }
}
