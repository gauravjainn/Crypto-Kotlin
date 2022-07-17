package com.siltech.cryptochat.chat

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.siltech.cryptochat.chat.model.PeersDataModel
import com.siltech.cryptochat.data.State
import com.siltech.cryptochat.model.*
import com.siltech.cryptochat.repo.SmsRepository
import com.siltech.cryptochat.utils.Resources
import com.siltech.cryptochat.utils.SessionManager
import com.siltech.cryptochat.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import org.json.JSONObject

class ChatViewModel constructor(context: Application, private val repository: SmsRepository) :
        AndroidViewModel(context) {

    val smsList: MutableLiveData<GetMessagesResponse> = MutableLiveData()

    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = _state

    val peersData = SingleLiveEvent<Resources<PeersDataModel>>()

    val chatUsersData = SingleLiveEvent<Resources<CheckCallUserResponse>>()

    val errorMessage = MutableLiveData<String>()
    val userModel = MutableLiveData<Boolean>()
    private val session = SessionManager(context)
    init {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result!!
            saveFCMToken(token)
        })
    }

    private fun saveFCMToken(token: String) {
        viewModelScope.launch {
            val jsonObject = JsonObject()
            jsonObject.addProperty("userId",session.userLoggedInID!!)
            jsonObject.addProperty("fcmToken",token)
            val response = repository.saveFCMToken(jsonObject)
            when {

                response.isSuccessful -> {
                    if (response.body() != null) {
                        Log.d("TAG", "FCM TOKEN Saved Successfully")
                    } else {
                        Log.d("TAG", "FCM TOKEN Not Saved")
                    }
                }
                else -> {
                    Log.d("TAG", "FCM TOKEN Not Saved ${response.message()}")
                }
            }
        }
    }


    fun getSMSS(string: String, chatName: String) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.getSmss(string, chatName)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no cuccessss")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessListState(response.body()!!)
                        val log = response.body()
                        smsList.value = response.body()
                        Log.d("AAA", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
                else -> {

                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun deleteAllSMSS() {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.deleteAllSmss()
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no cuccessss")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessObjectState(response.body()!!)
                        val log = response.body()
                        Log.d("AAA", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
                else -> {

                }
            }
            _state.value = State.LoadingState(false)
        }
    }


//
//    val getAllUsers: LiveData<List<UsersChatResponseItem>>
//
//    init {
//        getAllUsers = repository.getAllSms()
//    }

//    val getAllSms: LiveData<List<DocumentsData>>
//
//    private var repo: SmsRepository

//    init {
//        val smsMessagesDao = MessagesDataBase.getInstanceDataBase(context).smsDao()
//        repo = SmsRepository(smsMessagesDao, api)
//        getAllSms = repo.getAllSms()
//    }

    //    fun addSms(sms: DocumentsData){
//        viewModelScope.launch(Dispatchers.IO) {
//            repo.addSms(sms)
//
//        }
//    }
//    fun deleteSms(sms: DocumentsData){
//        viewModelScope.launch(Dispatchers.IO) {
//            repo.deleteSms(sms)
//        }
//    }

    fun createNewChat(string: String, chatCreateModel: ChatCreateModel) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.createNewChat(string, chatCreateModel)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no cuccessss")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessObjectState(response.body()!!)
                        val log = response.body()
                        Log.d("AAA", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
                else -> {
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun getUserInfo(login: String) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.getUser(login)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no cuccessss")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessObjectState(response.body()!!)
                        Log.d("AAA", "successsssssssssssssssss")
                        if (response.body()!![0].publicKey == null) {
                            Log.e("public key is emtpy", "public key is emtpy")
                        } else {
                            Log.e("public key is not emtpy", "public key is not emtpy")
                        }
                    } else {
                        _state.value = State.NoItemState
                    }
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun checkCallUser(userId: String) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.checkCallUser(userId)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.e("checkCallUser", "response == null")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessObjectState(response.body()!!)
                        Log.e("checkCallUser", "response != null")
                        if (response.body()!![0].isBlocked) {
                            Log.e("checkCallUser", "isBlocked == true")
                        } else {
                            Log.e("checkCallUser", "isBlocked == false")
                        }
                    } else {
                        _state.value = State.NoItemState
                    }
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun getUserInfoCurrent(string: String, login: String) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.getUserInfop(string, login)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no cuccessss")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessObjectState(response.body()!!)
                        Log.e("public key is emtpy", "public key is emtpy")
                        val log = response.body()
                        Log.d("AAA", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun gg(token: String) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.geerr(token)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no cuccessss")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessObjectState(response.body()!!)
                        val log = response.body()
                        Log.d("AAA", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun updateUserInfoPublicKey(id: String, publicKey: String) {
        Log.d("When Empty", "updateUserInfoPublicKey id : " + id + " publicKey : " + publicKey)
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.updateLogin(id, publicKey)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no cuccessss")
                }

                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessObjectState(response.body()!!)
                        val log = response.body()
                        Log.d("updated", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    //   fun create(token: String, user: User) {
    //
    //        val response = repository.create(token, user)
    //        response!!.enqueue(object : Callback<Void?> {
    //            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
    //                if (response.isSuccessful) {
    //                    Log.e("SUCCESS", "SUCCESS: ${response.message()}")
    //                    userModel.postValue(response.isSuccessful)
    //                    val log = response.body()
    //                } else {
    //                    Log.e("ERROR", "ERROR: ${response.message()}")
    ////                    Log.e("ERROR", "ERROR: ${response.raw().message}")
    //                }
    //            }
    //
    //            override fun onFailure(call: Call<Void?>, t: Throwable) {
    //                errorMessage.postValue(t.message)
    //                Log.e("Error", "onCreate: ${t.message}")
    //            }
    //        })
    //    }

    // class QuickExample {
    //
    //    fun function(argument: SomeOtherClass) {
    //        if (argument.mutableProperty != null ) {
    //            doSomething(argument.mutableProperty)
    //        } else {
    //            doOtherThing()
    //        }
    //    }
    fun getSpecificUserByLogin(string: String, login: String) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.getSpecificUser(string, login)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no cuccessss")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessObjectState(response.body()!!)
                        val log = response.body()
                        Log.d("AAA", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
                else -> {
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun deleteUser(string: String, id: String) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.deleteUser(string, id)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no cuccessss")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessObjectState(response.body()!!)
                        val log = response.body()
                        Log.d("AAA", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
                else -> {
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun getUsersChat(chatName: String) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.getUsersChat(chatName)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no cuccessss")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessListState(response.body()!!)
                        val log = response.body()
                        Log.d("AAA", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
                else -> {
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun getUserIdInChat(string: String, userId: String, chatId: String) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.getUserIdInChat(string, userId, chatId)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no cuccessss")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessObjectState(response.body()!!)
                        val log = response.body()
                        Log.d("AAA", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
                else -> {
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun getChatForResult(name: String) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.getChatForResult(name)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no cuccessss")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessObjectState(response.body()!!)
                        val log = response.body()
                        Log.d("AAA", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
                else -> {
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun addUsersIntoChat(string: String, addNewUsersRequest: AddNewUsersRequest) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.addUsersIntoChat(string, addNewUsersRequest)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no success put Users Into Chat")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessObjectState(response.body())
                        val log = response.body()
                        Log.d("AAA", "succes put users into chat")
                    } else {
                        Log.d("AAA", "response is null")
                        _state.value = State.NoItemState
                    }
                }
                else -> {
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun createMessage(string: String, createMessageRequest: CreateMessageRequest) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.addNewMessage(string, createMessageRequest)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("CHAT", "no successss")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessObjectState(response.body()!!)
                        val log = response.body()
                        Log.d("CHAT", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
                else -> {
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

//    fun getSMSS(string: String, chatName: String) {
//        viewModelScope.launch {
//            _state.value = State.LoadingState(true)
//            val response = repository.getSmss(string, chatName)
//            when {
//                response == null -> {
//                    _state.value = State.ErrorState("", 0)
//                    Log.d("AAA", "no cuccessss")
//                }
//                response.isSuccessful -> {
//                    if (response.body() != null) {
//                        _state.value = State.SuccessListState(response.body()!!)
//                        val log = response.body()
//                        Log.d("AAA", "successsssssssssssssssss")
//                    } else {
//                        _state.value = State.NoItemState
//                    }
//                }
//                else -> {
//                }
//            }
//            _state.value = State.LoadingState(false)
//        }
//    }

    fun getChats(userLogin: String) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.getChats(userLogin)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no cuccessss")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessListState(response.body()!!)
                        val log = response.body()
                        Log.d("AAA", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
                else -> {
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun changeNameOfChat(string: String, id: String, name: String) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.changeNameOfChat(string, id, name)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no cuccessss")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessObjectState(response.body())
                        val log = response.body()
                        Log.d("AAA", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
                else -> {
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun deleteChat(string: String, id: String) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.deleteChat(string, id)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no cuccessss")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessObjectState(response.body())
                        val log = response.body()
                        Log.d("AAA", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
                else -> {
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun changeSMS(string: String, id: String, sms: String) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.changeSMS(string, id, sms)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no cuccessss")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessObjectState(response.body())
                        val log = response.body()
                        Log.d("AAA", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
                else -> {
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun changeSMSViewedUsers(string: String, id: String, getMsm: changeListOfUsersItem) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.changeSMSViewedUsers(string, id, getMsm)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no cuccessss")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessObjectState(response.body())
                        val log = response.body()
                        Log.d("AAA", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
                else -> {
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun deleteSMS(string: String, id: String) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.deleteMessage(string, id)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no cuccessss")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessObjectState(response.body())
                        val log = response.body()
                        Log.d("AAA", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
                else -> {
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun getSpecifciSMS(string: String, sms: String, id: String) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repository.getSpecificSMS(string, sms, id)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", 0)
                    Log.d("AAA", "no cuccessss")
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value = State.SuccessObjectState(response.body())
                        val log = response.body()
                        Log.d("AAA", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
                else -> {
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun getPeersDetails(string: String,  id: String) {
        viewModelScope.launch {
            peersData.postValue(Resources.Loading())
            val response = repository.getPeersData(string,  id)
            when {
                response == null -> {
                    peersData.postValue(Resources.Error("SomeThing went wrong!!"))

                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        response.body()?.let {responseData->
                            peersData.postValue(Resources.Success(responseData))
                        }

                        _state.value = State.SuccessObjectState(response.body())
                        val log = response.body()
                        Log.d("AAA", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
                else -> {
                }
            }
            _state.value = State.LoadingState(false)
        }
    }

    fun getUsersChatData(id: String) {

        viewModelScope.launch {
            chatUsersData.postValue(Resources.Loading())
            val response = repository.checkCallUser(id)
            when {
                response == null -> {
                    chatUsersData.postValue(Resources.Error("SomeThing went wrong!!"))

                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        response.body()?.let {responseData->
                            chatUsersData.postValue(Resources.Success(responseData))
                        }

                        _state.value = State.SuccessObjectState(response.body())
                        val log = response.body()
                        Log.d("AAA", "successsssssssssssssssss")
                    } else {
                        _state.value = State.NoItemState
                    }
                }
                else -> {
                }
            }
            _state.value = State.LoadingState(false)
        }
    }
}

//
//    fun updateSms(sms: Ms){
//        viewModelScope.launch(Dispatchers.IO) {
//
//        }
//    }
