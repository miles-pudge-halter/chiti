package team.penicillin.penicillin.network

import io.reactivex.Observable
import retrofit2.http.*
import team.penicillin.penicillin.network.models.*

interface TransactionApiService {
    @FormUrlEncoded
    @POST("api/v1/create_group")
    fun createGroup(
        @Field("name") groupName: String,
        @Field("group_type") groupType: String,
        @Field("amount") amount: Int,
        @Field("min") min: Int,
        @Field("max") max: Int,
        @Field("start_date") startDate: String
    ): Observable<RequestOtpResponse>

    @GET("api/v1/groups")
    fun getGroups(): Observable<GroupListResponse>

    @GET("api/v1/group_details")
    fun getGroupDetails(@Query("id") id: Int): Observable<GroupDetailsResponse>

    @GET("api/v1/invitation_details")
    fun getInvitationDetails(@Query("invitation_id") invitationId: Int): Observable<GroupInviteData>

    @FormUrlEncoded
    @POST("api/v1/accept_invitation")
    fun acceptInvitation(@Field("invitation_id") invitationId: Int): Observable<GroupDetailsResponse>
}