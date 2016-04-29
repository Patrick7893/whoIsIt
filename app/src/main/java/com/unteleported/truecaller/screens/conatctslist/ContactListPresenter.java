package com.unteleported.truecaller.screens.conatctslist;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;

import com.google.gson.Gson;
import com.unteleported.truecaller.activity.MainActivityMethods;
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.screens.user_profile.UserProfileFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stasenkopavel on 4/28/16.
 */
public class ContactListPresenter {

    private ContactslistFragment view;

    public ContactListPresenter(ContactslistFragment view) {
        this.view = view;
    }

    public void goToUserProfileScreen(Contact contact) {
        Gson gson = new Gson();
        String contactString = gson.toJson(contact);
        Bundle bundle = new Bundle();
        bundle.putString(view.CONTACTINFO, contactString);
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        userProfileFragment.setArguments(bundle);
        ((MainActivityMethods) view.getActivity()).switchFragment(userProfileFragment);
    }

    public List<Contact> filter(List<Contact> models, String query) {
        query = query.toLowerCase();

        final List<Contact> filteredModelList = new ArrayList<>();
        for (Contact contact : models) {
            final String text = contact.getName().toLowerCase();
            if (text.startsWith(query)) {
                filteredModelList.add(contact);
            }
        }
        return filteredModelList;
    }

    public void initSearchView(final ArrayList<Contact> contacts, final ContactsAdapter contactsAdapter) {
        view.searchView.setFocusable(true);
        view.searchView.setIconified(false);
        view.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<Contact> filteredModelList = filter(contacts, newText);
                contactsAdapter.setFilter(filteredModelList);
                return true;
            }
        });
        view.searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                view.searchView.setVisibility(View.GONE);
                view.searchButton.setVisibility(View.VISIBLE);
                view.titleTextView.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }
}
