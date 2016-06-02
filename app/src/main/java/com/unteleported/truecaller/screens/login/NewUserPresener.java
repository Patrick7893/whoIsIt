package com.unteleported.truecaller.screens.login;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.api.ApiFactory;
import com.unteleported.truecaller.api.ApiInterface;
import com.unteleported.truecaller.api.RegistrationResponse;
import com.unteleported.truecaller.app.App;
import com.unteleported.truecaller.model.User;
import com.unteleported.truecaller.screens.mainscreen.TabFragment;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.AbstractPreferences;

import retrofit.mime.TypedFile;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stasenkopavel on 5/17/16.
 */
public class NewUserPresener {

    private NewUserFragment view;
    private File avatar;

    public NewUserPresener(NewUserFragment view) {
        this.view = view;
    }

    public void updateUser(int id, String firstname, String surname, String phone, String email, final File avatar) throws IOException {
        User user = new User(id, firstname, surname, phone, email, avatar);
        this.avatar = avatar;
        Observable<RegistrationResponse> updateUserObservable;
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
                            user.setCountyIso(view.getArguments().getString(NewUserFragment.COUNTRY));
                            user.setAvatar(avatar);
                            user.save();
                            view.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent, new TabFragment()).commit();
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

}
