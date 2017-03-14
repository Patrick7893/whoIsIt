package com.whois.whoiswho.screens.mainscreen;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.whois.whoiswho.api.ApiFactory;
import com.whois.whoiswho.api.LoadContactsRequest;
import com.whois.whoiswho.api.RegistrationResponse;
import com.whois.whoiswho.app.App;
import com.whois.whoiswho.model.Contact;
import com.whois.whoiswho.model.ContactNumber;
import com.whois.whoiswho.model.Phone;
import com.whois.whoiswho.model.Phone_Table;
import com.whois.whoiswho.utils.CountryManager;
import com.whois.whoiswho.utils.PermissionManager;
import com.whois.whoiswho.utils.SharedPreferencesSaver;
import com.whois.whoiswho.utils.ContactsManager;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 5/9/16.
 */
public class TabFragmentPresenter {

    private TabFragment view;
    private Subscription loadContactsSubscription;

    public TabFragmentPresenter (TabFragment view) {
        this.view = view;
    }

    Observable<ArrayList<Phone>> getContacts = Observable.create(new Observable.OnSubscribe<ArrayList<Phone>>() {
        @Override
        public void call(Subscriber<? super ArrayList<Phone>> subscriber) {
            if (ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                ArrayList<Contact> contacts = ContactsManager.readContacts(App.getContext(), false);
                ArrayList<Phone> phones = new ArrayList<Phone>();
                ArrayList<Phone> phonesToLoad = new ArrayList<>();
                TelephonyManager tMgr = (TelephonyManager) view.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                for (Contact contact : contacts) {
                    for (ContactNumber number : contact.getNumbers()) {
                        Phone phone = new Phone(number.getNumber(), number.getTypeOfNumber());
                        phone.setNumber(number.getNumber().replaceAll("[^0-9+]", ""));
                        phone.setName(contact.getName());
                        phones.add(phone);
                        if (!phone.getNumber().contains("+")) {
                            phone.setCountryIso(tMgr.getSimCountryIso().toUpperCase());
                            number.setCountryIso(tMgr.getSimCountryIso().toUpperCase());
                        }
                        else {
                            phone.setCountryIso(CountryManager.getIsoFromPhone(phone.getNumber()));
                            number.setCountryIso(CountryManager.getCountryIsoFromName(phone.getNumber()));
                        }
                    }
                }
                for (Phone phone : phones) {
                    Phone phoneFromDb = new Select().from(Phone.class).where(Phone_Table.number.is(phone.getNumber())).querySingle();
                    if (phoneFromDb == null) {
                        phonesToLoad.add(phone);
                        phone.save();
                    }
                    else {
                        if (!phoneFromDb.getName().equals(phone.getName())) {
                            phonesToLoad.add(phone);
                            phone.save();
                        }
                    }
                }
                subscriber.onNext(phonesToLoad);
                subscriber.onCompleted();
            }
            else {
                PermissionManager.requestPermissions(view.getActivity(), Manifest.permission.READ_CONTACTS);
            }

        }
    });

    public void loadContatcs() {
        loadContactsSubscription = getContacts.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ArrayList<Phone>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ArrayList<Phone> phones) {
                if (phones.size() > 0) {
                    ApiFactory.getInstance().getApiInterface().loadContacts(new LoadContactsRequest(SharedPreferencesSaver.get().getToken(), phones)).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<RegistrationResponse>() {
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
        });

    }

    public void unsubsribe() {
        loadContactsSubscription.unsubscribe();
    }
}
