package com.unteleported.truecaller.screens.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.screens.tutorial.TutorialDialog;
import com.unteleported.truecaller.utils.CountryManager;
import com.unteleported.truecaller.utils.FontManager;
import com.unteleported.truecaller.utils.PhoneFormatter;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

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
                phoneEditText.setText(CountryManager.getCodeFromIso(countryIso));
                phoneEditText.setSelection(phoneEditText.getText().length());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    @OnClick(R.id.okButton)
    public void login() {
      presenter.login(PhoneFormatter.removeAllNonNumeric(phoneEditText.getText().toString()), countryIso);
    }






}
