package com.siltech.cryptochat.model


import com.google.gson.annotations.SerializedName

data class ChatsListResponseItem(
    @SerializedName("creator_id")
    val creatorId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)