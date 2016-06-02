package com.unteleported.truecaller.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.squareup.picasso.Picasso;
import com.unteleported.truecaller.R;
import com.unteleported.truecaller.api.ApiFactory;
import com.unteleported.truecaller.api.ApiInterface;
import com.unteleported.truecaller.api.BaseResponse;
import com.unteleported.truecaller.api.FindPhoneResponse;
import com.unteleported.truecaller.app.App;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.model.Phone_Table;
import com.unteleported.truecaller.utils.PhoneFormatter;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;
import com.unteleported.truecaller.utils.Toaster;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 5/19/16.
 */
public class MissedCallPresenter {

    private MissedCallActivity view;
    private ArrayList<Phone> phones;

    public MissedCallPresenter (MissedCallActivity view) {
        this.view = view;
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
                    phones = findPhoneResponse.getData();
                   view.displayPhoneInfo(findPhoneResponse);
                }
            }
        });
    }

    public void blockUser(String number) {
        Phone phone = new Select().from(Phone.class).where(Phone_Table.number.is(PhoneFormatter.removeAllNonNumeric(number))).querySingle();
        if (phone!=null) {
            phone.setIsBlocked(true);
            phone.save();
        }
        else {
            phone = new Phone(number, 2);
            phone.setIsBlocked(true);
            phone.save();
        }
        Toaster.toast(App.getContext(), view.getString(R.string.userAddedToBlackList));
        if (phones.size()>0 && phones.get(0).getServerId()!=0) {
            ApiFactory.createRetrofitService().blockUser(phones.get(0).getServerId(), SharedPreferencesSaver.get().getToken(), phones.get(0).getServerId()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<BaseResponse>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Log.d("ERROR", e.getMessage());
                }

                @Override
                public void onNext(BaseResponse baseResponse) {

                }
            });
        }
    }

    public void unblockUser(String number) {
        Phone spamPhone = new Select().from(Phone.class).where(Phone_Table.number.is(number)).querySingle();
        spamPhone.setIsBlocked(false);
        spamPhone.save();
        Toaster.toast(App.getContext(), view.getString(R.string.userRemovedFromBlackList));
        if (phones.size()>0 && phones.get(0).getServerId()!=0) {
            ApiFactory.createRetrofitService().unblockUser(phones.get(0).getServerId(), SharedPreferencesSaver.get().getToken(), phones.get(0).getServerId()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<BaseResponse>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Log.d("ERROR", e.getMessage());
                }

                @Override
                public void onNext(BaseResponse baseResponse) {

                }
            });
        }

    }
}
