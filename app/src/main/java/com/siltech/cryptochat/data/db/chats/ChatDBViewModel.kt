import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.siltech.cryptochat.data.db.messages.s.SMSDATABASE
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.ChatDatas
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.SmsDatas
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.ChatWithSMSDatas
import com.siltech.cryptochat.repo.DBRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//package com.siltech.cryptochat.data.db.chats
//
//import android.app.Application
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.viewModelScope
//import com.siltech.cryptochat.data.db.MessagesDataBase
//import com.siltech.cryptochat.data.db.messages.ChatWithMessages
//import com.siltech.cryptochat.data.db.messages.MessagesDB
//import com.siltech.cryptochat.data.db.messages.MessagesData
//import com.siltech.cryptochat.model.CreateChatResponse
//import com.siltech.cryptochat.model.CreateChatResponseItem
//import com.siltech.cryptochat.model.CreateMessageResponseItem
//import com.siltech.cryptochat.model.UsersChatResponseItem
//import com.siltech.cryptochat.repo.DBRepo
//import com.siltech.cryptochat.repo.SmsRepository
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
class ChatDBViewModel(context: Application): AndroidViewModel(context) {


    suspend fun  getAlls(id: Int): List<SmsDatas>{
        viewModelScope.launch(Dispatchers.IO) {
            repo.getById(id)
        }
        return repo.getById(id)
    }
    suspend fun  deleteAlls(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAllSMS()
        }
        return repo.deleteAllSMS()
    }

    suspend fun deleteAllChats(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAllChats()
        }
        return repo.deleteAllSMS()
    }

    suspend fun  getAllChats(): List<ChatWithSMSDatas>{
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllUsers()
        }
        return repo.getAllUsers()
    }

    private val repo: DBRepo

    init {
        val smsDao = SMSDATABASE.getInstance(context).getDao()
        repo = DBRepo(smsDao)
    }

     fun addChat(user: ChatDatas){
        viewModelScope.launch(Dispatchers.IO) {
            repo.addChat(user)
        }
    }

    fun addSMS(user: SmsDatas){
        viewModelScope.launch(Dispatchers.IO) {
            repo.addSMS(user)
        }
    }

    fun updateChat(chatDatas: ChatDatas){
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateChat(chatDatas)
        }
    }

  fun deleteChat(chatDatas: ChatDatas){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteChat(chatDatas)
        }
    }

}

//

//
//    fun addSMS(user: MessagesData){
//        viewModelScope.launch(Dispatchers.IO) {
//            repo.addSMS(user)
//        }
//    }
//
//}