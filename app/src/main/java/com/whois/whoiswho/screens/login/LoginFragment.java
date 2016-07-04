package com.whois.whoiswho.screens.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.whois.whoiswho.R;
import com.whois.whoiswho.utils.CountryManager;
import com.whois.whoiswho.utils.FontManager;
import com.whois.whoiswho.utils.PhoneFormatter;
import com.whois.whoiswho.utils.Toaster;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by stasenkopavel on 4/26/16.
 */
public class LoginFragment extends Fragment {

    @Bind(R.id.phoneEditText) EditText phoneEditText;
    @Bind(R.id.countrySpinner) Spinner countrySpinner;
    @Bind(R.id.okButton) TextView okButton;

    private static LoginPresenter presenter;
    private String countryIso;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_login, container, false);
        ButterKnife.bind(this, view);
        FontManager.overrideFonts(view);
        presenter = new LoginPresenter(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.countryList, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);
        phoneEditText.setSelection(phoneEditText.getText().length());
        phoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryIso = CountryManager.getCountryIsoFromName((String) countrySpinner.getItemAtPosition(position));
                phoneEditText.setText(CountryManager.getCodeFromIso(countryIso) + " ");
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
        if (phoneEditText.getText().toString().startsWith("+380"))
            presenter.login(PhoneFormatter.removeAllNonNumeric(phoneEditText.getText().toString()), countryIso);
        else
            Toaster.toast(getActivity(), R.string.wrongNumber);
    }


}
