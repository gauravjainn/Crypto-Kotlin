package com.siltech.cryptochat.data.db.messages.s.ffff.aa

import androidx.room.Embedded
import androidx.room.Relation

data class ChatWithSMSDatas(
    @Embedded val owner: ChatDatas,
    @Relation(
        parentColumn = "idChat",
        entityColumn = "messagesOwnerId",
    )
    val sms: List<SmsDatas>
)