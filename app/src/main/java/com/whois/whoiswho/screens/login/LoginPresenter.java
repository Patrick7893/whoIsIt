package com.whois.whoiswho.screens.login;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.whois.whoiswho.R;
import com.whois.whoiswho.api.ApiFactory;
import com.whois.whoiswho.api.ApiInterface;
import com.whois.whoiswho.api.RegistrationResponse;
import com.whois.whoiswho.app.App;
import com.whois.whoiswho.model.User;
import com.whois.whoiswho.screens.mainscreen.TabFragment;
import com.whois.whoiswho.utils.SharedPreferencesSaver;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by stasenkopavel on 4/28/16.
 */
public class LoginPresenter {


    private LoginFragment view;
    private Calendar staticCalendar;
    private final int beginNumbersCount = 70000000;

    public LoginPresenter(LoginFragment view) {
        this.view = view;
        staticCalendar = Calendar.getInstance();
        staticCalendar.set(2016, Calendar.SEPTEMBER, 8);
    }


    public void login(final String number, final String countryIso) {
        final ProgressDialog pd = new ProgressDialog(view.getActivity());
        pd.setMessage(App.getContext().getString(R.string.enter));
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
                    view.getActivity().getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    view.getActivity().getFragmentManager().beginTransaction().replace(R.id.flContent, new TabFragment()).addToBackStack(null).commit();
                }
                else if (s.getError() == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(SMSConfirmFragment.SMS, s.getSms());
                    bundle.putString(NewUserFragment.PHONE, number);
                    bundle.putString(NewUserFragment.COUNTRY, countryIso);
                    SMSConfirmFragment smsConfirmFragment = new SMSConfirmFragment();
                    smsConfirmFragment.setArguments(bundle);
                  //  view.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent, smsConfirmFragment).addToBackStack(null).commit();
                    view.getActivity().getFragmentManager().beginTransaction().replace(R.id.flContent, smsConfirmFragment).addToBackStack(null).commit();
                }
            }
        });
    }


    public int generateNumberCount() {
        Long diffInDays = (System.currentTimeMillis() - staticCalendar.getTimeInMillis())/(24*60*60*1000);
        if (diffInDays < 0)
            return beginNumbersCount;
        Random rand = new Random();
        int randomNum = rand.nextInt((1200  - 800) + 1) + 800;
        return beginNumbersCount + (int) (diffInDays * randomNum);
    }
}
