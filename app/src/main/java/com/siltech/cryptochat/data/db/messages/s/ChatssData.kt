package com.siltech.cryptochat.data.db.messages.s

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chatzs")
data class ChatssData (
    @PrimaryKey(autoGenerate = true)
    val idChat:Int,
    val name: String
        )