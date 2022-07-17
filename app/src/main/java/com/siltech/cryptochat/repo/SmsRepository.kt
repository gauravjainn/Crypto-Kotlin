package com.siltech.cryptochat.repo

import android.util.Log
import com.androiddevs.firebasenotifications.RetrofitInstance
import com.google.gson.JsonObject
import com.siltech.cryptochat.chat.model.PeersDataModel
import com.siltech.cryptochat.model.*
import com.siltech.cryptochat.network.Api
import com.siltech.cryptochat.network.RetrofitFcmInstance
import com.siltech.cryptochat.network.UpdatePublicKey
import com.siltech.cryptochat.notificationCall.PushNotification
import org.json.JSONObject
import retrofit2.Response
import java.lang.Exception

class SmsRepository(val api: Api) {


//    fun getAllSms(): LiveData<List<UsersChatResponseItem>> = chatDao.getAllChats()

//
//    suspend fun addSms(sms: DocumentsData) {
//        smsDao.addNewSms(sms)
//    }
//    suspend fun deleteSms(sms: DocumentsData){
//        smsDao.deleteSms(sms)
//    }
//    suspend fun updateSms(sms: DocumentsData){
//        smsDao.updateSms(sms)
//    }


//    suspend fun addUser(user: UserData) {
//        chatDao.addNewUser(user)
//    }

//    suspend fun delete(user: UserData){
//        userDao.deleteuser(user)
//    }
//    suspend fun updateUser(user: UserData){
//        userDao.updateUser(user)
//    }
//    fun updateLogin(token: String, user: UpdatePublicKey): Call<List<User>>{
//        return retrofitService.updateLoginPublicKey("Bearer $token", user,"eq.${user.id}")
//    }


    suspend fun updateLogin(
            id: String,
            key: String
    ): Response<CreateUserResponse>? {
        return try {
            api.updateLoginPublicKey(id, key)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUser(
            login: String
    ): Response<CreateUserResponse>? {
        return try {
            api.getUser("eq.$login")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun checkCallUser(
            userId: String
    ): Response<CheckCallUserResponse>? {
        return try {
            Log.e("TAG", "SMSRepository api.checkCallUser called....");
            api.checkCallUser("in.$userId")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserInfop(
            string: String,
            login: String
    ): Response<CreateUserResponse>? {
        return try {
            api.getUserInfo(string, login)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun geerr(
            token: String,
    ): Response<*>? {
        return try {
            api.gerror("Bearer $token")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createNewChat(
            string: String,
            chatCreateModel: ChatCreateModel
    ): Response<CreateChatResponse>? {
        return try {
            api.createNewChat(string, chatCreateModel)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserInfo(token: String, login: String, publicKey: String): Response<CreateUserResponse>? {
        return try {
            api.getUserInfo("Bearer $token", login, publicKey)
        } catch (e: Exception) {
            null
        }
    }

//     fun updateUserInfo(token: String, login: String, publicKey: String): Response<CreateUserResponse>? {
//        return try {
//            api.updateLoginPublicKey("Bearer $token", login, publicKey)
//        } catch (e: Exception) {
//            null
//        }
//    }

    suspend fun getSpecificUser(string: String, login: String): Response<CreateUserResponse>? {
        return try {
            api.getSpecificUser(string, login)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUsersChat(chatName: String): Response<GetChatUsersResponseX>? {
        return try {
            api.getChatUsers(chatName)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteUser(string: String, id: String): Response<DeleteUserResponse>? {
        return try {
            api.deleteUser(string, id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserIdInChat(string: String, userid: String, chatId: String): Response<GetUserIdInChatResponse>? {
        return try {
            api.getUserIdInChat(string, userid, chatId)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getChatForResult(name: String): Response<ChatsListResponse>? {
        return try {
            api.getChatForResult(name)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addUsersIntoChat(string: String, addNewUsersRequest: AddNewUsersRequest): Response<AddUsersResponse>? {
        return try {
            api.addUsersIntoChat(string, addNewUsersRequest)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addNewMessage(string: String, createMessageRequest: CreateMessageRequest): Response<CreateMessageResponse>? {
        return try {
            api.addNewMessage(string, createMessageRequest)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getSmss(string: String, chatName: String): Response<GetMessagesResponse>? {
        return try {
            api.getMessages(string, chatName)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteAllSmss(): Response<*>? {
        return try {
            api.deleteAllMessages()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getChats(userLogin: String): Response<ArrayList<UsersChatResponseItem>>? {
        return try {
            api.getChats(userLogin)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun changeNameOfChat(string: String, id: String, name: String): Response<ChangeNameOfChatResponse>? {
        return try {
            api.changeNameOfChat(string, id, name)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteChat(string: String, id: String): Response<DeletedChatResponse>? {
        return try {
            api.deleteChat(string, id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteMessage(string: String, id: String): Response<DeletedMessageResponse>? {
        return try {
            api.deleteSms(string, id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun changeSMS(string: String, id: String, sms: String): Response<ChangedSMSResponse>? {
        return try {
            api.changeSMS(string, id, sms)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun changeSMSViewedUsers(string: String, id: String, getMsm: changeListOfUsersItem): Response<*>? {
        return try {
            api.changeSMSViewedUsers(string, id, getMsm)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getSpecificSMS(string: String, sms: String, id: String): Response<GetSpecificSMSResponse>? {
        return try {
            api.getSpecificSMS(string, sms, id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getPeersData(string: String, id: String): Response<PeersDataModel>? {
        return try {
            api.getPeersData(string,  id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveFCMToken(jsonObject: JsonObject) = RetrofitFcmInstance.api.saveFCMToken(jsonObject = jsonObject)

    suspend fun sendCallNotification(notification: PushNotification) = RetrofitInstance.api.postNotification(notification)
}
