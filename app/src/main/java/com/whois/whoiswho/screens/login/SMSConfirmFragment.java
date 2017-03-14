package com.whois.whoiswho.screens.login;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.whois.whoiswho.R;
import com.whois.whoiswho.api.RegistrationResponse;
import com.whois.whoiswho.utils.FontManager;
import com.whois.whoiswho.utils.KeyboardManager;
import com.whois.whoiswho.utils.Toaster;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by stasenkopavel on 5/16/16.
 */
public class SMSConfirmFragment extends Fragment {

    public static final String SMS = "SMS";

    @BindView(R.id.sms1EditText) EditText sms1EditText;
    @BindView(R.id.sms2EditText) EditText sms2EditText;
    @BindView(R.id.sms3EditText) EditText sms3EditText;
    @BindView(R.id.sms4EditText) EditText sms4EditText;
    @BindView(R.id.smsNumberTextView) TextView smsNumberTextView;

    private int sms;
    private String number;

    private SMSConfirmPresenter presenter;
    public FirebaseAnalytics mFirebaseAnalytics;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_smsconfirm, container, false);
        ButterKnife.bind(this, view);
        FontManager.overrideFonts(view);
        presenter = new SMSConfirmPresenter(this);
        sms1EditText.requestFocus();
        KeyboardManager.showKeyboard(getActivity());
        sms1EditText.addTextChangedListener(textWatcher);
        sms2EditText.addTextChangedListener(textWatcher);
        sms3EditText.addTextChangedListener(textWatcher);
        sms4EditText.addTextChangedListener(textWatcher);
        sms = this.getArguments().getInt(SMS);
        number = getArguments().getString(NewUserFragment.PHONE);
        smsNumberTextView.setText(getString(R.string.smsconfirm1) + " " + hideNumber(number) +  " " + getString(R.string.smsconfirm2));
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        return view;
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count > before) {
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
            else {
                if (sms2EditText.getText().length()==0) {
                    sms1EditText.requestFocus();
                }
                else if (sms3EditText.getText().length()==0) {
                    sms2EditText.requestFocus();
                }
                else if (sms4EditText.getText().length()==0) {
                    sms3EditText.requestFocus();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @OnClick(R.id.okButton)
    public void okButton() {
        if (!TextUtils.isEmpty(sms1EditText.getText()) && !TextUtils.isEmpty(sms2EditText.getText()) && !TextUtils.isEmpty(sms3EditText.getText()) && !TextUtils.isEmpty(sms4EditText.getText())) {
            String smsString = sms1EditText.getText().toString() + sms2EditText.getText().toString() + sms3EditText.getText().toString() + sms4EditText.getText().toString();
            presenter.smsConfirm(this.getArguments().getString(NewUserFragment.PHONE), getArguments().getString(NewUserFragment.COUNTRY), smsString);
            sendLogToFirebase("ConfirmSMS", "sms", smsString);
        }
        else {
            Toaster.toast(getActivity().getApplicationContext(), R.string.pleaseInputSMS);
        }
    }

    @OnClick(R.id.smsNotReceivedButton)
    public void resendSms() {
        presenter.resend(number, getArguments().getString(NewUserFragment.COUNTRY));
    }

    public void goToNewUserScreen(RegistrationResponse registrationResponse) {
        KeyboardManager.hideKeyboard(getActivity());
        if (registrationResponse.getError() == 0) {
            Bundle bundle = new Bundle();
            bundle.putInt(NewUserFragment.ID, registrationResponse.getId());
            bundle.putString(NewUserFragment.COUNTRY, this.getArguments().getString(NewUserFragment.COUNTRY));
            bundle.putString(NewUserFragment.PHONE, this.getArguments().getString(NewUserFragment.PHONE));
            NewUserFragment newUserFragment = new NewUserFragment();
            newUserFragment.setArguments(bundle);
            getActivity().getFragmentManager().beginTransaction().replace(R.id.flContent, newUserFragment).commit();
        }
        else {
            Toaster.toast(getActivity(), R.string.wrongSms);
        }
    }

    private String hideNumber(String number) {
        StringBuilder stringBuilder = new StringBuilder();
        if (number.length() > 12) {
            return stringBuilder.append(number.substring(0, 6)).append("*****").append(number.substring(number.length()-2)).toString();
        }
        else if (number.length() == 12){
            return stringBuilder.append(number.substring(0, 5)).append("*****").append(number.substring(number.length()-2)).toString();
        }
        else {
            return number;
        }
    }

    public void sendLogToFirebase(String event, String key, String value) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, key);
        bundle.putString(FirebaseAnalytics.Param.VALUE, value);
        mFirebaseAnalytics.logEvent(event, bundle);
    }
}
