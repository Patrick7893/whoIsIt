package com.whois.whoiswho.screens.mainscreen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.whois.whoiswho.R;
import com.whois.whoiswho.activity.MainActivity;
import com.whois.whoiswho.activity.MainActivityMethods;
import com.whois.whoiswho.screens.calls.CallFragment;
import com.whois.whoiswho.screens.findcontact.FindContactsFragment;
import com.whois.whoiswho.screens.spam.SpamFragment;
import com.whois.whoiswho.screens.tutorial.TutorialDialog;
import com.whois.whoiswho.utils.SharedPreferencesSaver;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        ((MainActivity)getActivity()).getUserInfo();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

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
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.flContent, new FindContactsFragment()).addToBackStack(null).commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @OnClick(R.id.menuButton)
    public void openDrawer() {
        ((MainActivityMethods)getActivity()).openDrawer();
    }

}
