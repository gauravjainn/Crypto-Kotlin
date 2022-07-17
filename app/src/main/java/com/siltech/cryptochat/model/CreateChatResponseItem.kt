package com.siltech.cryptochat.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
@Entity(tableName = "chat")
data class CreateChatResponseItem(

    @PrimaryKey
    @SerializedName("id")
    val id: Int,
    @SerializedName("creator_id")
    val creatorId: Int,
    @SerializedName("name")
    val name: String
)