package com.unteleported.truecaller.screens.spam;

import android.util.Log;
import android.view.View;

import com.unteleported.truecaller.api.ApiFactory;
import com.unteleported.truecaller.api.FindPhoneResponse;
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

    public void getSpammers() {
        ApiFactory.createRetrofitService().getSpammers(SharedPreferencesSaver.get().getToken()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<FindPhoneResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d("getSpammers", e.getMessage());
            }

            @Override
            public void onNext(FindPhoneResponse findPhoneResponse) {
                Log.d("getSpammers", String.valueOf(findPhoneResponse.getError()));
                view.displaySpammersFromServer(findPhoneResponse);
            }
        });
    }
}
