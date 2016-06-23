package com.unteleported.truecaller.activity;

import android.support.v4.app.Fragment;

/**
 * Created by stasenkopavel on 4/8/16.
 */
public interface MainActivityMethods {
    //переключение фрагмента
    void switchFragment(Fragment fragment);
    void switchFragment(Fragment fragment, boolean addToFragmentList);
    void swithConatcsFragment(Fragment fragment);

    void removeFragment(Fragment fragment);
    void back();

    void openDrawer();
    void enableDrawer();
    void disableDrawer();
}
