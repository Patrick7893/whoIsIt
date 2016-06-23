package com.unteleported.truecaller.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.unteleported.truecaller.R;

/**
 * Created by stasenkopavel on 6/7/16.
 */
public class IncomingCallDialog extends Dialog {

    public IncomingCallDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);
    }
}
