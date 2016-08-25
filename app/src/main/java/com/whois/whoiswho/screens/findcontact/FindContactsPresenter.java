package com.whois.whoiswho.screens.findcontact;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.whois.whoiswho.R;
import com.whois.whoiswho.api.ApiFactory;
import com.whois.whoiswho.api.FindPhoneResponse;
import com.whois.whoiswho.app.App;
import com.whois.whoiswho.model.Contact;
import com.whois.whoiswho.model.Database;
import com.whois.whoiswho.model.Phone;
import com.whois.whoiswho.model.Phone_Table;
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
    private ArrayList<Phone> lastSearchedPhones = new ArrayList<>();

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

    public void saveSearchedResults(ArrayList<Phone> phones) {
        if ((lastSearchedPhones.size() + phones.size()) > 20) {
            for (int i=0; i<(lastSearchedPhones.size() + phones.size()) - 20; i++) {
                lastSearchedPhones.get(i).delete();
            }
        }
        for (Phone phone : phones) {
            ProcessModelTransaction<Phone> phoneProcessModelTransaction = new ProcessModelTransaction.Builder<>((ProcessModelTransaction.ProcessModel<Phone>) model -> {
                phone.setIsSearched(1);
                phone.save();
            }).processListener((current, total, modifiedModel) -> {

            }).addAll(phone).build();
            Transaction transaction = FlowManager.getDatabase(Database.class).beginTransactionAsync(phoneProcessModelTransaction).build();
            transaction.execute();
        }

    }

    public void deleteLastSearchedPhones() {
        for (Phone phone : lastSearchedPhones) {
            phone.delete();
        }
    }

    public void getLastSearchedPhonesFromDatabase() {
        SQLite.select().from(Phone.class).where(Phone_Table.isSearched.is(1)).async().queryResultCallback((transaction, tResult) -> {
            view.displayPhones(new ArrayList<>(tResult.toList()), false);
            lastSearchedPhones = new ArrayList<>(tResult.toList());
        }).execute();
    }

    public void find(String query, String countryIso) {
        if (Character.isDigit(query.charAt(0)) || query.startsWith("+")) {
            query = PhoneFormatter.removeAllNonNumeric(query);
            if (query.length() < 9) {
                Toaster.toast(App.getContext(), R.string.inputTheRightNumber);
                return;
            }
        }
        if (query.startsWith("+"))
            countryIso = null;
        view.setProgressBarVisibility(View.VISIBLE);
        ApiFactory.getInstance().getApiInterface().findPhone(SharedPreferencesSaver.get().getToken(), query, countryIso).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<FindPhoneResponse>() {
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
                if (findPhoneResponse.getError() == 0) {
                    view.displayPhones(findPhoneResponse.getData(), true);
                    saveSearchedResults(findPhoneResponse.getData());
                }
                else
                    Toaster.toast(App.getContext(), R.string.firndNothind);
            }
        });
    }

    public ArrayList<Phone> getLastSearchedPhones() {
        return lastSearchedPhones;
    }
}
