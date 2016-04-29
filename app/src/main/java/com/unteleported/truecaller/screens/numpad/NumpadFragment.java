package com.unteleported.truecaller.screens.numpad;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.utils.FontManager;

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

    private static NumPadPresenter presenter;

    Vibrator vibe;
    OnPhonePrsesentListener phonePrsesentListener;
    private Handler backSpaceHandler = new Handler();

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
        presenter = new NumPadPresenter(this);
        for (RelativeLayout number : numbers) {
            number.setOnClickListener(onClickListener);
        }
        backSpaceImageView.setOnClickListener(onClickListener);
        backSpaceImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                backSpaceHandler.post(new BackspaceRunnable());
                return false;

            }
        });
        vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
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
                presenter.setNumberText((String) v.getTag());
            }
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
                backSpaceImageView.postDelayed(new BackspaceRunnable(), 150);
            }
        }
    }
}
