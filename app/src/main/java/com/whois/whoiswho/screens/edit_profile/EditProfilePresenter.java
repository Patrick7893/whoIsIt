package com.whois.whoiswho.screens.edit_profile;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.whois.whoiswho.R;
import com.whois.whoiswho.api.ApiFactory;
import com.whois.whoiswho.api.ApiInterface;
import com.whois.whoiswho.api.RegistrationResponse;
import com.whois.whoiswho.app.App;
import com.whois.whoiswho.model.User;
import com.whois.whoiswho.utils.SharedPreferencesSaver;
import com.whois.whoiswho.utils.Toaster;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 6/13/16.
 */
public class EditProfilePresenter {

    private String countryIso;
    private EditProfileFragment view;
    private User user;

    public EditProfilePresenter(EditProfileFragment view) {
        this.view = view;
    }

    public void getUserInfo() throws IOException {
        user = new Select().from(User.class).querySingle();
        if (user != null) {
            this.countryIso = user.getCountyIso();
            view.displayUserInfo(user);
        }
    }

    void updateUser(int id, String firstname, String surname, String phone, String email, File avatar) throws IOException {
        Observable<Response<RegistrationResponse>> updateUserObservable;
        User user = new User(id, firstname, surname, phone, email, avatar);
        RequestBody firstNameBody = RequestBody.create(MediaType.parse("multipart/form-data"), firstname);
        RequestBody surnameBody = RequestBody.create(MediaType.parse("multipart/form-data"), surname);
        RequestBody emailBody = RequestBody.create(MediaType.parse("multipart/form-data"), email);
        if (user.getAvatarFile() == null) {
            updateUserObservable = ApiFactory.getInstance().getApiInterface().updateUser(user.getServerId(), firstNameBody, surnameBody, emailBody, null);
        } else {
            updateUserObservable = ApiFactory.getInstance().getApiInterface().updateUser(user.getServerId(), firstNameBody, surnameBody, emailBody, MultipartBody.Part.createFormData("avatar", avatar.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), avatar)));
        }
        ProgressDialog pd = new ProgressDialog(this.view.getActivity());
        pd.setMessage(App.getContext().getString(R.string.loadingData));
        pd.show();
        updateUserObservable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Response<RegistrationResponse>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Response<RegistrationResponse> registrationResponse) {
                pd.dismiss();
                if (registrationResponse.code() == 403) {
                    Toaster.toast(App.getContext(), (int) R.string.wrongEmail);
                } else if (registrationResponse.body().getError() == 0) {
                    SharedPreferencesSaver.get().saveToken(registrationResponse.body().getData().getToken());
                    User user = registrationResponse.body().getData();
                    if (!TextUtils.isEmpty(registrationResponse.body().getData().getAvatarPath())) {
                        user.setAvatarPath(ApiInterface.SERVER_DOMAIN + registrationResponse.body().getData().getAvatarPath());
                    }
                    user.setAvatarFile(avatar);
                    user.setCountyIso(countryIso);
                    user.save();
                    EditProfilePresenter.this.view.updateSideBarUserInfo();
                    EditProfilePresenter.this.view.back();
                }
            }
        });
    }

    File convertBitmapToFile(Bitmap bitmap) throws IOException {
        if (bitmap == null) {
            return null;
        }
        File f = new File(this.view.getActivity().getCacheDir(), "avatar");
        f.createNewFile();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapdata = bos.toByteArray();
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return f;
    }

    public User getUser() {
        return user;
    }
}
