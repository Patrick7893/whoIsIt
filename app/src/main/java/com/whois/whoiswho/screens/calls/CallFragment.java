package com.whois.whoiswho.screens.calls;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.whois.whoiswho.R;
import com.whois.whoiswho.activity.MainActivity;
import com.whois.whoiswho.activity.MainActivityMethods;
import com.whois.whoiswho.app.App;
import com.whois.whoiswho.model.Call;
import com.whois.whoiswho.screens.conatctslist.ContactslistFragment;

import com.whois.whoiswho.screens.numpad.NumpadFragment;
import com.whois.whoiswho.utils.PermissionManager;


import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 4/8/16.
 */
public class CallFragment extends Fragment implements NumpadFragment.OnPhonePrsesentListener, ContactslistFragment.OnContactsDetachListener {

    @BindView(R.id.callsList) RecyclerView callsRecyclerView;
    @BindView(R.id.contactsImageView) ImageView contactsImageView;
    @BindView(R.id.favouiteContacts) ImageView favouriteContactsImageView;
    @BindView(R.id.numpadImageView) ImageView numPadImageView;

    public final static String ISFAVOURITECONTACTS = "isFavourite";
    public static final String CONTACTINFO = "CONTACTINFO";

    private static CallsPresenter presenter;

    ArrayList<Call> calls;
    private CallsAdapter callsAdapter;
    private boolean keyBoardPresent = false, canCall = false, contatcsPresent = false;
    private String number;
    private NumpadFragment numpadFragment;
    private ContactslistFragment contactslistFragment;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_calls, container, false);
        ButterKnife.bind(this, view);
        contactsImageView.setOnClickListener(onClickListener);
        favouriteContactsImageView.setOnClickListener(onClickListener);
        numPadImageView.setOnClickListener(onClickListener);
        presenter = new CallsPresenter(this);
        initiallizeScreen();
        getActivity().registerReceiver(callEndedBroadcastReceiver, new IntentFilter("CallEnd"));
        return view;
    }

    public void initiallizeScreen() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        callsRecyclerView.setLayoutManager(layoutManager);
        presenter.getCalls.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ArrayList<Call>>() {
            @Override
            public void call(ArrayList<Call> calls) {
                callsAdapter = new CallsAdapter(getActivity(), calls, new CallsAdapter.onCallsClickListener() {
                    @Override
                    public void callCLick(Call item) {
                        if (keyBoardPresent) {
                            numPadImageView.setImageDrawable(getResources().getDrawable(R.drawable.keypad_icon));
                            hideKeyBoard();
                            keyBoardPresent = false;
                        }
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.getNumber()));
                        startActivity(intent);
                    }

                    @Override
                    public void infoClick(Call item) {
                        presenter.goToUserProfileScreen(item);
                        //find(item.getNumber());
                    }
                });
                callsRecyclerView.setAdapter(callsAdapter);
                callsRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (keyBoardPresent) {
                            hideKeyBoard();
                            keyBoardPresent = false;
                        }
                    }
                });
            }
        });
    }

    View.OnClickListener onClickListener  = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.contactsImageView) {
                if (!contatcsPresent) {
                    goToContatcs(false);
                    contatcsPresent = true;
                }
                else {
                    contatcsPresent = false;
                    contactsImageView.setImageDrawable(getResources().getDrawable(R.drawable.contacts_icon));
                    ((MainActivityMethods)getActivity()).back();
                }
            }
            if (id == R.id.favouiteContacts) {
                if (!contatcsPresent) {
                    goToContatcs(true);
                    contatcsPresent = true;
                }
                else {
                    contatcsPresent = false;
                    favouriteContactsImageView.setImageDrawable(getResources().getDrawable(R.drawable.favourite_contacts));
                    ((MainActivityMethods)getActivity()).back();
                }
            }
            if (id == R.id.numpadImageView) {
                if (!keyBoardPresent) {
                    showKeyBoard();
                }
                else {
                    if (canCall) {
                        if (ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                            hideKeyBoard();
                            startActivity(intent);
                        }
                        else {
                            PermissionManager.requestPermissions(getActivity(), Manifest.permission.CALL_PHONE);
                        }
                    }
                    else {
                        numPadImageView.setImageDrawable(getResources().getDrawable(R.drawable.keypad_icon));
                        hideKeyBoard();
                        keyBoardPresent = false;
                    }
                }
            }

        }
    };

    private BroadcastReceiver callEndedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            presenter.getCalls.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ArrayList<Call>>() {
                @Override
                public void call(ArrayList<Call> calls) {
                    callsAdapter.notify(calls);
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(callEndedBroadcastReceiver);
    }

    @Override
    public void canCall(String number) {
        canCall = true;
        this.number = number;
        numPadImageView.setImageDrawable(getResources().getDrawable(R.drawable.phone));
    }

    @Override
    public void numberAbsent() {
        canCall = false;
        numPadImageView.setImageDrawable(getResources().getDrawable(R.drawable.keypad_filled));
    }

    @Override
    public void keyBoardDestroyed() {
        keyBoardPresent = false;
        canCall = false;
        numPadImageView.setImageDrawable(getResources().getDrawable(R.drawable.keypad_icon));
    }

    @Override
    public void onDetachContacts(boolean isFavourite) {
        contatcsPresent = false;
        if (isFavourite) {
            favouriteContactsImageView.setImageDrawable(getResources().getDrawable(R.drawable.favourite_contacts));
        }
        else {
            contactsImageView.setImageDrawable(getResources().getDrawable(R.drawable.contacts_icon));
        }
    }

    void showKeyBoard() {
        numPadImageView.setImageDrawable(getResources().getDrawable(R.drawable.keypad_filled));
        if (isContatcsPresent()) {
            getActivity().getFragmentManager().beginTransaction().setCustomAnimations(0, 0, 0, R.animator.slide_out_to_top).remove(contactslistFragment).commit();
            getActivity().getFragmentManager().popBackStack();
        }
        numpadFragment = new NumpadFragment();
        numpadFragment.setOnPhonePresentListener(this);
        getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_from_bottom, R.animator.slide_out_to_top, R.animator.slide_in_from_bottom, R.animator.slide_out_to_top).add(R.id.numPadContainer, numpadFragment).addToBackStack(null).commit();
        setKeyBoardPresent(true);
    }

    void hideKeyBoard() {
        ((MainActivity)getActivity()).back();
    }

    void goToContatcs(boolean isFavourite) {
        if (isKeyBoardPresent()) {
            hideKeyBoard();
        }
        contactslistFragment = new ContactslistFragment();
        contactslistFragment.setOnContatcsDetachListener(this);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ISFAVOURITECONTACTS, isFavourite);
        contactslistFragment.setArguments(bundle);
        if (isFavourite) {
            favouriteContactsImageView.setImageDrawable(getResources().getDrawable(R.drawable.like_filled));
        }
        else {
            contactsImageView.setImageDrawable(getResources().getDrawable(R.drawable.contact_filled));
        }
        ((MainActivity) getActivity()).swithConatcsFragment(contactslistFragment);
    }

    public boolean isKeyBoardPresent() {
        return keyBoardPresent;
    }

    public void setKeyBoardPresent(boolean keyBoardPresent) {
        this.keyBoardPresent = keyBoardPresent;
    }

    public boolean isContatcsPresent() {
        return contatcsPresent;
    }

    public void setContatcsPresent(boolean contatcsPresent) {
        this.contatcsPresent = contatcsPresent;
    }
}
