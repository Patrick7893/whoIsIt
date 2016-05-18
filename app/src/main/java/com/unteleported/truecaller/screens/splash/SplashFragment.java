package com.unteleported.truecaller.screens.splash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.activity.MainActivity;
import com.unteleported.truecaller.activity.MainActivityMethods;
import com.unteleported.truecaller.screens.login.LoginFragment;
import com.unteleported.truecaller.screens.mainscreen.TabFragment;
import com.unteleported.truecaller.utils.FontManager;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;

import butterknife.Bind;
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
