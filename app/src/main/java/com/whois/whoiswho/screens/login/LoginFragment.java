package com.whois.whoiswho.screens.login;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.whois.whoiswho.R;
import com.whois.whoiswho.app.App;
import com.whois.whoiswho.model.Phone;
import com.whois.whoiswho.utils.CountryManager;
import com.whois.whoiswho.utils.FontManager;
import com.whois.whoiswho.utils.PhoneFormatter;
import com.whois.whoiswho.utils.Toaster;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by stasenkopavel on 4/26/16.
 */
public class LoginFragment extends Fragment {

    @BindView(R.id.phoneEditText) EditText phoneEditText;
    @BindView(R.id.countrySpinner) Spinner countrySpinner;
    @BindView(R.id.okButton) TextView okButton;

    private static LoginPresenter presenter;
    private String countryIso;
    private String countryCode = "+380";
    private FirebaseAnalytics mFirebaseAnalytics;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_login, container, false);
        ButterKnife.bind(this, view);
        FontManager.overrideFonts(view);
        presenter = new LoginPresenter(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.countryList, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);
        final String countryLocale = getResources().getConfiguration().locale.getCountry();
        if (countryLocale.equals("CZ"))
            countrySpinner.setSelection(1);
        else
            countrySpinner.setSelection(0);
        phoneEditText.setSelection(phoneEditText.getText().length());
        phoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryIso = CountryManager.getCountryIsoFromName((String) countrySpinner.getItemAtPosition(position));
                countryCode = CountryManager.getCodeFromIso(countryIso);
                phoneEditText.setText(countryCode + " ");
                phoneEditText.setSelection(phoneEditText.getText().length());
                phoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher(countryIso));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    @OnClick(R.id.okButton)
    public void login() {
        if (valdatePhone(phoneEditText.getText().toString())) {
            presenter.login(PhoneFormatter.removeAllNonNumeric(phoneEditText.getText().toString()), countryIso);
            sendLogToFirebase("Login", "number", phoneEditText.getText().toString());
        }
        else
            Toaster.toast(getActivity(), R.string.wrongNumber);
    }

    private boolean valdatePhone(String phone) {
        if ((phone.startsWith(countryCode)) && (PhoneFormatter.removeAllNonNumeric(phone).length() >= 13))
            return true;
        else
            return false;
    }

    public void sendLogToFirebase(String event, String key, String value) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, key);
        bundle.putString(FirebaseAnalytics.Param.VALUE, value);
        mFirebaseAnalytics.logEvent(event, bundle);
    }


}
