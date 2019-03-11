package team.penicillin.penicillin.network.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("token")
    var token: String,
    @SerializedName("phone")
    var phone: String,
    @SerializedName("balance")
    var balance: Long,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("avatar")
    var avatar: String? = null,
    @SerializedName("address")
    var address: String? = null,
    @SerializedName("groups")
    var groups: List<Group> = (listOf()),
    @SerializedName("due_amount")
    var dueAmount: Double = 0.0
)

data class UserRegisterResponse(
    @SerializedName("status")
    var status: String,
    @SerializedName("msg")
    var msg: String,
    @SerializedName("user")
    var user: User
)

data class SearchUser(
    @SerializedName("phone")
    var phone: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("avatar")
    var avatar: String? = null
)

data class UserListResponse(
    @SerializedName("status")
    var status: String,
    @SerializedName("msg")
    var msg: String,
    @SerializedName("users")
    var users: List<SearchUser> = listOf()
)