package com.unteleported.truecaller.api;


import android.support.annotation.Nullable;

import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.model.User;

import java.util.ArrayList;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import rx.Observable;

/**
 * Created by stasenkopavel on 4/25/16.
 */
public interface ApiInterface {

    String SERVICE_ENDPOINT = "http://10.0.1.6:3000";


    @Multipart
    @POST("/users/login")
    Observable<RegistrationResponse> login(@Part("number") String number, @Part("countryIso") String countryIso);

    /*@Multipart
    @POST("/users")
    Observable<RegistrationResponse> newUser(@Part("name") String name, @Part("number") String number, @Part("email") String email, @Part("countryIso") String countryIso, @Part("avatar") TypedFile avatar);

    @Multipart
    @POST("/users")
    Observable<RegistrationResponse> newUserNoAvatar(@Part("name") String name, @Part("number") String number, @Part("email") String email, @Part("countryIso") String countryIso);*/

    @Multipart
    @PUT("/users/{id}")
    Observable<RegistrationResponse> updateUser(@Path("id") int id, @Part("firstname") String firstname, @Part("surname") String surname, @Part("email") String email, @Part("avatar") TypedFile avatar);

    @POST("/phones")
    Observable<RegistrationResponse> loadContacts(@Body LoadContactsRequest loadContactsRequest);

    @GET("/phones/findPhone")
    Observable<FindPhoneResponse> findPhone(@Query("token") String token, @Query("query") String number);

    @GET("/phones/getSpammers")
    Observable<FindPhoneResponse> getSpammers(@Query("token") String token);

    @Multipart
    @PUT("/phones/{id}/block")
    Observable<BaseResponse> blockUser(@Path("id") long id, @Part("userId") long userId);

    @Multipart
    @PUT("/phones/{id}/unblock")
    Observable<BaseResponse> unblockUser(@Path("id") long id, @Part("userId") long userId);


}
