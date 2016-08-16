package com.whois.whoiswho.screens.call_story;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.whois.whoiswho.R;
import com.whois.whoiswho.activity.MainActivityMethods;
import com.whois.whoiswho.model.Call;
import com.whois.whoiswho.model.Contact;
import com.whois.whoiswho.screens.user_profile.UserProfileFragment;
import com.whois.whoiswho.utils.FontManager;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 4/19/16.
 */
public class CallStoryFragment extends Fragment {

    private static CallStoryPresenter presenter;

    private Contact contact;

    @BindView(R.id.callStoryRecyclerView) RecyclerView callStoryRecyclerView;
    @BindView(R.id.emptyListView) TextView emptyTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_call_story, container, false);
        ButterKnife.bind(this, view);
        FontManager.overrideFonts(view);
        presenter = new CallStoryPresenter(this);
        initiallizeScreen();
        return view;
    }

    private void initiallizeScreen() {
        Bundle bundle = getArguments();
        String contatcString = bundle.getString(UserProfileFragment.CONTACTINFO);
        Gson gson = new Gson();
        contact = gson.fromJson(contatcString, Contact.class);
        presenter.getCalls.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ArrayList<Call>>() {
            @Override
            public void call(ArrayList<Call> calls) {
                callStoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                callStoryRecyclerView.setAdapter(new CallStoryAdapter(getActivity(), calls, new CallStoryAdapter.onCallClickListener() {
                    @Override
                    public void callCLick(Call item) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.getNumber()));
                        startActivity(intent);
                    }
                }));
                if (callStoryRecyclerView.getAdapter().getItemCount() == 0) {
                    callStoryRecyclerView.setVisibility(View.GONE);
                    emptyTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @OnClick(R.id.closeButton)
    public void back() {
        ((MainActivityMethods)getActivity()).back();
    }

    public Contact getContact() {
        return contact;
    }
}
