package com.siltech.cryptochat.model


import com.google.gson.annotations.SerializedName

data class GetChatUsersResponseItemX(
    @SerializedName("chat_name")
    val chatName: String,
    @SerializedName("is_administrator")
    val isAdministrator: Boolean,
    @SerializedName("user_login")
    val userLogin: String,
    @SerializedName("user_id")
    val userId: String
)