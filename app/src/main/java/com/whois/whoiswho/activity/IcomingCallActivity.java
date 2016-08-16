package com.whois.whoiswho.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.squareup.picasso.Picasso;
import com.whois.whoiswho.R;
import com.whois.whoiswho.api.ApiFactory;
import com.whois.whoiswho.api.ApiInterface;
import com.whois.whoiswho.api.GetRecordByNumberResponse;
import com.whois.whoiswho.model.Phone;
import com.whois.whoiswho.model.Phone_Table;
import com.whois.whoiswho.utils.CountryManager;
import com.whois.whoiswho.utils.SharedPreferencesSaver;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 4/4/16.
 */
public class IcomingCallActivity extends Activity {

    WindowManager.LayoutParams wlp;
    private int scrennHeight;
    @BindView(R.id.phoneTextView) TextView phoneTextView;
    @BindView(R.id.nameTextView) TextView nameTextView;
    @BindView(R.id.avatarImageView) CircleImageView avatarImageView;
    @BindView(R.id.spamTextView) TextView spamTextView;
    @BindView(R.id.container) FrameLayout container;

    private ActivityManager mActivityManager;
    private boolean mDismissed = false;

    private static final int MSG_ID_CHECK_TOP_ACTIVITY = 1;
    private static final long DELAY_INTERVAL = 100;

    private String number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        setContentView(R.layout.activity_incoming_call);
        //getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ButterKnife.bind(this);

        scrennHeight = getDisplayHeight();
        initiallizeScreen();

        final View view = getWindow().getDecorView().findViewById(R.id.container);

//        KeyguardManager kgm = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
//        boolean isKeyguardUp = kgm.inKeyguardRestrictedInputMode();
//        KeyguardManager.KeyguardLock kgl = kgm.newKeyguardLock("Your Activity/Service name");
//
//        if(isKeyguardUp){
//            kgl.disableKeyguard();
//            isKeyguardUp = false;
//        }

//        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        mHandler.sendEmptyMessageDelayed(MSG_ID_CHECK_TOP_ACTIVITY,
//                DELAY_INTERVAL);


        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v.getLayoutParams();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        lp.topMargin = (int) event.getRawY();
                        wlp = getWindow().getAttributes();
                        wlp.y = lp.topMargin - scrennHeight / 2;
                        getWindow().setAttributes(wlp);
                        break;
                    case MotionEvent.ACTION_UP:
                        SharedPreferencesSaver.get().saveCallDialogPosition((int) event.getRawY());
                        break;
                }
                return true;
            }
        });


    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MSG_ID_CHECK_TOP_ACTIVITY && !mDismissed) {
                List<ActivityManager.RunningTaskInfo> tasks = mActivityManager
                        .getRunningTasks(3);

                Log.d("TASK", tasks.get(0).topActivity.getClassName());

                String topActivityName = tasks.get(0).topActivity
                        .getClassName();

//                if (!topActivityName.equals(CallReceiver.incomingCallActivity)) {
//                    // Try to show on top until user dismiss this activity
//                    Intent i = new Intent();
//                    i.setClassName("com.unteleported.truecaller", CallReceiver.incomingCallActivity);
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    i.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//                    Bundle b = new Bundle();
//                    b.putString("phone", number);
//                    i.putExtras(b); //P
//                    startActivity(i);
//                }
                sendEmptyMessageDelayed(MSG_ID_CHECK_TOP_ACTIVITY,
                        DELAY_INTERVAL);
            }
        };
    };


    @Override
    public void onAttachedToWindow() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("PAUSER", "");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d("DETACHE", "");
    }
    

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mHandler.removeMessages(MSG_ID_CHECK_TOP_ACTIVITY);
    }

    public void initiallizeScreen() {

        wlp = new WindowManager.LayoutParams();
        Window window = getWindow();
        wlp.copyFrom(window.getAttributes());
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.gravity = Gravity.CENTER;
        int position = SharedPreferencesSaver.get().getCallDialogPosition();
        if (position > 0) {
            wlp.y = position - scrennHeight/2;
        }
        window.setAttributes(wlp);
        Bundle b = getIntent().getExtras();
        number = b.getString("phone");
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        find(number, tm.getSimCountryIso());
        phoneTextView.setText(PhoneNumberUtils.formatNumber(number, CountryManager.getIsoFromPhone(number)) + " - " + CountryManager.getCountryNameFromIso(CountryManager.getIsoFromPhone(number)));
        checkNumberIsSpam(number);
    }

    private void checkNumberIsSpam(String number) {
        Phone phone = new Select().from(Phone.class).where(Phone_Table.number.is(number)).querySingle();
        if (phone!=null) {
            if (phone.isBlocked()) {
                container.setBackgroundColor(getResources().getColor(R.color.missedCall));
                spamTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public int getDisplayHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public void find(String number, String countyIso) {
        ApiFactory.getInstance().getApiInterface().getPhoneRecord(SharedPreferencesSaver.get().getToken(), number, countyIso).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<GetRecordByNumberResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d("ERROR", e.getMessage());
            }

            @Override
            public void onNext(GetRecordByNumberResponse findPhoneResponse) {
                Log.d("Success", String.valueOf(findPhoneResponse.getError()));
                if (findPhoneResponse.getError() == 0) {
                    nameTextView.setText(findPhoneResponse.getData().getName());
                    if (findPhoneResponse.getData().getNumberOfSettedSpam() > 3) {
                        container.setBackgroundColor(getResources().getColor(R.color.missedCall));
                        spamTextView.setVisibility(View.VISIBLE);
                        spamTextView.setText(findPhoneResponse.getData().getNumberOfSettedSpam() + " " + getString(R.string.usersCheckNumberAsSpam));
                    }
                    if (!TextUtils.isEmpty(findPhoneResponse.getData().getAvatar().getUrl()))
                        Picasso.with(getApplicationContext()).load(ApiInterface.SERVICE_ENDPOINT + findPhoneResponse.getData().getAvatar().getUrl()).into(avatarImageView);
                }
            }
        });
    }

    @OnClick(R.id.closeButton)
    public void close() {
        finish();
    }

}
