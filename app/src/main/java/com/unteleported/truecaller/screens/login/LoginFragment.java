package com.unteleported.truecaller.screens.login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.unteleported.truecaller.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by stasenkopavel on 4/26/16.
 */
public class LoginFragment extends Fragment {

    @Bind(R.id.nameEditText) EditText nameEditText;
    @Bind(R.id.phoneEditText) EditText phoneEditText;
    @Bind(R.id.emailEditText) EditText emailEditText;

    private static LoginPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_login, container, false);
        ButterKnife.bind(this, view);
        TelephonyManager tMgr = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        phoneEditText.setText(tMgr.getLine1Number());
        presenter = new LoginPresenter(this);
        return view;
    }

    @OnClick(R.id.okButton)
    public void login() {
        presenter.login();
    }

}
