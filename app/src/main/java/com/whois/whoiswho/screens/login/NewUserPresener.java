package com.whois.whoiswho.screens.login;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.whois.whoiswho.R;
import com.whois.whoiswho.api.ApiFactory;
import com.whois.whoiswho.api.ApiInterface;
import com.whois.whoiswho.api.RegistrationResponse;
import com.whois.whoiswho.app.App;
import com.whois.whoiswho.model.User;
import com.whois.whoiswho.screens.mainscreen.TabFragment;
import com.whois.whoiswho.utils.FirebaseLogManager;
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
 * Created by stasenkopavel on 5/17/16.
 */
public class NewUserPresener {

    private NewUserFragment view;

    public NewUserPresener(NewUserFragment view) {
        this.view = view;
    }

    public void createUser(String number, String countyIso, String firstname, String surname, String phone, String email, final File avatar) throws IOException {
        Observable<RegistrationResponse> updateUserObservable;
        if (avatar == null) {
            updateUserObservable = ApiFactory.getInstance().getApiInterface().createUser(number, countyIso, firstname, surname, email, null);
        }
        else {
            updateUserObservable = ApiFactory.getInstance().getApiInterface().createUser(number, countyIso, firstname, surname, email, new TypedFile("multipart/form-data", avatar));
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
                        FirebaseLogManager.sendLogToFirebase(view.mFirebaseAnalytics, "RegistrationFailed", "name", e.getMessage());
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
