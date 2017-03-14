package com.whois.whoiswho.screens.login;

import android.app.Fragment;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.whois.whoiswho.R;
import com.whois.whoiswho.activity.MainActivityMethods;
import com.whois.whoiswho.utils.CountryManager;
import com.whois.whoiswho.utils.FontManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by stasenkopavel on 3/14/17.
 */

public class TermsOfConditionsFragment extends Fragment {

    @BindView(R.id.webView) WebView webView;

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_terms_of_comditions, container, false);
        ButterKnife.bind(this, view);
        FontManager.overrideFonts(view);
        webView.loadUrl("https://checkphone.info/tos");
        return view;
    }

    @OnClick(R.id.closeButton)
    public void close() {
        ((MainActivityMethods) getActivity()).back();
    }
}
