package com.siltech.cryptochat.model


import com.google.gson.annotations.SerializedName

data class changeListOfUsersItem(
    @SerializedName("chat_users_id")
    val chatUsersId: Int,
    @SerializedName("date_time")
    val dateTime: String,
    @SerializedName("filetype")
    val filetype: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("viewed_users")
    val viewedUsers: List<Int>
)