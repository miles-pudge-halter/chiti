package team.penicillin.penicillin.network.models

import com.google.gson.annotations.SerializedName

data class NotiModel(
    @SerializedName("message")
    var noti: String
)
data class NotiData(
    @SerializedName("id")
    var id: Int,
    @SerializedName("data_id")
    var dataId: Int,
    @SerializedName("noti_type")
    var notiType: String,
    @SerializedName("title")
    var title: String,
    @SerializedName("description")
    var body: String,
    @SerializedName("created_at")
    var createdAt: String
)

data class NotiListResponse(
    @SerializedName("status")
    var status: String,
    @SerializedName("msg")
    var msg: String,
    @SerializedName("notis")
    var notis: List<NotiData> = listOf()
)