package com.unteleported.truecaller.screens.numpad;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.utils.CountryManager;
import com.unteleported.truecaller.utils.FontManager;
import com.unteleported.truecaller.utils.PhoneFormatter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by stasenkopavel on 4/13/16.
 */
public class NumpadFragment extends Fragment {

    @Bind({R.id.number1, R.id.number2, R.id.number3, R.id.number4, R.id.number5, R.id.number6, R.id.number7, R.id.number8, R.id.number9, R.id.number0, R.id.numberAst, R.id.numberDiez}) List<RelativeLayout> numbers;
    @Bind(R.id.numberContainer) RelativeLayout numberContainer;
    @Bind(R.id.numberTextView) TextView numberTextView;
    @Bind(R.id.backspaceButton) ImageView backSpaceImageView;


    private Vibrator vibe;
    private OnPhonePrsesentListener phonePrsesentListener;
    private Handler backSpaceHandler = new Handler();
    private TelephonyManager tMgr;

    public interface OnPhonePrsesentListener {
        public void canCall(String number);
        public void numberAbsent();
        public void keyBoardDestroyed();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_numpad, container, false);
        ButterKnife.bind(this, view);
        for (RelativeLayout number : numbers) {
            number.setOnClickListener(onClickListener);
            number.setOnLongClickListener(onLongClickListener);
        }
        backSpaceImageView.setOnClickListener(onClickListener);
        backSpaceImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                backSpaceHandler.post(new BackspaceRunnable());
                return false;

            }
        });
        tMgr = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        numberTextView.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        FontManager.overrideFonts(view);
        return view;
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.backspaceButton) {
                if (!TextUtils.isEmpty(numberTextView.getText())) {
                    String number = numberTextView.getText().toString();
                    number = number.substring(0, number.length()-1);
                    numberTextView.setText(number);
                    if (numberTextView.length() == 0) {
                        phonePrsesentListener.numberAbsent();
                    }
                }
            }
            else {
                setNumberText((String) v.getTag());
            }
        }
    };

    public void setNumberText(String number) {
        if (TextUtils.isEmpty(numberTextView.getText())) {
            numberContainer.setVisibility(View.VISIBLE);
            numberTextView.setText(number);
            phonePrsesentListener.canCall(number);
        }
        else {
            String text = numberTextView.getText().toString();
            numberTextView.setText(text+number);
            phonePrsesentListener.canCall(text+number);
        }
        vibe.vibrate(30);
    }

    public String formatNumber(String number) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (number.startsWith("+")) {
                String formattedNumber = PhoneNumberUtils.formatNumber(number, CountryManager.getIsoFromPhone(number));
                if (!TextUtils.isEmpty(formattedNumber)) {
                    return formattedNumber;
                }
                else {
                    return number;
                }
            }
            else {
                return PhoneNumberUtils.formatNumber(number, tMgr.getSimCountryIso().toUpperCase());
            }

        }
        else {
            return number;
        }
    }

    View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (v.getId() == R.id.number0) {
                setNumberText("+");
            }
            return true;
        }
    };



    public void setOnPhonePresentListener(OnPhonePrsesentListener onPhonePresentListener) {
        this.phonePrsesentListener = onPhonePresentListener;
    }


    @Override
    public void onPause() {
        phonePrsesentListener.keyBoardDestroyed();
        super.onPause();
    }

    class BackspaceRunnable implements Runnable {

        @Override
        public void run() {
            if (!TextUtils.isEmpty(numberTextView.getText())) {
                String number = numberTextView.getText().toString();
                number = number.substring(0, number.length()-1);
                numberTextView.setText(number);
                if (numberTextView.length() == 0) {
                    phonePrsesentListener.numberAbsent();
                }
                backSpaceImageView.postDelayed(new BackspaceRunnable(), 120);
            }
        }
    }
}
