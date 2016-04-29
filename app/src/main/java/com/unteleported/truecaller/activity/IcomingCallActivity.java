package com.unteleported.truecaller.activity;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.api.ApiFactory;
import com.unteleported.truecaller.api.ApiInterface;
import com.unteleported.truecaller.api.FindPhoneResponse;
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
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

    public int getDisplayHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public void initiallizeScreen() {

        wlp = new WindowManager.LayoutParams();
        Window window = getWindow();
        wlp.copyFrom(window.getAttributes());
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.gravity = Gravity.CENTER;
        int position = SharedPreferencesSaver.get().getCallDialogPosition();
        if (position>0) {
            wlp.y = position - scrennHeight/2;
        }
        window.setAttributes(wlp);
        Bundle b = getIntent().getExtras();
        String phone = b.getString("phone");
        find(phone);
        phoneTextView.setText(phone);

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
                   //nameTextView.setText(findPhoneResponse.getPhone().getName());
                }
            }
        });
    }

    @OnClick(R.id.closeButton)
    public void close() {
        finish();
    }

}
