package com.unteleported.truecaller.screens.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.utils.FontManager;
import com.unteleported.truecaller.utils.Toaster;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by stasenkopavel on 5/16/16.
 */
public class SMSConfirmFragment extends Fragment {

    public static final String SMS = "SMS";

    @Bind(R.id.sms1EditText) EditText sms1EditText;
    @Bind(R.id.sms2EditText) EditText sms2EditText;
    @Bind(R.id.sms3EditText) EditText sms3EditText;
    @Bind(R.id.sms4EditText) EditText sms4EditText;

    private int sms;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_smsconfirm, container, false);
        ButterKnife.bind(this, view);
        FontManager.overrideFonts(view);
        sms1EditText.addTextChangedListener(textWatcher);
        sms2EditText.addTextChangedListener(textWatcher);
        sms3EditText.addTextChangedListener(textWatcher);
        sms4EditText.addTextChangedListener(textWatcher);
        sms = this.getArguments().getInt(SMS);

        return view;
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (sms3EditText.getText().length()>0) {
                sms4EditText.requestFocus();
            }
            else if (sms2EditText.getText().length()>0) {
                sms3EditText.requestFocus();
            }
            else if (sms1EditText.getText().length()>0) {
                sms2EditText.requestFocus();
            }
        }
    };

    @OnClick(R.id.okButton)
    public void okButton() {
        String smsString = sms1EditText.getText().toString() + sms2EditText.getText().toString() + sms3EditText.getText().toString() + sms4EditText.getText().toString();
        if (Integer.valueOf(smsString) == sms) {
            Bundle bundle = new Bundle();
            bundle.putInt(NewUserFragment.ID, this.getArguments().getInt(NewUserFragment.ID));
            bundle.putString(NewUserFragment.COUNTRY, this.getArguments().getString(NewUserFragment.COUNTRY));
            NewUserFragment newUserFragment = new NewUserFragment();
            newUserFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent, newUserFragment).commit();
        }
        else
            Toaster.toast(getActivity(), R.string.smsconfirm);
    }
}
