package com.unteleported.truecaller.screens.login;

import android.app.ProgressDialog;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.api.ApiFactory;
import com.unteleported.truecaller.api.RegistrationResponse;
import com.unteleported.truecaller.app.App;

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

    public void smsConfirm(int sms) {
        final ProgressDialog pd = new ProgressDialog(view.getActivity());
        pd.setMessage(App.getContext().getString(R.string.checkSms));
        pd.show();
        ApiFactory.createRetrofitService().smsConfirm(sms).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<RegistrationResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                pd.dismiss();
            }

            @Override
            public void onNext(RegistrationResponse registrationResponse) {
                pd.dismiss();
                view.goToNewUserScreen(registrationResponse);
            }
        });
    }
}
