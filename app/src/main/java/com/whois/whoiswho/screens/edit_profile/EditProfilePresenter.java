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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit.mime.TypedFile;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 6/13/16.
 */
public class EditProfilePresenter {

    private EditProfileFragment view;
    private User user;

    public EditProfilePresenter(EditProfileFragment view) {
        this.view = view;
    }

    public void getUserInfo() throws IOException {
        user = new Select().from(User.class).querySingle();
        if (user != null) {
            view.displayUserInfo(user);
        }
    }

    public void updateUser(int id, String firstname, String surname, String phone, String email, final File avatar) throws IOException {
        User user = new User(id, firstname, surname, phone, email, avatar);
        final Observable<RegistrationResponse> updateUserObservable;
        if (user.getAvatar() == null) {
            updateUserObservable = ApiFactory.createRetrofitService().updateUser(user.getServerId(), user.getFirstname(), user.getSurname(), user.getEmail(), null);
        }
        else {
            updateUserObservable = ApiFactory.createRetrofitService().updateUser(user.getServerId(), user.getFirstname(), user.getSurname(), user.getEmail(), new TypedFile("multipart/form-data", user.getAvatar()));
        }
        final ProgressDialog pd = new ProgressDialog(view.getActivity());
        pd.setMessage(App.getContext().getString(R.string.loadingData));
        pd.show();
        updateUserObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RegistrationResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ApiFactory.checkConnection();
                        pd.dismiss();
                    }

                    @Override
                    public void onNext(RegistrationResponse registrationResponse) {
                        pd.dismiss();
                        if (registrationResponse.getError() == 0) {
                            SharedPreferencesSaver.get().saveToken(registrationResponse.getToken());
                            User user = registrationResponse.getData();
                            if (!TextUtils.isEmpty(registrationResponse.getAvatarPath()))
                                user.setAvatarPath(ApiInterface.SERVICE_ENDPOINT + registrationResponse.getAvatarPath());
                            user.setAvatar(avatar);
                            user.save();
                            view.updateSideBarUserInfo();
                            view.back();
                        }
                    }
                });
    }


    public File convertBitmapToFile(Bitmap bitmap) throws IOException {
        if (bitmap == null) {
            return null;
        }
        File f = new File(view.getActivity().getCacheDir(), "avatar");
        f.createNewFile();


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
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
