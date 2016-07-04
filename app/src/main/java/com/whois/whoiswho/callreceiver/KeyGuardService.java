package com.whois.whoiswho.callreceiver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by stasenkopavel on 6/21/16.
 */
public class KeyGuardService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
