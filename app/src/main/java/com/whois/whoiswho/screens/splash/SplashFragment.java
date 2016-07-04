package com.whois.whoiswho.screens.splash;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whois.whoiswho.R;
import com.whois.whoiswho.screens.login.LoginFragment;
import com.whois.whoiswho.screens.mainscreen.TabFragment;
import com.whois.whoiswho.utils.FontManager;
import com.whois.whoiswho.utils.SharedPreferencesSaver;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by stasenkopavel on 4/20/16.
 */
public class SplashFragment extends Fragment {

    public final static int MY_PERMISSIONS_REQUEST = 100;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_splash, container, false);
        ButterKnife.bind(this, view);
        FontManager.overrideFonts(view);
        return view;
    }


    @OnClick(R.id.letsGoButton)
    public void goToLoginScreen() {
        Log.d("Token", SharedPreferencesSaver.get().getToken());
        if (TextUtils.isEmpty(SharedPreferencesSaver.get().getToken())) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent, new LoginFragment()).commit();
        }
        else {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent, new TabFragment()).commit();
        }
        //((MainActivityMethods)getActivity()).switchFragment(new TabFragment());
    }


}
