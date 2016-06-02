package com.unteleported.truecaller.screens.spam;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.unteleported.truecaller.R;
import com.unteleported.truecaller.api.FindPhoneResponse;
import com.unteleported.truecaller.api.GetSpammersResponse;
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.model.Phone_Table;
import com.unteleported.truecaller.screens.conatctslist.ContactslistFragment;
import com.unteleported.truecaller.screens.user_profile.UserProfileFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by stasenkopavel on 4/15/16.
 */
public class SpamFragment extends Fragment {

    @Bind(R.id.spamList) RecyclerView spamRecyclerView;
    @Bind(R.id.emptyTextView) TextView emptyTextView;
    @Bind(R.id.swiperefresh) SwipeRefreshLayout swipeRefreshLayout;

    private SpamPresenter presenter;
    private SpamAdapter spamAdapter;
    private ArrayList<Phone> localSpammers;

    SpamAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_spam, container, false);
        ButterKnife.bind(this, view);
        presenter = new SpamPresenter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        spamRecyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getLocalSpammers();
            }
        });
        presenter.getLocalSpammers();
        return view;
    }


    public void dispayLocalSpammers(CursorResult<Phone> tResult) {
        localSpammers = new ArrayList<>(tResult.toList());
        Log.d("SPAMMERS", "loaded");
        adapter = new SpamAdapter(localSpammers, new SpamAdapter.OnSpamClickListener() {
            @Override
            public void spamClick(Phone item) {
                goToContactInfoScreen(item);
            }
        });
        spamRecyclerView.setAdapter(adapter);
        presenter.getSpammers();
    }

    public void displaySpammersFromServer(GetSpammersResponse getSpammersResponse) {
        if (getSpammersResponse.getSpammersOfUser().size()>0) {
            for (Phone spammer : getSpammersResponse.getSpammersOfUser()) {
                spammer.setIsBlocked(true);
                spammer.save();
            }
            adapter.addSpammersFromServer(getSpammersResponse.getSpammersOfUser());
        }
        if (getSpammersResponse.getGlobalSpammers().size()>0) {
            adapter.addGlobalSpammers(getSpammersResponse.getGlobalSpammers());
        }
        if (getSpammersResponse.getGlobalSpammers().size()==0 && getSpammersResponse.getSpammersOfUser().size() == 0 && localSpammers.size() == 0) {
            emptyTextView.setVisibility(View.VISIBLE);
            spamRecyclerView.setVisibility(View.GONE);
        }
        swipeRefreshLayout.setRefreshing(false);
    }



    private void goToContactInfoScreen(Phone phone) {
        Contact contact = new Contact();
        contact.phoneToContact(phone);
        Gson gson = new Gson();
        String contactString = gson.toJson(contact);
        Bundle bundle = new Bundle();
        bundle.putString(ContactslistFragment.CONTACTINFO, contactString);
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        userProfileFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left, R.anim.slide_out_to_right).add(R.id.flContent, userProfileFragment).addToBackStack(null).commit();
    }

}
