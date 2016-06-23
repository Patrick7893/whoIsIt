package com.unteleported.truecaller.screens.mainscreen;

import android.Manifest;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.pm.PackageManager;
import android.print.PrintJob;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.queriable.StringQuery;
import com.unteleported.truecaller.api.ApiFactory;
import com.unteleported.truecaller.api.LoadContactsRequest;
import com.unteleported.truecaller.api.RegistrationResponse;
import com.unteleported.truecaller.app.App;
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.model.ContactNumber;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.model.Phone_Table;
import com.unteleported.truecaller.utils.CountryManager;
import com.unteleported.truecaller.utils.PermissionManager;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;
import com.unteleported.truecaller.utils.UserContactsManager;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 5/9/16.
 */
public class TabFragmentPresenter {

    private TabFragment view;

    public TabFragmentPresenter (TabFragment view) {
        this.view = view;
    }

    Observable<ArrayList<Phone>> getContacts = Observable.create(new Observable.OnSubscribe<ArrayList<Phone>>() {
        @Override
        public void call(Subscriber<? super ArrayList<Phone>> subscriber) {
            if (ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                ArrayList<Contact> contacts = UserContactsManager.readContacts(App.getContext(), false);
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
        getContacts.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ArrayList<Phone>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ArrayList<Phone> phones) {
                if (phones.size() > 0) {
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
        });

    }
}
