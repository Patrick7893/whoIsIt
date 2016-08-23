package com.whois.whoiswho.screens.login;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.whois.whoiswho.R;
import com.whois.whoiswho.api.ApiFactory;
import com.whois.whoiswho.api.ApiInterface;
import com.whois.whoiswho.api.RegistrationResponse;
import com.whois.whoiswho.app.App;
import com.whois.whoiswho.model.User;
import com.whois.whoiswho.screens.mainscreen.TabFragment;
import com.whois.whoiswho.utils.FirebaseLogManager;
import com.whois.whoiswho.utils.SharedPreferencesSaver;
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

    public void resend(final String number, final String countryIso) {
        final ProgressDialog pd = new ProgressDialog(view.getActivity());
        pd.setMessage(App.getContext().getString(R.string.dispatch));
        pd.show();
        ApiFactory.getInstance().getApiInterface().login(number).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<RegistrationResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d("ERROR", e.getMessage());
                ApiFactory.checkConnection();
                pd.dismiss();
            }

            @Override
            public void onNext(RegistrationResponse s) {
                pd.dismiss();
                if (s.getError() == 0) {
                    User user = s.getData();
                    if (!TextUtils.isEmpty(s.getAvatarPath()))
                        user.setAvatarPath(ApiInterface.SERVICE_ENDPOINT + s.getAvatarPath());
                    user.save();
                    SharedPreferencesSaver.get().saveToken(s.getToken());
                    //view.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent, new TabFragment()).addToBackStack(null).commit();
                    view.getActivity().getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.flContent, new TabFragment()).addToBackStack(null).commit();
                }
            }
        });
    }
}
