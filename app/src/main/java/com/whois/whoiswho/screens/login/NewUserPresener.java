package com.whois.whoiswho.screens.login;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.jaredrummler.android.device.DeviceName;
import com.whois.whoiswho.R;
import com.whois.whoiswho.api.ApiFactory;
import com.whois.whoiswho.api.ApiInterface;
import com.whois.whoiswho.api.RegistrationResponse;
import com.whois.whoiswho.app.App;
import com.whois.whoiswho.model.Database;
import com.whois.whoiswho.model.User;
import com.whois.whoiswho.screens.mainscreen.TabFragment;
import com.whois.whoiswho.utils.FirebaseLogManager;
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
 * Created by stasenkopavel on 5/17/16.
 */
public class NewUserPresener {

    private NewUserFragment view;

    public NewUserPresener(NewUserFragment view) {
        this.view = view;
    }

    public void createUser(String number, String countyIso, String firstname, String surname, String phone, String email, final File avatar) throws IOException {
        Observable<Response<RegistrationResponse>> updateUserObservable;
        RequestBody numberBody = RequestBody.create(MediaType.parse("multipart/form-data"), number);
        RequestBody countryIsoBody = RequestBody.create(MediaType.parse("multipart/form-data"), countyIso);
        RequestBody firstNameBody = RequestBody.create(MediaType.parse("multipart/form-data"), firstname);
        RequestBody surnameBody = RequestBody.create(MediaType.parse("multipart/form-data"), surname);
        RequestBody emailBody = RequestBody.create(MediaType.parse("multipart/form-data"), email);
        RequestBody deviceBody = RequestBody.create(MediaType.parse("multipart/form-data"), DeviceName.getDeviceName());
        if (avatar == null) {
            updateUserObservable = ApiFactory.getInstance().getApiInterface().createUser(numberBody, countryIsoBody, firstNameBody, surnameBody, emailBody, deviceBody, null);
        } else {
            updateUserObservable = ApiFactory.getInstance().getApiInterface().createUser(numberBody, countryIsoBody, firstNameBody, surnameBody, emailBody, deviceBody, MultipartBody.Part.createFormData("avatar", avatar.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), avatar)));
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
                }
                else if (((RegistrationResponse) registrationResponse.body()).getError() == 0) {
                    App.getContext().deleteDatabase(Database.NAME);
                    SharedPreferencesSaver.get().saveToken(((RegistrationResponse) registrationResponse.body()).getData().getToken());
                    User user = ((RegistrationResponse) registrationResponse.body()).getData();
                    if (!TextUtils.isEmpty(((RegistrationResponse) registrationResponse.body()).getData().getAvatarPath())) {
                        user.setAvatarPath(ApiInterface.SERVER_DOMAIN + ((RegistrationResponse) registrationResponse.body()).getData().getAvatarPath());
                    }
                    user.setCountyIso(NewUserPresener.this.view.getArguments().getString(NewUserFragment.COUNTRY));
                    user.setAvatarFile(avatar);
                    user.save();
                    NewUserPresener.this.view.getActivity().getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.flContent, new TabFragment()).addToBackStack(null).commit();
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
