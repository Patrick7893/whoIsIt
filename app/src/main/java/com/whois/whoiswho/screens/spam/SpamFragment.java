package com.whois.whoiswho.screens.spam;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.whois.whoiswho.R;
import com.whois.whoiswho.api.GetSpammersResponse;
import com.whois.whoiswho.model.Contact;
import com.whois.whoiswho.model.Phone;
import com.whois.whoiswho.screens.conatctslist.ContactslistFragment;
import com.whois.whoiswho.screens.user_profile.UserProfileFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by stasenkopavel on 4/15/16.
 */
public class SpamFragment extends Fragment {

    @BindView(R.id.spamList) RecyclerView spamRecyclerView;
    @BindView(R.id.emptyTextView) TextView emptyTextView;
    @BindView(R.id.swiperefresh) SwipeRefreshLayout swipeRefreshLayout;

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
        if (getSpammersResponse.getUserSpammers().size()>0) {
            for (Phone spammer : getSpammersResponse.getUserSpammers()) {
                spammer.setIsBlocked(true);
                spammer.save();
            }
            adapter.addSpammersFromServer(getSpammersResponse.getUserSpammers());
        }
        if (getSpammersResponse.getGlobalSpammers().size()>0) {
            adapter.addGlobalSpammers(getSpammersResponse.getGlobalSpammers());
        }
        if (getSpammersResponse.getGlobalSpammers().size()==0 && getSpammersResponse.getUserSpammers().size() == 0 && localSpammers.size() == 0) {
            emptyTextView.setVisibility(View.VISIBLE);
            spamRecyclerView.setVisibility(View.GONE);
        }
        else {
            emptyTextView.setVisibility(View.GONE);
            spamRecyclerView.setVisibility(View.VISIBLE);
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
       // getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left, R.anim.slide_out_to_right).add(R.id.flContent, userProfileFragment).addToBackStack(null).commit();
         getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_from_right, R.animator.slide_out_to_left, R.animator.slide_in_from_left, R.animator.slide_out_to_right).add(R.id.flContent, userProfileFragment).addToBackStack(null).commit();
    }

}
