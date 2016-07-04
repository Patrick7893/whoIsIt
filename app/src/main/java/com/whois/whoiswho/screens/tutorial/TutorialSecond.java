package com.whois.whoiswho.screens.tutorial;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whois.whoiswho.R;
import com.whois.whoiswho.utils.FontManager;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by stasenkopavel on 4/20/16.
 */
public class TutorialSecond extends Fragment {

    @Bind(R.id.tutorial2TextView) TextView tutorial2TextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_tutorial_second, container, false);
        FontManager.overrideFonts(view);
        ButterKnife.bind(this, view);
        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString tutorial2_aSpannable = new SpannableString(getString(R.string.tutorial2_a));
        tutorial2_aSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, getString(R.string.tutorial2_a).length(), 0);
        builder.append(tutorial2_aSpannable);

        SpannableString tutorial2_bSpannable= new SpannableString(" " + getString(R.string.tutorial2_b));
        tutorial2_bSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.userActiveColor)), 0, getString(R.string.tutorial2_b).length()+1, 0);
        builder.append(tutorial2_bSpannable);

        SpannableString tutorial2_cSpannable = new SpannableString(getString(R.string.tutorial2_c));
        tutorial2_cSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, getString(R.string.tutorial2_c).length(), 0);
        builder.append(tutorial2_cSpannable);

        SpannableString tutorial2_dSpannable= new SpannableString(" " + getString(R.string.tutorial2_d));
        tutorial2_dSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.userSpeaksColor)), 0, getString(R.string.tutorial2_d).length()+1, 0);
        builder.append(tutorial2_dSpannable);

        SpannableString tutorial2_eSpannable = new SpannableString(" " + getString(R.string.tutorial2_e));
        tutorial2_eSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, getString(R.string.tutorial2_e).length()+1, 0);
        builder.append(tutorial2_eSpannable);

        SpannableString tutorial2_fSpannable= new SpannableString(" " + getString(R.string.tutorial2_f));
        tutorial2_fSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.userNoSoundColor)), 0, getString(R.string.tutorial2_f).length()+1, 0);
        builder.append(tutorial2_fSpannable);

        SpannableString tutorial2_gSpannable = new SpannableString(getString(R.string.tutorial2_g));
        tutorial2_gSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, getString(R.string.tutorial2_g).length(), 0);
        builder.append(tutorial2_gSpannable);


        tutorial2TextView.setText(builder, TextView.BufferType.SPANNABLE);
        return view;
    }
}
