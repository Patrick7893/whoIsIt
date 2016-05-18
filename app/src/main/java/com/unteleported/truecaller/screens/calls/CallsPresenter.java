package com.unteleported.truecaller.screens.calls;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;
import com.unteleported.truecaller.R;
import com.unteleported.truecaller.activity.MainActivity;
import com.unteleported.truecaller.activity.MainActivityMethods;
import com.unteleported.truecaller.model.Call;
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.screens.conatctslist.ContactslistFragment;
import com.unteleported.truecaller.screens.numpad.NumpadFragment;
import com.unteleported.truecaller.screens.user_profile.UserProfileFragment;
import com.unteleported.truecaller.utils.CountryManager;
import com.unteleported.truecaller.utils.UserContactsManager;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by stasenkopavel on 4/28/16.
 */
public class CallsPresenter {

    private CallFragment view;

    private NumpadFragment numpadFragment;
    private ContactslistFragment contactslistFragment;

    public CallsPresenter(CallFragment view) {
        this.view = view;
    }

     void goToUserProfileScreen(Call call) {
        Contact contact = new Contact();
        contact.setName(call.getName());
        ArrayList<Phone> phones = new ArrayList<>();
        Phone phone = new Phone(call.getNumber(), call.getType());
        phone.setTypeDescription(call.getTypeOfNumber());
         if (call.getNumber().startsWith("+")) {
             phone.setCountryIso(CountryManager.getIsoFromPhone(call.getNumber()));
         }
         else {
             TelephonyManager tMgr = (TelephonyManager)view.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
             phone.setCountryIso(tMgr.getSimCountryIso().toUpperCase());
         }
        phones.add(phone);
        contact.setPhones(phones);
        Gson gson = new Gson();
        String contactString = gson.toJson(contact);
        Bundle bundle = new Bundle();
        bundle.putString(view.CONTACTINFO, contactString);
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        userProfileFragment.setArguments(bundle);
        view.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent, userProfileFragment).addToBackStack(null).commit();
    }

    Observable<ArrayList<Call>> getCalls = Observable.create(new Observable.OnSubscribe<ArrayList<Call>>() {
        @Override
        public void call(Subscriber<? super ArrayList<Call>> subscriber) {
            view.calls = UserContactsManager.getUserCallsList(view.getActivity());
            subscriber.onNext(view.calls);
            subscriber.onCompleted();
        }
    });

     void showKeyBoard() {
        view.numPadImageView.setImageDrawable(view.getResources().getDrawable(R.drawable.keypad_filled));
        if (view.isContatcsPresent()) {
            view.getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).remove(contactslistFragment).commit();
            view.getActivity().getSupportFragmentManager().popBackStack();
        }
        numpadFragment = new NumpadFragment();
        numpadFragment.setOnPhonePresentListener(view);
        view.getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.numPadContainer, numpadFragment).addToBackStack(null).commit();
        view.setKeyBoardPresent(true);
    }

     void hideKeyBoard() {
        view.getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).remove(numpadFragment).commit();
        view.getActivity().getSupportFragmentManager().popBackStack();
    }

     void goToContatcs(boolean isFavourite) {
        if (view.isKeyBoardPresent()) {
            hideKeyBoard();
        }
        contactslistFragment = new ContactslistFragment();
        contactslistFragment.setOnContatcsDetachListener(view);
        Bundle bundle = new Bundle();
        bundle.putBoolean(view.ISFAVOURITECONTACTS, isFavourite);
        contactslistFragment.setArguments(bundle);
        if (isFavourite) {
            view.favouriteContactsImageView.setImageDrawable(view.getResources().getDrawable(R.drawable.like_filled));
        }
        else {
            view.contactsImageView.setImageDrawable(view.getResources().getDrawable(R.drawable.contact_filled));
        }
        ((MainActivity) view.getActivity()).swithConatcsFragment(contactslistFragment);
    }
}

