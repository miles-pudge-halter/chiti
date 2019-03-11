package team.penicillin.penicillin.network.models

import com.google.gson.annotations.SerializedName

data class RequestOtpResponse(
    @SerializedName("status")
    var status: String,
    @SerializedName("msg")
    var message: String
)

data class VerifyOtpResponse(
    @SerializedName("status")
    var status: String,
    @SerializedName("msg")
    var message: String,
    @SerializedName("token")
    var token: String
)