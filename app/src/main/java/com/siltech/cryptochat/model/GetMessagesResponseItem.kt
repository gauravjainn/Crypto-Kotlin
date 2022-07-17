package com.siltech.cryptochat.model


import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

data class GetMessagesResponseItem(
    @SerializedName("chat_name")
    val chatName: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("user_login")
    val userLogin: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("date_time")
    val date: String,
    @SerializedName("filetype")
    val filetype: String,
    @SerializedName("message_id")
    val messageId: Int,
    @SerializedName("chat_id")
    val chatId: Int,
    @SerializedName("chat_users_id")
    val chatUsersId: Int,
    @SerializedName("viewed_users")
    val viwedUsers: ArrayList<Int>

){
    var id: String =UUID.randomUUID().toString()

}