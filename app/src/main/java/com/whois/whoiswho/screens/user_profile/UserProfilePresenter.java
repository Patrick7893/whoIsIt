package com.whois.whoiswho.screens.user_profile;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.whois.whoiswho.R;
import com.whois.whoiswho.api.ApiFactory;
import com.whois.whoiswho.api.BaseResponse;
import com.whois.whoiswho.api.GetRecordByNumberResponse;
import com.whois.whoiswho.model.Contact;
import com.whois.whoiswho.model.Phone;
import com.whois.whoiswho.model.Phone_Table;
import com.whois.whoiswho.model.User;
import com.whois.whoiswho.utils.CountryManager;
import com.whois.whoiswho.utils.PhoneFormatter;
import com.whois.whoiswho.utils.SharedPreferencesSaver;
import com.whois.whoiswho.utils.Toaster;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 5/12/16.
 */
public class UserProfilePresenter {

    private UserProfileFragment view;
    private Contact contact;
    private User user;

    public UserProfilePresenter(UserProfileFragment view, Contact contact) {
        this.view = view;
        this.contact = contact;
        user = new Select().from(User.class).querySingle();
    }

    public void find(String number) {
        if (!number.startsWith("+")) {
            if (number.length() == 10)
                number = "+38" + number;
            else if (number.length() == 11)
                number = "+3" + number;
            else if (number.length() == 12)
                number = "+" + number;
        }
        ApiFactory.getInstance().getApiInterface().getPhoneRecord(SharedPreferencesSaver.get().getToken(), number).observeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<GetRecordByNumberResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                view.hideProgressBar();
            }

            @Override
            public void onNext(GetRecordByNumberResponse findPhoneResponse) {
                view.hideProgressBar();
                view.displayUserInfo(findPhoneResponse);
                if (findPhoneResponse.getError() == 0)
                    contact.setId(findPhoneResponse.getData().getServerId());
                else
                    contact.setId(0);
            }
        });
    }

    public void blockUser() {
        Phone phone = new Select().from(Phone.class).where(Phone_Table.number.is(PhoneFormatter.removeAllNonNumeric(contact.getNumbers().get(0).getNumber()))).querySingle();
        if (phone!=null) {
            phone.setIsBlocked(true);
            phone.save();
        }
        else {
            phone = new Phone(contact.getNumbers().get(0).getNumber(), contact.getNumbers().get(0).getTypeOfNumber());
            phone.setName(view.titleTextView.getText().toString());
            phone.setIsBlocked(true);
            phone.save();
        }
        Toaster.toast(view.getContext(), view.getString(R.string.userAddedToBlackList));
        if (contact.getId()!=0) {
            ApiFactory.getInstance().getApiInterface().blockUser(contact.getId(), SharedPreferencesSaver.get().getToken(), user.getServerId()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<BaseResponse>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Log.d("ERROR", e.getMessage());
                  //  ApiFactory.checkConnection();
                }

                @Override
                public void onNext(BaseResponse baseResponse) {
                    Log.d("BLOCK", String.valueOf(baseResponse.error));
                }
            });
        }
    }

    public void unblockUser() {
        Phone spamPhone = new Select().from(Phone.class).where(Phone_Table.number.is(PhoneFormatter.removeAllNonNumeric(contact.getNumbers().get(0).getNumber()))).querySingle();
        spamPhone.setIsBlocked(false);
        spamPhone.save();
        Toaster.toast(view.getContext(), view.getString(R.string.userRemovedFromBlackList));
        if (contact.getId()!=0) {
            ApiFactory.getInstance().getApiInterface().unblockUser(contact.getId(), SharedPreferencesSaver.get().getToken(), user.getServerId()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<BaseResponse>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Log.d("ERROR", e.getMessage());
                   // ApiFactory.checkConnection();
                }

                @Override
                public void onNext(BaseResponse baseResponse) {
                    Log.d("UNBLOCK", String.valueOf(baseResponse.error));
                }
            });
        }

    }


}
