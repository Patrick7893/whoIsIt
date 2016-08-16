package com.whois.whoiswho.screens.tutorial;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whois.whoiswho.R;
import com.whois.whoiswho.utils.FontManager;

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
