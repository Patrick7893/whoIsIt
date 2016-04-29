package com.unteleported.truecaller.screens.tutorial;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.utils.FontManager;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by stasenkopavel on 4/20/16.
 */
public class TutorialDialog extends android.support.v4.app.DialogFragment {

    @Bind(R.id.tutorialViewPager) ViewPager viewPager;
    @Bind(R.id.positionTextView) TextView postionTextView;
    @Bind(R.id.nextTextView) TextView nextTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_tutorial, container, false);
        FontManager.overrideFonts(view);
        ButterKnife.bind(this, view);
        setupViewPager(viewPager);
        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new TutorialFirst(), "FIRST");
        adapter.addFragment(new TutorialSecond(), "SECOND");
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
                        nextTextView.setText(getString(R.string.next));
                        break;
                    }
                    case 2: {
                        postionTextView.setText(getString(R.string.tutorial3_number));
                        nextTextView.setText(getString(R.string.ready));
                        break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.nextTextView)
    public void nextPage() {
        if (viewPager.getCurrentItem() != 2) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
        else {
            SharedPreferencesSaver.get().saveTutorialDone();
            this.dismiss();
        }
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


}
