package com.siltech.cryptochat.model


import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

var uniqueID = UUID.randomUUID().toString()

data class UsersChatResponseItem(

    @SerializedName("chat_name")
    val chatName: String,
    @SerializedName("is_administrator")
    val isAdministrator: Boolean,
    @SerializedName("role")
    val role: String,
    @SerializedName("user_login")
    val userLogin: String,
    @SerializedName("chat_id")
    val chatId: Int,


    )