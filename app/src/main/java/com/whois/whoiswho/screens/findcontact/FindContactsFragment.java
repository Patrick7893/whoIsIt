package com.whois.whoiswho.screens.findcontact;

import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.gson.Gson;
import com.whois.whoiswho.R;
import com.whois.whoiswho.activity.MainActivityMethods;
import com.whois.whoiswho.api.FindPhoneResponse;
import com.whois.whoiswho.model.Contact;
import com.whois.whoiswho.model.Phone;
import com.whois.whoiswho.screens.user_profile.UserProfileFragment;
import com.whois.whoiswho.utils.KeyboardManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 4/8/16.
 */
public class FindContactsFragment extends Fragment {

    @BindView(R.id.findContactEditText) EditText findContactsEditText;
    @BindView(R.id.findList) RecyclerView findConatctsRecyclerView;
    @BindView(R.id.progressBar) CircularProgressView progressBar;

    public static final String CONTACTINFO = "CONTACTINFO";
    public static final String ADAPTERSTATE = "ADAPTERSTATE";

    private static FindContactsPresenter presenter;
    private String countryIso;

    FindContactsAutocompliteAdapter autocompleteAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Parcelable adapterState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_findcontacts, container, false);
        ButterKnife.bind(this, view);
        presenter = new FindContactsPresenter(this);
        TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        countryIso = tm.getSimCountryIso();
        initiallizeScreen();

        return view;
    }

    private void initiallizeScreen() {
        //displayContacts();
        presenter.getContacts.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(contacts -> {
            autocompleteAdapter = new FindContactsAutocompliteAdapter(getActivity(), contacts, item -> goToUserProfileScreen(item));
            layoutManager = new LinearLayoutManager(getActivity());
            autocompleteAdapter.setEmptyAdapter();
            findConatctsRecyclerView.setLayoutManager(layoutManager);
            findConatctsRecyclerView.setAdapter(autocompleteAdapter);

            findContactsEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().length() > 0) {
                        findConatctsRecyclerView.setVisibility(View.VISIBLE);
                        autocompleteAdapter.setFilter(presenter.filter(contacts, s.toString()));
                    } else {
                        findConatctsRecyclerView.setVisibility(View.GONE);
                        autocompleteAdapter.setEmptyAdapter();
                    }
                }
            });
            findContactsEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!TextUtils.isEmpty(findContactsEditText.getText().toString()))
                        presenter.find(findContactsEditText.getText().toString(), countryIso);
                    return true;
                }
                return false;
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                findContactsEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher("UA"));
            }
        });

    }

    public void displayPhones(FindPhoneResponse findPhoneResponse) {
        ArrayList<Contact> contacts = new ArrayList<>();
        Collections.sort(findPhoneResponse.getData(), (lhs, rhs) -> lhs.getName().compareTo(rhs.getName()));
        for (Phone phone : findPhoneResponse.getData()) {
            contacts.add(new Contact().phoneToContact(phone));
        }
        autocompleteAdapter.addContactsFromServer(contacts);
    }

    private void goToUserProfileScreen(Contact contact) {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
        Gson gson = new Gson();
        String contactString = gson.toJson(contact);
        Bundle bundle = new Bundle();
        bundle.putString(CONTACTINFO, contactString);
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        userProfileFragment.setArguments(bundle);
        getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_from_right, R.animator.slide_out_to_left, R.animator.slide_in_from_left, R.animator.slide_out_to_right).add(R.id.flContent, userProfileFragment).addToBackStack(null).commit();
    }


    @OnClick(R.id.menuButton)
    public void openDrawer() {
        ((MainActivityMethods)getActivity()).openDrawer();
    }

    @OnClick(R.id.searchButton)
    public void back() {
        KeyboardManager.hideKeyboard(getActivity());
        ((MainActivityMethods)getActivity()).back();
    }

    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

}
