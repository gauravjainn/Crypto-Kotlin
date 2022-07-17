package com.siltech.cryptochat.data.db.messages.s.ffff.aa

import androidx.room.*
import com.google.gson.Gson
import com.siltech.cryptochat.model.UsersChatResponseItem
import java.util.*

@Entity(tableName = "chaa")
data class ChatDatas(
//    @TypeConverters(ChatsTypeConverter::class)
    @PrimaryKey(autoGenerate = true)
    val idChat: Int,
    val name: String,
    val nameDB : String = "name"

)

data class Chatss(val idChat: Int, val name: String)

class ChatsTypeConverter {
    @TypeConverter
    fun listToJson(value: List<UsersChatResponseItem>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String?) =
        Gson().fromJson(value, Array<UsersChatResponseItem>::class.java).toList()
}