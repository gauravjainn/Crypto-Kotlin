package com.siltech.cryptochat.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class CreateMessageResponseItem(
    @SerializedName("chat_users_id")
    val chatUsersId: Int,
    @SerializedName("date_time")
    val dateTime: String,
    @SerializedName("filetype")
    val filetype: String,


    @SerializedName("id")
    val id: Int,

    @SerializedName("message")
    val message: String
)