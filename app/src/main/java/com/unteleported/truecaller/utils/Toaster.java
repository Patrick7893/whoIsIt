package com.unteleported.truecaller.utils;

import android.content.Context;
import android.widget.Toast;

import com.unteleported.truecaller.app.App;


public class Toaster {


    public static void toast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
    }

    public static void toastShort(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context ctx, int messageId) {
        Toast.makeText(ctx, messageId, Toast.LENGTH_LONG).show();
    }
    public static void toastShort(Context ctx, int messageId) {
        Toast.makeText(ctx, messageId, Toast.LENGTH_SHORT).show();
    }

}
