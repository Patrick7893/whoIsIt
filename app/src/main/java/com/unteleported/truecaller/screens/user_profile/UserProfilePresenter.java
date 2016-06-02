package com.unteleported.truecaller.screens.user_profile;

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
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.model.Phone_Table;
import com.unteleported.truecaller.model.User;
import com.unteleported.truecaller.utils.CountryManager;
import com.unteleported.truecaller.utils.PhoneFormatter;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;
import com.unteleported.truecaller.utils.Toaster;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.AbstractPreferences;

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
        ApiFactory.createRetrofitService().findPhone(SharedPreferencesSaver.get().getToken(), number).observeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<FindPhoneResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                view.hideProgressBar();
              //  ApiFactory.checkConnection();
            }

            @Override
            public void onNext(FindPhoneResponse findPhoneResponse) {
                view.hideProgressBar();
                view.displayUserInfo(findPhoneResponse);
                if (findPhoneResponse.getError() == 0)
                    contact.setId(findPhoneResponse.getData().get(0).getServerId());
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
            ApiFactory.createRetrofitService().blockUser(contact.getId(), SharedPreferencesSaver.get().getToken(), user.getServerId()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<BaseResponse>() {
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
            ApiFactory.createRetrofitService().unblockUser(contact.getId(), SharedPreferencesSaver.get().getToken(), user.getServerId()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<BaseResponse>() {
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
