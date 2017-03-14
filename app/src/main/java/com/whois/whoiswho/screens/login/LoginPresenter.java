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
import com.whois.whoiswho.utils.Toaster;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        ApiFactory.getInstance().getApiInterface().login(number, countryIso).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Response<RegistrationResponse>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Response<RegistrationResponse> registrationResponseResponse) {
                RegistrationResponse response = registrationResponseResponse.body();
                pd.dismiss();
                Log.d("ERROR", String.valueOf(response.getError()));
                if (response.getError() == 0) {
                    User user = response.getData();
                    user.setCountyIso(countryIso);
                    if (!TextUtils.isEmpty(user.getAvatarPath())) {
                        user.setAvatarPath(ApiInterface.SERVER_DOMAIN + user.getAvatarPath());
                    }
                    user.save();
                    SharedPreferencesSaver.get().saveToken(response.getData().getToken());
                    view.goToMainScreen();
                } else if (response.getError() == 1) {
                    view.goToSmsScreen(Integer.valueOf(response.getSms()), number);
                } else if (response.getError() == 2) {
                    Toaster.toast(App.getContext(), App.getContext().getString(R.string.wrongNumber));
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
