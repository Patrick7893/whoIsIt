package com.whois.whoiswho.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface ApiInterface {
    String SERVER_DOMAIN = "https://truecaller.unteleported.com";
    String SERVICE_ENDPOINT = "https://truecaller.unteleported.com/api/v1/";

//    String SERVER_DOMAIN = "http://10.0.1.141:3000";
//    String SERVICE_ENDPOINT = "http://10.0.1.141:3000/api/v1/";

    @FormUrlEncoded
    @PUT("phones/{id}/block.json")
    Observable<BaseResponse> blockUser(@Path("id") long j, @Field("token") String str, @Field("user_id") long j2);

    @POST("users/registration.json")
    @Multipart
    Observable<Response<RegistrationResponse>> createUser(@Part("number") RequestBody requestBody, @Part("country_iso") RequestBody requestBody2, @Part("firstname") RequestBody requestBody3, @Part("surname") RequestBody requestBody4, @Part("email") RequestBody requestBody5, @Part("device") RequestBody requestBody6, @Part MultipartBody.Part part);

    @GET("phones/search.json")
    Observable<FindPhoneResponse> findPhone(@Query("token") String str, @Query("query") String str2, @Query("user_locale") String str3);

    @GET("phones/fetch_by_number.json")
    Observable<GetRecordByNumberResponse> getPhoneRecord(@Query("token") String str, @Query("number") String str2, @Query("country_iso") String str3);

    @GET("phones/get_spammers.json")
    Observable<GetSpammersResponse> getSpammers(@Query("token") String str);

    @POST("phones.json")
    Observable<RegistrationResponse> loadContacts(@Body LoadContactsRequest loadContactsRequest);

    @FormUrlEncoded
    @POST("users/login.json")
    Observable<Response<RegistrationResponse>> login(@Field("number") String str, @Field("country_iso") String str2);

    @FormUrlEncoded
    @POST("users/verify.json")
    Observable<RegistrationResponse> smsConfirm(@Field("number") String str, @Field("country_iso") String str2, @Field("pin") String str3);

    @FormUrlEncoded
    @PUT("phones/{id}/unblock.json")
    Observable<BaseResponse> unblockUser(@Path("id") long j, @Field("token") String str, @Field("user_id") long j2);

    @PUT("users/{id}.json")
    @Multipart
    Observable<Response<RegistrationResponse>> updateUser(@Path("id") int i, @Part("firstname") RequestBody requestBody, @Part("surname") RequestBody requestBody2, @Part("email") RequestBody requestBody3, @Part MultipartBody.Part part);
}
