package com.unteleported.truecaller.screens.numpad;

import android.text.TextUtils;
import android.view.View;

/**
 * Created by stasenkopavel on 4/28/16.
 */
public class NumPadPresenter {

    private NumpadFragment view;

    public NumPadPresenter(NumpadFragment view) {
        this.view = view;
    }

    public void setNumberText(String number) {
        if (TextUtils.isEmpty(view.numberTextView.getText())) {
            view.numberContainer.setVisibility(View.VISIBLE);
            view.numberTextView.setText(number);
            view.phonePrsesentListener.canCall(number);
        }
        else {
            String text = view.numberTextView.getText().toString();
            view.numberTextView.setText(formatNumber(text+number));
            view.phonePrsesentListener.canCall(text+number);
        }
        view.vibe.vibrate(30);
    }

    public String formatNumber(String number) {
        if (number.length() == 3) {
            return number + " ";
        }
        if (number.length() == 7) {
            return number + " ";
        }
        else
            return number;

    }
}
