package com.unteleported.truecaller.screens.spam;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.unteleported.truecaller.R;
import com.unteleported.truecaller.api.FindPhoneResponse;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.model.Phone_Table;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by stasenkopavel on 4/15/16.
 */
public class SpamFragment extends Fragment {

    @Bind(R.id.spamList) RecyclerView spamRecyclerView;
    @Bind(R.id.emptyTextView) TextView emptyTextView;

    private SpamPresenter presenter;

    SpamAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_spam, container, false);
        ButterKnife.bind(this, view);
        presenter = new SpamPresenter(this);
        initiallizeScreen();
        return view;
    }

    private void initiallizeScreen() {
        List<Phone> spamPhones = new Select().from(Phone.class).where(Phone_Table.isBlocked.is(true)).queryList();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        spamRecyclerView.setLayoutManager(layoutManager);
        adapter = new SpamAdapter(new ArrayList<Phone>(spamPhones));
        spamRecyclerView.setAdapter(adapter);
        if (spamRecyclerView.getAdapter().getItemCount() == 0) {
            spamRecyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        }
        presenter.getSpammers();
    }

    public void displaySpammersFromServer(FindPhoneResponse findPhoneResponse) {
        if (findPhoneResponse.getData().size()>0) {
            adapter.addSpammersFromServer(findPhoneResponse.getData());
            emptyTextView.setVisibility(View.GONE);
            spamRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
