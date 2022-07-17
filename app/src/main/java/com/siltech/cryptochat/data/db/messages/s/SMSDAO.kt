package com.siltech.cryptochat.data.db.messages.s

import androidx.room.*
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.ChatDatas
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.ChatWithSMSDatas
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.SmsDatas

@Dao
interface SMSDAO {
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun addChats(chatssData: ChatDatas)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addChats(chatssData: ChatDatas)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSMS(smsdatas: SmsDatas)

    @Update
    suspend fun updateChat(chatssData: ChatDatas)

//    @Query("SELECT * FROM SmsDatas")
//    suspend fun getAll(): List<ChatWithSMSDatas>

    @Query("SELECT * FROM chaa")
    suspend fun getAll(): List<ChatWithSMSDatas>

    @Query("DELETE FROM SmsDatas")
    suspend fun deleteAllMessages()

    @Query("DELETE FROM chaa")
    suspend fun deleteAllChats()

    @Delete
    suspend fun deleteChat(chatssData: ChatDatas)

    @Query("SELECT * FROM SmsDatas WHERE messagesOwnerId=:id")
    suspend fun getById(id: Int): List<SmsDatas>
}