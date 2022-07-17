package com.siltech.cryptochat.data.db.messages.s

import androidx.room.Embedded
import androidx.room.Relation
//import com.siltech.cryptochat.data.db.messages.ChatsData

data class smswithchats (
    @Embedded val chats: ChatssData,
    @Relation(
        parentColumn = "idChat",
        entityColumn = "messagesOwnerId"
    )
    val sms: SMSDATAS
        )