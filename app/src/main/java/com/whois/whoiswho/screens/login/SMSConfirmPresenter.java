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

import retrofit2.Response;
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

    public void smsConfirm(String number, String countryIso, String sms) {
        final ProgressDialog pd = new ProgressDialog(view.getActivity());
        pd.setMessage(App.getContext().getString(R.string.checkSms));
        pd.show();
        ApiFactory.getInstance().getApiInterface().smsConfirm(number, countryIso, sms).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<RegistrationResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                pd.dismiss();
                Toaster.toast(SMSConfirmPresenter.this.view.getActivity(), R.string.wrongSms);
            }

            @Override
            public void onNext(RegistrationResponse registrationResponse) {
                pd.dismiss();
                SMSConfirmPresenter.this.view.goToNewUserScreen(registrationResponse);
            }
        });
    }

    public void resend(final String number, final String countryIso) {
        final ProgressDialog pd = new ProgressDialog(view.getActivity());
        pd.setMessage(App.getContext().getString(R.string.dispatch));
        pd.show();
        ApiFactory.getInstance().getApiInterface().login(number, countryIso).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Response<RegistrationResponse>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Response<RegistrationResponse> registrationResponseResponse) {
                pd.dismiss();
                if ((registrationResponseResponse.body()).getError() == 0) {
                    User user = registrationResponseResponse.body().getData();
                    if (!TextUtils.isEmpty(registrationResponseResponse.body().getAvatarPath())) {
                        user.setAvatarPath(ApiInterface.SERVICE_ENDPOINT + registrationResponseResponse.body().getAvatarPath());
                    }
                    user.save();
                    SharedPreferencesSaver.get().saveToken(registrationResponseResponse.body().getToken());
                    SMSConfirmPresenter.this.view.getActivity().getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.flContent, new TabFragment()).addToBackStack(null).commit();
                } else if (registrationResponseResponse.body().getError() == 1) {
                    Toaster.toast(App.getContext(), (int) R.string.wrongSms);
                }
            }
        });
    }
}
