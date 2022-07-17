package com.siltech.cryptochat.model


import com.google.gson.annotations.SerializedName

data class CreateMessageRequest(
    @SerializedName("chat_users_id")
    val chatUsersId: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("filetype")
    val filetype: String,
    @SerializedName("viewed_users")
    val viewedUsers: ArrayList<Int>

//    @SerializedName("viewed_users")
//    val viewedUsers: String

)