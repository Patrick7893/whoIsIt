package com.whois.whoiswho.screens.tutorial;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whois.whoiswho.R;
import com.whois.whoiswho.utils.FontManager;
import com.whois.whoiswho.utils.SharedPreferencesSaver;
import com.whois.whoiswho.view.CustomViewPager;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by stasenkopavel on 4/20/16.
 */
public class TutorialDialog extends DialogFragment {

    @BindView(R.id.tutorialViewPager) CustomViewPager viewPager;
    @BindView(R.id.positionTextView) TextView postionTextView;
    @BindView(R.id.nextTextView) TextView nextTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.screen_tutorial, container, false);
        FontManager.overrideFonts(view);
        ButterKnife.bind(this, view);
        setupViewPager(viewPager);
        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = null;
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new TutorialFirst(), "FIRST");
        //adapter.addFragment(new TutorialSecond(), "SECOND");
        adapter.addFragment(new TutorialThird(), "THIRD");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: {
                        postionTextView.setText(getString(R.string.tutorial1_number));
                        nextTextView.setText(getString(R.string.next));
                        break;
                    }
                    case 1: {
                        postionTextView.setText(getString(R.string.tutorial2_number));
                        nextTextView.setText(getString(R.string.ready));
                        break;
                    }
//                    case 2: {
//                        postionTextView.setText(getString(R.string.tutorial3_number));
//                        nextTextView.setText(getString(R.string.ready));
//                        break;
//                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.nextTextView)
    public void nextPage() {
        if (viewPager.getCurrentItem() != 1) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
        else {
            SharedPreferencesSaver.get().saveTutorialDone();
            this.dismiss();
        }
    }



    class ViewPagerAdapter extends FragmentStatePagerAdapter {
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


}
