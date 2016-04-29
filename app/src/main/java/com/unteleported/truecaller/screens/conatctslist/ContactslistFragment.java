package com.unteleported.truecaller.screens.conatctslist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.activity.MainActivityMethods;
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.screens.calls.CallFragment;
import com.unteleported.truecaller.utils.UserContactsManager;
import com.unteleported.truecaller.view.FastScroller;

import java.util.ArrayList;

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
public class ContactslistFragment extends Fragment {

    @Bind(R.id.contactsListView) RecyclerView contactsRecyclerView;
    @Bind(R.id.fastscroller) FastScroller fastScroller;
    @Bind(R.id.titleTextView) TextView titleTextView;
    @Bind(R.id.searchButton) ImageView searchButton;
    @Bind(R.id.searchView) SearchView searchView;

    private static ContactListPresenter presenter;

    public interface OnContactsDetachListener {
        void onDetachContacts(boolean isFavourite);
    }

    public static final String CONTACTINFO = "CONTACTINFO";
    private OnContactsDetachListener onContactsDetachListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_contact_list, container, false);
        ButterKnife.bind(this, view);
        presenter = new ContactListPresenter(this);
        if (!this.getArguments().getBoolean(CallFragment.ISFAVOURITECONTACTS)) {
            initiallizeScreenAllContacts();
        } else {
            initiallizeScreenFavouriteContacts();
        }

        return view;
    }

    Observable<ArrayList<Contact>> getContacts = Observable.create(new Observable.OnSubscribe<ArrayList<Contact>>() {
        @Override
        public void call(Subscriber<? super ArrayList<Contact>> subscriber) {
            ArrayList<Contact> contacts = UserContactsManager.readContacts(getActivity(), getArguments().getBoolean(CallFragment.ISFAVOURITECONTACTS));
            subscriber.onNext(contacts);
            subscriber.onCompleted();
        }
    });

    public void initiallizeScreenAllContacts() {
        getContacts.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ArrayList<Contact>>() {
            @Override
            public void call(ArrayList<Contact> contacts) {
                final ContactsAdapter contactsAdapter = new ContactsAdapter(getActivity(), contacts, new ContactsAdapter.OnContactsClickListener() {
                    @Override
                    public void callClick(Contact item) {
                        if (item.getPhones().size() < 2) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.getPhones().get(0).getNumber()));
                            startActivity(intent);
                        } else {
                            presenter.goToUserProfileScreen(item);
                        }
                    }

                    @Override
                    public void infoClick(Contact item) {
                        presenter.goToUserProfileScreen(item);
                    }

                });
                contactsRecyclerView.setAdapter(contactsAdapter);
                contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
                    @Override
                    public void onLayoutChildren(final RecyclerView.Recycler recycler, final RecyclerView.State state) {
                        super.onLayoutChildren(recycler, state);
                        //TODO if the items are filtered, considered hiding the fast scroller here
                        final int firstVisibleItemPosition = findFirstVisibleItemPosition();
                        if (firstVisibleItemPosition != 0) {
                            // this avoids trying to handle un-needed calls
                            if (firstVisibleItemPosition == -1)
                                //not initialized, or no items shown, so hide fast-scroller
                                fastScroller.setVisibility(View.GONE);
                            return;
                        }
                        final int lastVisibleItemPosition = findLastVisibleItemPosition();
                        int itemsShown = lastVisibleItemPosition - firstVisibleItemPosition + 1;
                        //if all items are shown, hide the fast-scroller
                        fastScroller.setVisibility(contactsAdapter.getItemCount() > itemsShown ? View.VISIBLE : View.GONE);
                    }
                });
                fastScroller.setRecyclerView(contactsRecyclerView);
                fastScroller.setViewsToUse(R.layout.view_fastscroller, R.id.fastscroller_bubble, R.id.fastscroller_handle);
                presenter.initSearchView(contacts, contactsAdapter);
                searchView.clearFocus();
            }
        });

    }

    public void initiallizeScreenFavouriteContacts() {
        titleTextView.setText(getString(R.string.favorites));
        searchButton.setVisibility(View.GONE);
        searchView.clearFocus();
        getContacts.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ArrayList<Contact>>() {
            @Override
            public void call(ArrayList<Contact> contacts) {
                final ContactsAdapter contactsAdapter = new ContactsAdapter(getActivity(), contacts, new ContactsAdapter.OnContactsClickListener() {
                    @Override
                    public void callClick(Contact item) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.getPhones().get(0).getNumber()));
                        startActivity(intent);
                    }

                    @Override
                    public void infoClick(Contact item) {
                        presenter.goToUserProfileScreen(item);
                    }
                });
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                contactsRecyclerView.setLayoutManager(layoutManager);
                contactsRecyclerView.setAdapter(contactsAdapter);
            }
        });
    }

    public void setOnContatcsDetachListener(OnContactsDetachListener onContatcsDetachListener) {
        this.onContactsDetachListener = onContatcsDetachListener;
    }



    @OnClick(R.id.closeButton)
    public void close() {
        ((MainActivityMethods) getActivity()).back();
    }

    @OnClick(R.id.searchButton)
    public void showSearchView() {
        searchButton.setVisibility(View.GONE);
        titleTextView.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
        searchView.requestFocusFromTouch();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onContactsDetachListener.onDetachContacts(this.getArguments().getBoolean(CallFragment.ISFAVOURITECONTACTS));
    }
}


