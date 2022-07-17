package com.siltech.cryptochat.model


import com.google.gson.annotations.SerializedName

data class DeleteUserResponseItem(
    @SerializedName("chat_id")
    val chatId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_administrator")
    val isAdministrator: Boolean,
    @SerializedName("user_id")
    val userId: Int
)