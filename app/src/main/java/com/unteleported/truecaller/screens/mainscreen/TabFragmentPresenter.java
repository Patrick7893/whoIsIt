package com.unteleported.truecaller.screens.mainscreen;

import android.util.Log;

import com.unteleported.truecaller.api.ApiFactory;
import com.unteleported.truecaller.api.LoadContactsRequest;
import com.unteleported.truecaller.api.RegistrationResponse;
import com.unteleported.truecaller.app.App;
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;
import com.unteleported.truecaller.utils.UserContactsManager;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 5/9/16.
 */
public class TabFragmentPresenter {

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
                Log.d("loadContacts", e.getMessage());
            }

            @Override
            public void onNext(RegistrationResponse registrationResponse) {
                Log.d("LoadContacts", String.valueOf(registrationResponse.getError()));
            }
        });
    }
}
