package com.siltech.cryptochat.model


import com.google.gson.annotations.SerializedName

data class GetSpecificSMSResponseItem(
    @SerializedName("chat_users_id")
    val chatUsersId: Int,
    @SerializedName("date_time")
    val dateTime: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("message")
    val message: String
)