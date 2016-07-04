package com.whois.whoiswho.activity;

import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.whois.whoiswho.R;
import com.whois.whoiswho.api.ApiFactory;
import com.whois.whoiswho.api.BaseResponse;
import com.whois.whoiswho.api.GetRecordByNumberResponse;
import com.whois.whoiswho.app.App;
import com.whois.whoiswho.model.Phone;
import com.whois.whoiswho.model.Phone_Table;
import com.whois.whoiswho.utils.PhoneFormatter;
import com.whois.whoiswho.utils.SharedPreferencesSaver;
import com.whois.whoiswho.utils.Toaster;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 5/19/16.
 */
public class MissedCallPresenter {

    private MissedCallActivity view;
    private Phone phone;

    public MissedCallPresenter (MissedCallActivity view) {
        this.view = view;
    }

    public void find(String number) {
        ApiFactory.createRetrofitService().getPhoneRecord(SharedPreferencesSaver.get().getToken(), number).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<GetRecordByNumberResponse>() {
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
                    phone = findPhoneResponse.getData();
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
        if (phone !=null && this.phone.getServerId()!=0) {
            ApiFactory.createRetrofitService().blockUser(this.phone.getServerId(), SharedPreferencesSaver.get().getToken(), this.phone.getServerId()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<BaseResponse>() {
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
        if (phone!=null && phone.getServerId()!=0) {
            ApiFactory.createRetrofitService().unblockUser(phone.getServerId(), SharedPreferencesSaver.get().getToken(), phone.getServerId()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<BaseResponse>() {
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
