package com.siltech.cryptochat.model

import com.google.gson.annotations.SerializedName

data class CreateUserResponseItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_blocked")
    val isBlocked: Boolean,
    @SerializedName("login")
    val login: String,
    @SerializedName("public_key")
    val publicKey: String?,
    @SerializedName("role")
    val role: String ,
    @SerializedName("socket_id")
    val socket_id: String
)
