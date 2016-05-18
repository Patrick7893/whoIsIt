package com.unteleported.truecaller.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.squareup.picasso.Picasso;
import com.unteleported.truecaller.api.ApiFactory;
import com.unteleported.truecaller.api.LoadContactsRequest;
import com.unteleported.truecaller.api.RegistrationResponse;
import com.unteleported.truecaller.app.App;
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.model.User;
import com.unteleported.truecaller.screens.login.LoginFragment;
import com.unteleported.truecaller.screens.mainscreen.TabFragment;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;
import com.unteleported.truecaller.utils.UserContactsManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 4/28/16.
 */
public class MainActivityPresenter {

    private MainActivity view;

    public MainActivityPresenter(MainActivity view) {
        this.view = view;
    }

    public void requestPermissions() {
        if ((ContextCompat.checkSelfPermission(view, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)&&
                (ContextCompat.checkSelfPermission(view, Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED)&&
                (ContextCompat.checkSelfPermission(view, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)) {
            view.switchFragment(new LoginFragment());
            view.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            ActivityCompat.requestPermissions(view, new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG, Manifest.permission.CALL_PHONE}, view.MY_PERMISSIONS_REQUEST);
        }
        else {
            if (!TextUtils.isEmpty(SharedPreferencesSaver.get().getToken())) {
                view.switchFragment(new TabFragment(), false);
            } else {
                view.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                view.switchFragment(new LoginFragment());
            }
        }
    }


    public void setUserInfo() throws IOException {
        User user = new Select().from(User.class).querySingle();
        if (user != null) {
           view.displayUserInfo(user);
        }
    }

}
