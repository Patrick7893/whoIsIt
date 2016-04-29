package com.unteleported.truecaller.screens.call_story;

import com.unteleported.truecaller.model.Call;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.utils.UserContactsManager;

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
            for (Phone phone : view.getContact().getPhones()) {
                calls.addAll(UserContactsManager.getCallListOfContact(view.getActivity(), phone.getNumber()));
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

