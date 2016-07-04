package com.whois.whoiswho.utils;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whois.whoiswho.app.App;

/**
 * Created by stasenkopavel on 4/18/16.
 */
public class FontManager {

    public static final Typeface ROBOTO_MEDIUM = getFont("Roboto-Medium.ttf");
    public static final Typeface ROBOTO = getFont("Roboto-Regular.ttf");
    public static final Typeface BEBAS = getFont("bebas_neue.ttf");
    public static final Typeface ROBOTO_LIGHT = getFont("Roboto-Light.ttf");
    public static final Typeface FUTURA = getFont("futura.ttf");

    private static Typeface getFont(String fontName) {
        return Typeface.createFromAsset(App.getContext().getAssets(),
                new StringBuilder("fonts/").append(fontName).toString());
    }

    public static void overrideFonts(final View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                overrideFonts(viewGroup.getChildAt(i));
            }
        }
        else if (view.getTag() != null) {
            if (view.getTag().toString().equals("Roboto-Medium.ttf")) {
                ((TextView) view).setTypeface(ROBOTO_MEDIUM);

            }
            else if (view.getTag().toString().equals("Roboto-Regular.ttf")) {
                ((TextView) view).setTypeface(ROBOTO);

            }
            else if (view.getTag().toString().equals("bebas_neue.ttf")) {
                ((TextView) view).setTypeface(BEBAS);

            }
            else if (view.getTag().toString().equals("Roboto-Light.ttf")) {
                ((TextView) view).setTypeface(ROBOTO_LIGHT);

            }
            else if (view.getTag().toString().equals("futura.ttf")) {
                ((TextView) view).setTypeface(FUTURA);

            }

        }

    }
}
