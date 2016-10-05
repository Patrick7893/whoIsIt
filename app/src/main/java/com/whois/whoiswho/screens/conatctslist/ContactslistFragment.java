package com.whois.whoiswho.screens.conatctslist;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whois.whoiswho.R;
import com.whois.whoiswho.activity.MainActivityMethods;
import com.whois.whoiswho.app.App;
import com.whois.whoiswho.model.Contact;
import com.whois.whoiswho.screens.calls.CallFragment;
import com.whois.whoiswho.utils.KeyboardManager;
import com.whois.whoiswho.utils.PermissionManager;
import com.whois.whoiswho.utils.ContactsManager;
import com.whois.whoiswho.view.FastScroller;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
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

    @BindView(R.id.contactsListView) RecyclerView contactsRecyclerView;
    @BindView(R.id.fastscroller) FastScroller fastScroller;
    @BindView(R.id.titleTextView) TextView titleTextView;
    @BindView(R.id.searchButton) ImageView searchButton;
    @BindView(R.id.searchView) SearchView searchView;
    @BindView(R.id.allowContactsLayout) LinearLayout allowConatctsLayout;

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
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (getIsFavourite()) {
            titleTextView.setText(getString(R.string.favorites));
            searchButton.setVisibility(View.GONE);
        }

        if (!getIsFavourite())
            initiallizeScreenAllContacts();
        else
            initiallizeScreenFavouriteContacts();

        return view;
    }


    private boolean getIsFavourite() {
        return this.getArguments().getBoolean(CallFragment.ISFAVOURITECONTACTS);
    }


    public void setAllowContactsLayoutVisible() {
        contactsRecyclerView.setVisibility(View.GONE);
        allowConatctsLayout.setVisibility(View.VISIBLE);
    }



    public void initSearchView(final ArrayList<Contact> contacts, final ContactsAdapter contactsAdapter) {
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<Contact> filteredModelList = presenter.filter(contacts, newText);
                contactsAdapter.setFilter(filteredModelList);
                return true;
            }
        });
        searchView.setOnCloseListener(() -> {
            searchView.setVisibility(View.GONE);
            searchButton.setVisibility(View.VISIBLE);
            titleTextView.setVisibility(View.VISIBLE);
            return false;
        });
        searchView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(v, 0);
                }
            }
        });
    }

    public void initiallizeScreenAllContacts() {
        if (ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            presenter.getContacts.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ArrayList<Contact>>() {
                @Override
                public void call(ArrayList<Contact> contacts) {
                    final ContactsAdapter contactsAdapter = new ContactsAdapter(getActivity(), contacts, new ContactsAdapter.OnContactsClickListener() {
                        @Override
                        public void callClick(Contact item) {
                            if (item.getNumbers().size() < 2) {
                                if (ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.getNumbers().get(0).getNumber()));
                                    startActivity(intent);
                                } else {
                                    PermissionManager.requestPermissions(getActivity(), Manifest.permission.CALL_PHONE);
                                }

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
                    initSearchView(contacts, contactsAdapter);
                    searchView.clearFocus();
                }

            });
        }
        else {
            setAllowContactsLayoutVisible();
        }
    }

    public void initiallizeScreenFavouriteContacts() {
        titleTextView.setText(getString(R.string.favorites));
        searchButton.setVisibility(View.GONE);
        searchView.clearFocus();
        if (ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            presenter.getContacts.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(contacts -> {
                final ContactsAdapter contactsAdapter = new ContactsAdapter(getActivity(), contacts, new ContactsAdapter.OnContactsClickListener() {
                    @Override
                    public void callClick(Contact item) {
                        if (ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.getNumbers().get(0).getNumber()));
                            startActivity(intent);
                        } else {
                            PermissionManager.requestPermissions(getActivity(), Manifest.permission.CALL_PHONE);
                        }

                    }

                    @Override
                    public void infoClick(Contact item) {
                        presenter.goToUserProfileScreen(item);
                    }
                });
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                contactsRecyclerView.setLayoutManager(layoutManager);
                contactsRecyclerView.setAdapter(contactsAdapter);
            });
        }
        else {
            setAllowContactsLayoutVisible();
        }
    }

    public void setOnContatcsDetachListener(OnContactsDetachListener onContatcsDetachListener) {
        this.onContactsDetachListener = onContatcsDetachListener;
    }



    @OnClick(R.id.closeButton)
    public void close() {
        KeyboardManager.hideKeyboard(getActivity());
        ((MainActivityMethods) getActivity()).back();
    }

    @OnClick(R.id.givePermissionButton)
    public void requestPermission() {
        PermissionManager.requestPermissions(getActivity(), Manifest.permission.READ_CONTACTS);
    }

    @OnClick(R.id.searchButton)
    public void showSearchView() {
        searchButton.setVisibility(View.GONE);
        titleTextView.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.requestFocus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Boolean isFavourite = getArguments().getBoolean(CallFragment.ISFAVOURITECONTACTS);
        if (onContactsDetachListener != null)
            onContactsDetachListener.onDetachContacts(isFavourite);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}


