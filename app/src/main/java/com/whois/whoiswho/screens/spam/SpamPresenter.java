package com.whois.whoiswho.screens.spam;

import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.whois.whoiswho.R;
import com.whois.whoiswho.api.ApiFactory;
import com.whois.whoiswho.api.FindPhoneResponse;
import com.whois.whoiswho.api.GetSpammersResponse;
import com.whois.whoiswho.model.Phone;
import com.whois.whoiswho.model.Phone_Table;
import com.whois.whoiswho.utils.SharedPreferencesSaver;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 5/16/16.
 */
public class SpamPresenter {

    private SpamFragment view;
    private Subscription getSpammersSubscription;

    public SpamPresenter(SpamFragment view) {
        this.view = view;
    }

    public void getLocalSpammers() {
        SQLite.select().from(Phone.class).where(Phone_Table.isBlocked.is(true)).and(Phone_Table.name.eq(view.getString(R.string.unknown_user))).async().queryResultCallback((transaction, tResult) -> view.dispayLocalSpammers(tResult)).execute();
    }

    public void getSpammers() {
        getSpammersSubscription = ApiFactory.getInstance().getApiInterface().getSpammers(SharedPreferencesSaver.get().getToken()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<GetSpammersResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d("getSpammers", e.getMessage());
            }

            @Override
            public void onNext(GetSpammersResponse response) {
                Log.d("getSpammers", String.valueOf(response.error));
                if (response.error == 0)
                    view.displaySpammersFromServer(response);
            }
        });
    }

    public void unsubscribe() {
        if (!getSpammersSubscription.isUnsubscribed())
            getSpammersSubscription.unsubscribe();
    }
}
