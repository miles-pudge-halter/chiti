package team.penicillin.penicillin.network.apis

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import team.penicillin.penicillin.network.models.*

interface UserApiService{
    @FormUrlEncoded
    @POST("api/v1/request_for_otp")
    fun requestOtp(@Field("phone") phone: String): Observable<RequestOtpResponse>

    @FormUrlEncoded
    @POST("api/v1/verify")
    fun verifyOtp(@Field("phone") phone: String, @Field("otp") otp: String): Observable<VerifyOtpResponse>

    @Multipart
    @POST("api/v1/sign_up")
    fun updateRetailer (
        @Part("name") name: RequestBody,
        @Part("address") address: RequestBody,
        @Part userPhoto: MultipartBody.Part?
    ): Observable<UserRegisterResponse>

    @GET("api/v1/profile")
    fun getProfileData(): Observable<UserRegisterResponse>

    @FormUrlEncoded
    @POST("api/v1/register_fcm")
    fun saveDeviceToken(@Field("fcm") deviceToken: String): Observable<Any>

    @GET("api/v1/users")
    fun getUsers(): Observable<UserListResponse>

    @GET("api/v1/notifications")
    fun getNotifications(): Observable<NotiListResponse>

    @FormUrlEncoded
    @POST("api/v1/invite_group")
    fun inviteToGroup(@Field("phone") phone: String, @Field("group_id") groupId: Int)
            :Observable<RequestOtpResponse>
}