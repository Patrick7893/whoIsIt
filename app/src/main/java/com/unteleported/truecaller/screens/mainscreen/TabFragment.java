package com.unteleported.truecaller.screens.mainscreen;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.activity.MainActivityMethods;
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.screens.calls.CallFragment;
import com.unteleported.truecaller.screens.findcontact.FindContactsFragment;
import com.unteleported.truecaller.screens.spam.SpamFragment;
import com.unteleported.truecaller.screens.tutorial.TutorialDialog;
import com.unteleported.truecaller.utils.CountryManager;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;
import com.unteleported.truecaller.utils.UserContactsManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 4/8/16.
 */
public class TabFragment extends Fragment {

    @Bind(R.id.tabs) TabLayout tabLayout;
    @Bind(R.id.viewpager) ViewPager viewPager;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_tabs, container, false);
        ButterKnife.bind(this, view);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        final TabFragmentPresenter presenter = new TabFragmentPresenter(this);
        ((MainActivityMethods)getActivity()).enableDrawer();
        ((MainActivityMethods)getActivity()).setUserInfo();
        presenter.loadContatcs();

        if (!SharedPreferencesSaver.get().getTutorialDone()) {
            TutorialDialog tutorialDialog = new TutorialDialog();
            tutorialDialog.setCancelable(false);
            tutorialDialog.show(getActivity().getSupportFragmentManager(), "tutorial");
        }

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new CallFragment(), getString(R.string.calls));
        adapter.addFragment(new SpamFragment(), getString(R.string.spam));
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @OnClick(R.id.searchButton)
    public void goToSearchScreen() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent, new FindContactsFragment()).addToBackStack(null).commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @OnClick(R.id.menuButton)
    public void openDrawer() {
        ((MainActivityMethods)getActivity()).openDrawer();
    }

    Observable<ArrayList<Contact>> getContacts = Observable.create(new Observable.OnSubscribe<ArrayList<Contact>>() {
        @Override
        public void call(Subscriber<? super ArrayList<Contact>> subscriber) {
            ArrayList<Contact> contacts = UserContactsManager.readContacts(getActivity(), false);
            subscriber.onNext(contacts);
            subscriber.onCompleted();
        }
    });

}
