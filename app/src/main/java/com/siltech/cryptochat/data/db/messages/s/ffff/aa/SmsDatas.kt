package com.siltech.cryptochat.data.db.messages.s.ffff.aa

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

var uniqueIDInt = UUID.randomUUID().toString()

@Entity(
//    foreignKeys = arrayOf(
//        ForeignKey(
//            entity = ChatDatas::class,
//            parentColumns = arrayOf("idChat"),
//            childColumns = arrayOf("messagesOwnerId"),
//            onDelete = ForeignKey.CASCADE,
//            onUpdate = ForeignKey.CASCADE))
            )
data class SmsDatas (
    @PrimaryKey(autoGenerate = true)
    val idSms:Int ,
    val sms: String,
    var messagesOwnerId: Long,
    var userLogin: String,
    var type: String,
    var fileType: String,
    var date: String
)