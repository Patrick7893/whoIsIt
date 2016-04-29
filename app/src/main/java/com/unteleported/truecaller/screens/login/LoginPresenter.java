package com.unteleported.truecaller.screens.login;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.unteleported.truecaller.activity.MainActivityMethods;
import com.unteleported.truecaller.api.ApiFactory;
import com.unteleported.truecaller.api.ApiInterface;
import com.unteleported.truecaller.api.RegistrationResponse;
import com.unteleported.truecaller.model.User;
import com.unteleported.truecaller.screens.mainscreen.TabFragment;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 4/28/16.
 */
public class LoginPresenter {

    private LoginFragment view;
    private User user;

    public LoginPresenter(LoginFragment view) {
        this.view = view;
    }

    public void login() {
        TelephonyManager tMgr = (TelephonyManager)view.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        user = new User(view.nameEditText.getText().toString(), view.phoneEditText.getText().toString(), view.emailEditText.getText().toString(), tMgr.getSimCountryIso().toUpperCase());
        ApiFactory.createRetrofitService().newUser(user).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<RegistrationResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d("ERROR", e.getMessage());
            }

            @Override
            public void onNext(RegistrationResponse s) {
                Log.d("ONNEXT", String.valueOf(s.error));
                if (s.error == 0) {
                    Log.d("ONNEXT", String.valueOf(s.token));
                    user.save();
                    SharedPreferencesSaver.get().saveToken(s.token);
                    ((MainActivityMethods) view.getActivity()).switchFragment(new TabFragment());
                }
            }
        });
    }
}
