package com.whois.whoiswho.screens.call_story;

import com.whois.whoiswho.model.Call;
import com.whois.whoiswho.model.ContactNumber;
import com.whois.whoiswho.utils.ContactsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by stasenkopavel on 4/28/16.
 */
public class CallStoryPresenter {

    private CallStoryFragment view;

    public CallStoryPresenter (CallStoryFragment view) {
        this.view = view;
    }

    Observable<ArrayList<Call>> getCalls = Observable.create(new Observable.OnSubscribe<ArrayList<Call>>() {
        @Override
        public void call(Subscriber<? super ArrayList<Call>> subscriber) {
            ArrayList<Call> calls = new ArrayList<Call>();
            for (ContactNumber number : view.getContact().getNumbers()) {
                calls.addAll(ContactsManager.getCallListOfContact(view.getActivity(), number.getNumber()));
            }
            Collections.sort(calls, new Comparator<Call>() {
                @Override
                public int compare(Call lhs, Call rhs) {
                    return rhs.getDate().compareTo(lhs.getDate());
                }
            });
            subscriber.onNext(calls);
            subscriber.onCompleted();
        }
    });
}

