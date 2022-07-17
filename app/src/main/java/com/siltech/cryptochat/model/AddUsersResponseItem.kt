package com.siltech.cryptochat.model


import com.google.gson.annotations.SerializedName

data class AddUsersResponseItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("chat_id")
    val chatId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("is_administrator")
    val isAdministrator: Boolean
)