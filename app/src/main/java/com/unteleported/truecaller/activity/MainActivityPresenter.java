package com.unteleported.truecaller.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.unteleported.truecaller.api.ApiFactory;
import com.unteleported.truecaller.api.ApiInterface;
import com.unteleported.truecaller.api.LoadContactsRequest;
import com.unteleported.truecaller.api.RegistrationResponse;
import com.unteleported.truecaller.app.App;
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.model.User;
import com.unteleported.truecaller.screens.mainscreen.TabFragment;
import com.unteleported.truecaller.screens.splash.SplashFragment;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;
import com.unteleported.truecaller.utils.UserContactsManager;

import java.io.File;
import java.util.ArrayList;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
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
            view.switchFragment(new SplashFragment());
            view.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            ActivityCompat.requestPermissions(view, new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG, Manifest.permission.CALL_PHONE}, view.MY_PERMISSIONS_REQUEST);
        }
        else {
            if (!TextUtils.isEmpty(SharedPreferencesSaver.get().getToken())) {
                view.switchFragment(new TabFragment(), false);
            }
            else {
                view.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                view.switchFragment(new SplashFragment());
            }
            getContacts.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ArrayList<Contact>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Log.d("readContacts", e.getMessage());

                }

                @Override
                public void onNext(ArrayList<Contact> contacts) {
                    Log.d("readContacts", "NEXT");
                    ArrayList<Phone> phones = new ArrayList<Phone>();
                    TelephonyManager tMgr = (TelephonyManager) view.getSystemService(Context.TELEPHONY_SERVICE);
                    for (Contact contact : contacts) {
                        for (Phone phone : contact.getPhones()) {
                            phone.setNumber(phone.getNumber().replaceAll("[^0-9+]", ""));
                            phones.add(phone);
                            if (!phone.getNumber().contains("+")) {
                                phone.setCountryIso(tMgr.getSimCountryIso().toUpperCase());
                            }
                        }
                    }
                    loadContatcs(phones);
                }
            });
            //((MainActivityMethods)getActivity()).switchFragment(new TabFragment());
        }
    }

    Observable<ArrayList<Contact>> getContacts = Observable.create(new Observable.OnSubscribe<ArrayList<Contact>>() {
        @Override
        public void call(Subscriber<? super ArrayList<Contact>> subscriber) {
            ArrayList<Contact> contacts = UserContactsManager.readContacts(App.getContext(), false);
            subscriber.onNext(contacts);
            subscriber.onCompleted();
        }
    });

    public void loadContatcs(ArrayList<Phone> phones) {
        ApiFactory.createRetrofitService().loadContacts(new LoadContactsRequest(SharedPreferencesSaver.get().getToken(), phones)).observeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<RegistrationResponse>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.d("loadConctas", e.getMessage());
            }

            @Override
            public void onNext(RegistrationResponse registrationResponse) {
                Log.d("LoadCOntacts", String.valueOf(registrationResponse.error));
            }
        });
    }

    public void setUserInfo() {
        User user = User.findById(User.class, 1);
        if (user != null) {
            view.nameTextView.setText(user.getName());
            view.phoneTextView.setText(user.getNumber());
        }
    }
}
