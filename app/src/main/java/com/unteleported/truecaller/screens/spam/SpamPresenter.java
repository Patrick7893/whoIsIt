package com.unteleported.truecaller.screens.spam;

import android.util.Log;
import android.view.View;

import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.unteleported.truecaller.R;
import com.unteleported.truecaller.api.ApiFactory;
import com.unteleported.truecaller.api.FindPhoneResponse;
import com.unteleported.truecaller.api.GetSpammersResponse;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.model.Phone_Table;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 5/16/16.
 */
public class SpamPresenter {

    private SpamFragment view;

    public SpamPresenter(SpamFragment view) {
        this.view = view;
    }

    public void getLocalSpammers() {
        SQLite.select().from(Phone.class).where(Phone_Table.isBlocked.is(true)).and(Phone_Table.name.eq(view.getString(R.string.unknown_user))).async().queryResultCallback(new QueryTransaction.QueryResultCallback<Phone>() {
            @Override
            public void onQueryResult(QueryTransaction transaction, CursorResult<Phone> tResult) {
                view.dispayLocalSpammers(tResult);
            }
        }).execute();
    }

    public void getSpammers() {
        ApiFactory.createRetrofitService().getSpammers(SharedPreferencesSaver.get().getToken()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<GetSpammersResponse>() {
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
                view.displaySpammersFromServer(response);
            }
        });
    }
}
