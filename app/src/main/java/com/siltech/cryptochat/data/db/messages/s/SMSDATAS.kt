package com.siltech.cryptochat.data.db.messages.s

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "smsko")
data class SMSDATAS (
    @PrimaryKey(autoGenerate = true)
    val idSms:Int,
    val sms: String,
    var messagesOwnerId: Long

    )