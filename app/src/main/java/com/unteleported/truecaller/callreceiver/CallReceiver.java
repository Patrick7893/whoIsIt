package com.unteleported.truecaller.callreceiver;

import android.content.Context;
import android.content.Intent;
import android.content.SyncAdapterType;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.unteleported.truecaller.activity.IcomingCallActivity;
import com.unteleported.truecaller.activity.MissedCallActivity;
import com.unteleported.truecaller.utils.UserContactsManager;

import java.security.PublicKey;
import java.util.Date;

/**
 * Created by stasenkopavel on 4/1/16.
 */
public class CallReceiver extends PhonecallReceiver {

    public static final String incomingCallActivity = "com.unteleported.truecaller.activity.IcomingCallActivity";


    @Override
    protected void onIncomingCallReceived(final Context ctx, final String number, Date start) {
        showDialogActivity(ctx, IcomingCallActivity.class, number);
        Log.d("IncomingCall", number);
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {

    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d("IncomingCallEnded", number);
        Intent callEndedIntent = new Intent("CallEnd");
        ctx.sendBroadcast(callEndedIntent);

    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.d("OutgoingCall", number);
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d("OutgoingCallEnded", number);
        Intent callEndedIntent = new Intent("CallEnd");
        ctx.sendBroadcast(callEndedIntent);
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.d("MISSED CALL", number);
        Intent callEndedIntent = new Intent("CallEnd");
        ctx.sendBroadcast(callEndedIntent);
        showDialogActivity(ctx, MissedCallActivity.class, number);
    }


    private void showDialogActivity(final Context ctx, final Class activityClass, final String number) {

        if (!UserContactsManager.checkNumberInContacts(ctx, number)) {

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub   Intent i = new Intent(ctx, activityClass);
                    Intent i = new Intent(ctx, activityClass);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle b = new Bundle();
                    b.putString("phone", number);
                    i.putExtras(b); //P
                    ctx.startActivity(i);
                }
            }, 1000);


        }
    }

}
