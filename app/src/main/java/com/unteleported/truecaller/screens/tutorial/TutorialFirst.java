package com.unteleported.truecaller.screens.tutorial;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.utils.FontManager;

/**
 * Created by stasenkopavel on 4/20/16.
 */
public class TutorialFirst extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_tutorial_first, container, false);
        FontManager.overrideFonts(view);
        return view;
    }
}
