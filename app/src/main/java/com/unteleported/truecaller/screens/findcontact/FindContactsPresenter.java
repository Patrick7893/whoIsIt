package com.unteleported.truecaller.screens.findcontact;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.unteleported.truecaller.R;
import com.unteleported.truecaller.activity.MainActivityMethods;
import com.unteleported.truecaller.api.ApiFactory;
import com.unteleported.truecaller.api.FindPhoneResponse;
import com.unteleported.truecaller.app.App;
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.screens.user_profile.UserProfileFragment;
import com.unteleported.truecaller.utils.PermissionManager;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;
import com.unteleported.truecaller.utils.Toaster;
import com.unteleported.truecaller.utils.UserContactsManager;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 4/28/16.
 */
public class FindContactsPresenter {

    private FindContactsFragment view;

    public FindContactsPresenter(FindContactsFragment view) {
        this.view = view;
    }

    Observable<ArrayList<Contact>> getContacts = Observable.create(new Observable.OnSubscribe<ArrayList<Contact>>() {
        @Override
        public void call(Subscriber<? super ArrayList<Contact>> subscriber) {
            if (ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                ArrayList<Contact> contacts = UserContactsManager.readContacts(view.getActivity(), false);
                subscriber.onNext(contacts);
                subscriber.onCompleted();
            }
            else {
                PermissionManager.requestPermissions(view.getActivity(), Manifest.permission.READ_CONTACTS);
            }

        }
    });

    public List<Contact> filter(List<Contact> models, String query) {
        query = query.toLowerCase();
        final List<Contact> filteredModelList = new ArrayList<>();
        for (Contact contact : models) {
            if (contact.getName().toLowerCase().startsWith(query) || contact.getNumbers().get(0).getNumber().replaceAll("[^0-9+]", "").contains(query)) {
                filteredModelList.add(contact);
            }
        }
        return filteredModelList;
    }



    public void find(String number) {
        view.setProgressBarVisibility(View.VISIBLE);
        ApiFactory.createRetrofitService().findPhone(SharedPreferencesSaver.get().getToken(), number).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<FindPhoneResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d("ERROR", e.getMessage());
                ApiFactory.checkConnection();
                view.setProgressBarVisibility(View.GONE);
            }

            @Override
            public void onNext(FindPhoneResponse findPhoneResponse) {
                view.setProgressBarVisibility(View.GONE);
                Log.d("FINDPHONE", String.valueOf(findPhoneResponse.getError()));
                if (findPhoneResponse.getError() == 0)
                    view.displayPhones(findPhoneResponse);
                else
                    Toaster.toast(App.getContext(), R.string.firndNothind);
            }
        });
    }
}
