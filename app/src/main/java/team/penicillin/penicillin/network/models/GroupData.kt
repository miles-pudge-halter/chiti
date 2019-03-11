package team.penicillin.penicillin.network.models

import com.google.gson.annotations.SerializedName

data class Group(
    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("group_type")
    var type: String,
    @SerializedName("min")
    var min: Int,
    @SerializedName("max")
    var max: Int,
    @SerializedName("start_date")
    var startDate: String,
    @SerializedName("status")
    var status: String,
    @SerializedName("amount")
    var amount: Int,
    @SerializedName("users")
    var users: List<User> = listOf()
)

data class GroupDetailsResponse(
    @SerializedName("status")
    var status: String,
    @SerializedName("msg")
    var msg: String,
    @SerializedName("group")
    var group: Group
)

data class GroupListResponse(
    @SerializedName("status")
    var status: String,
    @SerializedName("groups")
    var groups: List<Group>
)

data class GroupInviteData(
    @SerializedName("status")
    var status: String,
    @SerializedName("msg")
    var msg: String,
    @SerializedName("invitation")
    var invitation: GroupInvitation
)

data class GroupInvitation(
    @SerializedName("id")
    var id: Int,
    @SerializedName("user")
    var user :User,
    @SerializedName("group")
    var group: Group
)