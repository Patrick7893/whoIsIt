package com.whois.whoiswho.screens.login;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.whois.whoiswho.R;
import com.whois.whoiswho.api.ApiFactory;
import com.whois.whoiswho.api.RegistrationResponse;
import com.whois.whoiswho.app.App;
import com.whois.whoiswho.utils.FirebaseLogManager;
import com.whois.whoiswho.utils.Toaster;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 6/6/16.
 */
public class SMSConfirmPresenter {

    private SMSConfirmFragment view;

    public SMSConfirmPresenter (SMSConfirmFragment view) {
        this.view = view;
    }

    public void smsConfirm(String number, String sms) {
        final ProgressDialog pd = new ProgressDialog(view.getActivity());
        pd.setMessage(App.getContext().getString(R.string.checkSms));
        pd.show();
        ApiFactory.getInstance().getApiInterface().smsConfirm(number, sms).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<RegistrationResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                pd.dismiss();
                Toaster.toast(view.getActivity(), R.string.wrongSms);
                Bundle bundle = new Bundle();
                bundle.getString(FirebaseAnalytics.Param.ITEM_ID, "ID");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "SMS confirm failed");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, e.getMessage());
                view.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }

            @Override
            public void onNext(RegistrationResponse registrationResponse) {
                pd.dismiss();
                view.goToNewUserScreen(registrationResponse);
            }
        });
    }
}
