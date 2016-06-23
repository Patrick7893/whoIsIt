package com.unteleported.truecaller.api;


import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
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
   // String SERVICE_ENDPOINT = "http://truecaller.unteleported.com";


    @Multipart
    @POST("/users/login")
    Observable<RegistrationResponse> login(@Part("number") String number);

    @Multipart
    @POST("/users/smsconfirm")
    Observable<RegistrationResponse> smsConfirm(@Part("sms") int sms);

    @Multipart
    @POST("/users")
    Observable<RegistrationResponse> createUser(@Part("number") String number, @Part("countryIso") String countryIso, @Part("firstname") String firstname, @Part("surname") String surname, @Part("email") String email, @Part("avatar") TypedFile avatar);

    @Multipart
    @PUT("/users/{id}")
    Observable<RegistrationResponse> updateUser(@Path("id") int id, @Part("firstname") String firstname, @Part("surname") String surname, @Part("email") String email, @Part("avatar") TypedFile avatar);

    @POST("/phones")
    Observable<RegistrationResponse> loadContacts(@Body LoadContactsRequest loadContactsRequest);

    @GET("/phones/search")
    Observable<FindPhoneResponse> findPhone(@Query("token") String token, @Query("query") String query);

    @GET("/phones/getRecordByNumber")
    Observable<GetRecordByNumberResponse> getPhoneRecord(@Query("token") String token, @Query("number") String number);

    @GET("/phones/getSpammers")
    Observable<GetSpammersResponse> getSpammers(@Query("token") String token);

    @Multipart
    @PUT("/phones/{id}/block")
    Observable<BaseResponse> blockUser(@Path("id") long id, @Part("token") String token,  @Part("userId") long userId);

    @Multipart
    @PUT("/phones/{id}/unblock")
    Observable<BaseResponse> unblockUser(@Path("id") long id, @Part("token") String token, @Part("userId") long userId);


}
