package com.whois.whoiswho.screens.calls;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;
import com.whois.whoiswho.R;
import com.whois.whoiswho.app.App;
import com.whois.whoiswho.model.Call;
import com.whois.whoiswho.model.Contact;
import com.whois.whoiswho.model.ContactNumber;
import com.whois.whoiswho.screens.numpad.NumpadFragment;
import com.whois.whoiswho.screens.user_profile.UserProfileFragment;
import com.whois.whoiswho.utils.CountryManager;
import com.whois.whoiswho.utils.PermissionManager;
import com.whois.whoiswho.utils.ContactsManager;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by stasenkopavel on 4/28/16.
 */
public class CallsPresenter {

    private CallFragment view;

    private NumpadFragment numpadFragment;


    public CallsPresenter(CallFragment view) {
        this.view = view;
    }

     void goToUserProfileScreen(Call call) {
        Contact contact = new Contact();
        contact.setName(call.getName());
        ArrayList<ContactNumber> phones = new ArrayList<>();
        ContactNumber contactNumber = new ContactNumber(call.getNumber(), call.getType());
         contactNumber.setTypeDescription(call.getTypeOfNumber());
         if (call.getNumber().startsWith("+")) {
             contactNumber.setCountryIso(CountryManager.getIsoFromPhone(call.getNumber()));
         }
         else {
             TelephonyManager tMgr = (TelephonyManager)view.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
             contactNumber.setCountryIso(tMgr.getSimCountryIso().toUpperCase());
         }
        phones.add(contactNumber);
        contact.setNumbers(phones);
        Gson gson = new Gson();
        String contactString = gson.toJson(contact);
        Bundle bundle = new Bundle();
        bundle.putString(view.CONTACTINFO, contactString);
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        userProfileFragment.setArguments(bundle);
        //view.getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left, R.anim.slide_out_to_right).add(R.id.flContent, userProfileFragment).addToBackStack(null).commit();
         view.getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_from_right, R.animator.slide_out_to_left, R.animator.slide_in_from_left, R.animator.slide_out_to_right).add(R.id.flContent, userProfileFragment).addToBackStack(null).commit();
    }

    Observable<ArrayList<Call>> getCalls = Observable.create(new Observable.OnSubscribe<ArrayList<Call>>() {
        @Override
        public void call(Subscriber<? super ArrayList<Call>> subscriber) {
                view.calls = ContactsManager.getUserCallsList(view.getActivity());
                subscriber.onNext(view.calls);
                subscriber.onCompleted();
        }
    });

}

