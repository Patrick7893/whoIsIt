package com.unteleported.truecaller.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.squareup.picasso.Picasso;
import com.unteleported.truecaller.R;
import com.unteleported.truecaller.api.ApiFactory;
import com.unteleported.truecaller.api.ApiInterface;
import com.unteleported.truecaller.api.FindPhoneResponse;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.model.Phone_Table;
import com.unteleported.truecaller.utils.CountryManager;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;

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
public class MissedCallActivity extends AppCompatActivity {

    private WindowManager.LayoutParams wlp;
    private int scrennHeight;
    private String number;

    @Bind(R.id.phoneTextView) TextView phoneTextView;
    @Bind(R.id.nameTextView) TextView nameTextView;
    @Bind(R.id.avatarImageView) CircleImageView avatarImageView;
    @Bind(R.id.spamTextView) TextView spamTextView;
    @Bind(R.id.container) RelativeLayout container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missed_call);
        ButterKnife.bind(this);
        scrennHeight = getDisplayHeight();
        initiallizeScreen();

    }

    @Override
    public void onAttachedToWindow() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
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
            wlp.y = position - scrennHeight / 2;
        }
        window.setAttributes(wlp);
        Bundle b = getIntent().getExtras();
        number = b.getString("phone");
        find(number);
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

    @OnClick(R.id.closeButton)
    public void close() {
        finish();
    }

    @OnClick(R.id.callTextVIew)
    public void call() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

    @OnClick(R.id.saveTextView)
    public void save() {
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE).putExtra(ContactsContract.Intents.Insert.PHONE, number);
        startActivity(intent);
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
}
