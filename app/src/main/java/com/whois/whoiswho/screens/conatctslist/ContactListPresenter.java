package com.whois.whoiswho.screens.conatctslist;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.gson.Gson;
import com.whois.whoiswho.R;
import com.whois.whoiswho.app.App;
import com.whois.whoiswho.model.Contact;
import com.whois.whoiswho.screens.calls.CallFragment;
import com.whois.whoiswho.screens.user_profile.UserProfileFragment;
import com.whois.whoiswho.utils.ContactsManager;
import com.whois.whoiswho.utils.KeyboardManager;
import com.whois.whoiswho.utils.PermissionManager;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

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
        KeyboardManager.hideKeyboard(view.getActivity());
       // view.getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left, R.anim.slide_out_to_right).add(R.id.flContent, userProfileFragment).addToBackStack(null).commit();
        view.getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_from_right, R.animator.slide_out_to_left, R.animator.slide_in_from_left, R.animator.slide_out_to_right).add(R.id.flContent, userProfileFragment).addToBackStack(null).commit();
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

    Observable<ArrayList<Contact>> getContacts = Observable.create(new Observable.OnSubscribe<ArrayList<Contact>>() {
        @Override
        public void call(Subscriber<? super ArrayList<Contact>> subscriber) {
            if (ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                ArrayList<Contact> contacts = ContactsManager.readContacts(view.getActivity(), view.getArguments().getBoolean(CallFragment.ISFAVOURITECONTACTS));
                subscriber.onNext(contacts);
                subscriber.onCompleted();
            }
            else {
                PermissionManager.requestPermissions(view.getActivity(), Manifest.permission.READ_CONTACTS);
            }

        }
    });


}
