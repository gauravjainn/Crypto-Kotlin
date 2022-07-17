package com.siltech.cryptochat.model

import com.google.gson.annotations.SerializedName

data class ChatCreateModel(
    @SerializedName("name")
    val name: String,
    @SerializedName("creator_id")
    val creator_id: Int

)
