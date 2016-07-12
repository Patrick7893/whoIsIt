package com.whois.whoiswho.callreceiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.whois.whoiswho.R;
import com.whois.whoiswho.activity.IcomingCallActivity;
import com.whois.whoiswho.activity.MissedCallActivity;
import com.whois.whoiswho.model.Phone;
import com.whois.whoiswho.model.Phone_Table;
import com.whois.whoiswho.utils.ContactsManager;

import java.util.Date;

/**
 * Created by stasenkopavel on 4/1/16.
 */
public class CallReceiver extends PhonecallReceiver {

    public static final String incomingCallActivity = "com.unteleported.truecaller.activity.IcomingCallActivity";


    @Override
    protected void onIncomingCallReceived(final Context ctx, final String number, Date start) {
        showDialogActivity(ctx, IcomingCallActivity.class, number);
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {

    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Intent callEndedIntent = new Intent("CallEnd");
        ctx.sendBroadcast(callEndedIntent);

    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Intent callEndedIntent = new Intent("CallEnd");
        ctx.sendBroadcast(callEndedIntent);
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Intent callEndedIntent = new Intent("CallEnd");
        ctx.sendBroadcast(callEndedIntent);
        showDialogActivity(ctx, MissedCallActivity.class, number);
    }


    private void showDialogActivity(final Context ctx, final Class activityClass, final String number) {
        Phone phone = new Select().from(Phone.class).where(Phone_Table.number.is(number)).querySingle();
        if (phone!=null) {
            if (phone.isBlocked()) {
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

}
