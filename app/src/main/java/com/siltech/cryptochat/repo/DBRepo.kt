package com.siltech.cryptochat.repo

import com.siltech.cryptochat.data.db.messages.s.SMSDAO
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.ChatDatas
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.ChatWithSMSDatas
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.SmsDatas

class DBRepo(private val chatsDAO: SMSDAO) {

    suspend fun getAllUsers(): List<ChatWithSMSDatas> =
        chatsDAO.getAll()

    suspend fun deleteAllSMS() =
        chatsDAO.deleteAllMessages()

    suspend fun deleteAllChats() =
        chatsDAO.deleteAllChats()

    suspend fun getById(id:Int): List<SmsDatas> = chatsDAO.getById(id)

    suspend fun addChat(user: ChatDatas) {
        chatsDAO.addChats(user)
    }
    suspend fun addSMS(smsdatas: SmsDatas) {
        chatsDAO.addSMS(smsdatas)
    }
    suspend fun updateChat(chatDatas: ChatDatas) {
        chatsDAO.updateChat(chatDatas)
    }
    suspend fun deleteChat(chatDatas: ChatDatas) {
        chatsDAO.deleteChat(chatDatas)
    }
}
