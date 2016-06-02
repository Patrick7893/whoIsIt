package com.unteleported.truecaller.screens.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.activity.MainActivityMethods;
import com.unteleported.truecaller.api.ApiFactory;
import com.unteleported.truecaller.api.ApiInterface;
import com.unteleported.truecaller.api.RegistrationResponse;
import com.unteleported.truecaller.app.App;
import com.unteleported.truecaller.model.User;
import com.unteleported.truecaller.screens.mainscreen.TabFragment;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit.mime.TypedFile;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 4/28/16.
 */
public class LoginPresenter {


    private LoginFragment view;

    public LoginPresenter(LoginFragment view) {
        this.view = view;
    }


    public void login(final String number, final String countryIso) {
        final ProgressDialog pd = new ProgressDialog(view.getActivity());
        pd.setMessage(App.getContext().getString(R.string.enter));
        pd.show();
        ApiFactory.createRetrofitService().login(number, countryIso).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<RegistrationResponse>() {
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
                Log.d("ONNEXT", String.valueOf(s.getError()));
                Log.d("TOKEN", s.getToken() + "      " +  String.valueOf(s.getSms()));
                if (s.getError() == 0) {
                    User user = s.getData();
                    if (!TextUtils.isEmpty(s.getAvatarPath()))
                        user.setAvatarPath(ApiInterface.SERVICE_ENDPOINT + s.getAvatarPath());
                    user.save();
                    SharedPreferencesSaver.get().saveToken(s.getToken());
                    view.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent, new TabFragment()).addToBackStack(null).commit();
                }
                else if (s.getError() == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(NewUserFragment.ID, s.getId());
                    bundle.putInt(SMSConfirmFragment.SMS, s.getSms());
                    bundle.putString(NewUserFragment.PHONE, number);
                    bundle.putString(NewUserFragment.COUNTRY, countryIso);
                    SMSConfirmFragment smsConfirmFragment = new SMSConfirmFragment();
                    smsConfirmFragment.setArguments(bundle);
                    view.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent, smsConfirmFragment).addToBackStack(null).commit();
                }
            }
        });
    }
}
