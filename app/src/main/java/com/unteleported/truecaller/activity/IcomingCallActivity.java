package com.unteleported.truecaller.activity;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.squareup.picasso.Picasso;
import com.unteleported.truecaller.R;
import com.unteleported.truecaller.api.ApiFactory;
import com.unteleported.truecaller.api.ApiInterface;
import com.unteleported.truecaller.api.FindPhoneResponse;
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.utils.CountryManager;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 4/4/16.
 */
public class IcomingCallActivity extends AppCompatActivity {

    WindowManager.LayoutParams wlp;
    private int scrennHeight;
    @Bind(R.id.phoneTextView) TextView phoneTextView;
    @Bind(R.id.nameTextView) TextView nameTextView;
    @Bind(R.id.avatarImageView) CircleImageView avatarImageView;
    @Bind(R.id.spamTextView) TextView spamTextView;
    @Bind(R.id.container) RelativeLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_incoming_call);
        ButterKnife.bind(this);

        scrennHeight = getDisplayHeight();
        initiallizeScreen();

        final View view = getWindow().getDecorView().findViewById(R.id.container);

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


    @Override
    public void onAttachedToWindow() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        String number = b.getString("phone");
        find(number);
        phoneTextView.setText(PhoneNumberUtils.formatNumber(number, CountryManager.getIsoFromPhone(number)) + " - " + CountryManager.getCountryNameFromIso(CountryManager.getIsoFromPhone(number)));
        List<Phone> spamPhones = new Select().from(Phone.class).queryList();
        for (Phone phone : spamPhones) {
            if (phone.getNumber().contains(number)) {
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

    public void find(String number) {
        ApiFactory.createRetrofitService().findPhone(SharedPreferencesSaver.get().getToken(), number).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<FindPhoneResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d("ERROR", e.getMessage());
            }

            @Override
            public void onNext(FindPhoneResponse findPhoneResponse) {
                Log.d("Success", String.valueOf(findPhoneResponse.getError()));
                if (findPhoneResponse.getError() == 0) {
                    nameTextView.setText(findPhoneResponse.getData().get(0).getName());
                    if (findPhoneResponse.getData().get(0).getNumberOfSettedSpam() > 3) {
                        container.setBackgroundColor(getResources().getColor(R.color.missedCall));
                        spamTextView.setVisibility(View.VISIBLE);
                        spamTextView.setText(findPhoneResponse.getData().get(0).getNumberOfSettedSpam() + " " + getString(R.string.usersCheckNumberAsSpam));
                    }
                    if (!TextUtils.isEmpty(findPhoneResponse.getData().get(0).getAvatar().getUrl()))
                        Picasso.with(getApplicationContext()).load(ApiInterface.SERVICE_ENDPOINT + findPhoneResponse.getData().get(0).getAvatar().getUrl()).into(avatarImageView);
                }
            }
        });
    }

    @OnClick(R.id.closeButton)
    public void close() {
        finish();
    }

}
