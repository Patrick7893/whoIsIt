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
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.model.Phone_Table;
import com.unteleported.truecaller.utils.CountryManager;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;
import com.unteleported.truecaller.utils.Toaster;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 5/12/16.
 */
public class UserProfilePresenter {

    private UserProfileFragment view;
    private Contact contact;

    public UserProfilePresenter(UserProfileFragment view, Contact contact) {
        this.view = view;
        this.contact = contact;
    }

    public void find(String number) {
        ApiFactory.createRetrofitService().findPhone(SharedPreferencesSaver.get().getToken(), number).observeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<FindPhoneResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(FindPhoneResponse findPhoneResponse) {
                view.hideProgressBar();
                view.displayUserInfo(findPhoneResponse);
                contact.setId(findPhoneResponse.getData().get(0).getServerId());
            }
        });
    }

    public void blockUser() {
        contact.getPhones().get(0).setIsBlocked(true);
        contact.getPhones().get(0).setName(contact.getName());
        contact.getPhones().get(0).save();
        Toaster.toast(view.getContext(), view.getString(R.string.userAddedToBlackList));
        if (contact.getId()!=0) {
            ApiFactory.createRetrofitService().blockUser(contact.getId(), contact.getId()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<BaseResponse>() {
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

    public void unblockUser() {
        Phone spamPhone = new Select().from(Phone.class).where(Phone_Table.number.is(contact.getPhones().get(0).getNumber())).querySingle();
        spamPhone.setIsBlocked(false);
        spamPhone.save();
        Toaster.toast(view.getContext(), view.getString(R.string.userRemovedFromBlackList));
        if (contact.getId()!=0) {
            ApiFactory.createRetrofitService().unblockUser(contact.getId(), contact.getId()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<BaseResponse>() {
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
