package com.unteleported.truecaller.screens.findcontact;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.unteleported.truecaller.R;
import com.unteleported.truecaller.activity.MainActivityMethods;
import com.unteleported.truecaller.api.FindPhoneResponse;
import com.unteleported.truecaller.model.Call;
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.screens.calls.CallFragment;
import com.unteleported.truecaller.screens.conatctslist.ContactsAdapter;
import com.unteleported.truecaller.screens.user_profile.UserProfileFragment;
import com.unteleported.truecaller.utils.UserContactsManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 4/8/16.
 */
public class FindContactsFragment extends Fragment {

    @Bind(R.id.findContactEditText) EditText findContactsEditText;
    @Bind(R.id.findList) RecyclerView findConatctsRecyclerView;

    public static final String CONTACTINFO = "CONTACTINFO";
    public static final String ADAPTERSTATE = "ADAPTERSTATE";

    private static FindContactsPresenter presenter;

    FindContactsAutocompliteAdapter autocompleteAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Parcelable adapterState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_findcontacts, container, false);
        ButterKnife.bind(this, view);
        presenter = new FindContactsPresenter(this);
        initiallizeScreen();

        return view;
    }

    private void initiallizeScreen() {
        //displayContacts();
        presenter.getContacts.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ArrayList<Contact>>() {
            @Override
            public void call(final ArrayList<Contact> contacts) {
                autocompleteAdapter = new FindContactsAutocompliteAdapter(getActivity(), contacts, new FindContactsAutocompliteAdapter.OnContactsClickListener() {
                    @Override
                    public void infoClick(Contact item) {
                        presenter.goToUserProfileScreen(item);
                    }
                });
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
                findContactsEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            presenter.find(findContactsEditText.getText().toString());
                            return true;
                        }
                        return false;
                    }
                });
            }

        });

    }

    public void displayPhones(FindPhoneResponse findPhoneResponse) {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        for (Phone phone : findPhoneResponse.getData()) {
            contacts.add(new Contact().phoneToContact(phone));
        }
        autocompleteAdapter.addContactsFromServer(contacts);
    }


    @OnClick(R.id.menuButton)
    public void openDrawer() {
        ((MainActivityMethods)getActivity()).openDrawer();
    }

}
