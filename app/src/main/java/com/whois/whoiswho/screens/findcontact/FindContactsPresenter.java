package com.whois.whoiswho.screens.findcontact;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.whois.whoiswho.R;
import com.whois.whoiswho.api.ApiFactory;
import com.whois.whoiswho.api.FindPhoneResponse;
import com.whois.whoiswho.app.App;
import com.whois.whoiswho.model.Contact;
import com.whois.whoiswho.utils.PermissionManager;
import com.whois.whoiswho.utils.PhoneFormatter;
import com.whois.whoiswho.utils.SharedPreferencesSaver;
import com.whois.whoiswho.utils.Toaster;
import com.whois.whoiswho.utils.ContactsManager;

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
                ArrayList<Contact> contacts = ContactsManager.readContacts(view.getActivity(), false);
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



    public void find(String query) {
        if (Character.isDigit(query.charAt(0))) {
            query = PhoneFormatter.removeAllNonNumeric(query);
            if (query.length() < 10) {
                Toaster.toast(App.getContext(), R.string.inputTheRightNumber);
                return;
            } else {
                switch (query.length()) {
                    case 10:
                        query = "+38" + query;
                        break;
                    case 11:
                        query = "+3" + query;
                        break;
                    case 12:
                        query = "+" + query;
                        break;
                }
            }
        }
        view.setProgressBarVisibility(View.VISIBLE);
        ApiFactory.createRetrofitService().findPhone(SharedPreferencesSaver.get().getToken(), query).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<FindPhoneResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d("ERROR", e.getMessage());
                Toaster.toast(App.getContext(), R.string.firndNothind);
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
