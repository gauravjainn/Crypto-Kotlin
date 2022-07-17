package com.siltech.cryptochat.model


import com.google.gson.annotations.SerializedName

data class AddNewUsersRequestItem(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("chat_id")
    val chatId: Int,
    @SerializedName("is_administrator")
    val isAdministrator: Boolean

)