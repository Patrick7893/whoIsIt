package com.unteleported.truecaller.api;


import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.model.User;

import java.util.ArrayList;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by stasenkopavel on 4/25/16.
 */
public interface ApiInterface {

    String SERVICE_ENDPOINT = "http://10.0.1.6:3000";

    //@FormUrlEncoded
    @POST("/users")
    Observable<RegistrationResponse> newUser(@Body User user);

    @POST("/phones")
    Observable<RegistrationResponse> loadContacts(@Body LoadContactsRequest loadContactsRequest);

    @GET("/phones/find")
    Observable<FindPhoneResponse> findPhone(@Query("token") String token, @Query("query") String number);


}
