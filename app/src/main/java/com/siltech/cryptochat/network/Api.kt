package com.siltech.cryptochat.network

import com.google.gson.annotations.SerializedName
import com.siltech.cryptochat.chat.model.PeersDataModel
import com.siltech.cryptochat.model.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

data class UpdatePublicKey(
        val id: Int? = null,
        val login: String? = null,
        val role: String? = null,
        @SerializedName("public_key")
        val publicKey: String? = null,
        @SerializedName("is_blocked")
        val isBlocked: Boolean = false
)

interface Api {

    @POST("user")
    fun createPost(
            @Header("Authorization") token: String,
            @Body user: User
    ): Call<Void>?

    //http://194.67.110.76:3000/user?id=eq.245
    @FormUrlEncoded
    @PATCH("user")
    suspend fun updateLoginPublicKey(
            @Query("id") id: String,
            @Field("public_key") publicKey: String
    ): Response<CreateUserResponse>

    //http://194.67.110.76:3000/user?login=eq.jibek@mail.ru
    @GET("user")
    suspend fun getUser(
            @Query("login") login: String
    ): Response<CreateUserResponse>

    @GET("user")
    suspend fun checkCallUser(
            @Query("login") userId: String
    ): Response<CheckCallUserResponse>

    @GET("user")
    suspend fun getUserInfo(
            @Header("Prefer") string: String,
            @Query("login") login: String
    ): Response<CreateUserResponse>


    @GET("user")
    suspend fun gerror(
            @Header("Authorization") token: String,
    ): Response<*>
//////////////////////////////////

    @POST("chat")
    suspend fun createNewChat(
            @Header("Prefer") string: String,
            @Body chatCreateModel: ChatCreateModel
    ): Response<CreateChatResponse>

    @GET("user")
    suspend fun getUserInfo(
            @Header("Authorization") token: String,
            @Query("login") id: String,
            @Query("public_key") publicKey: String
    ): Response<CreateUserResponse>

    @GET("user")
    suspend fun getSpecificUser(
            @Header("Prefer") string: String,
            @Query("login") id: String
    ): Response<CreateUserResponse>

    @POST("chat_users")
    suspend fun addUsersIntoChat(
            @Header("Prefer") string: String,
            @Body addNewUsersRequest: AddNewUsersRequest
    ): Response<AddUsersResponse>

    @GET("chat_users_info")
    suspend fun getChatUsers(
            @Query("chat_name") chatName: String
    ): Response<GetChatUsersResponseX>

    @DELETE("chat_users")
    suspend fun deleteUser(
            @Header("Prefer") string: String,
            @Query("id") id: String
    ): Response<DeleteUserResponse>

    @GET("chat_users")
    suspend fun getUserIdInChat(
            @Header("Prefer") string: String,
            @Query("user_id") userId: String,
            @Query("chat_id") chatId: String
    ): Response<GetUserIdInChatResponse>

    @GET("chat")
    suspend fun getChatForResult(
            @Query("name") userLogin: String
    ): Response<ChatsListResponse>

    @GET("user")
    suspend fun getUserByLogin(
            @Query("login") userLogin: String
    ): Response<ChatsListResponse>

    @POST("message_history")
    suspend fun addNewMessage(
            @Header("Prefer") string: String,
            @Body createMessageRequest: CreateMessageRequest
    ): Response<CreateMessageResponse>

    @GET("message_history_info")
    suspend fun getMessages(
            @Header("Prefer") string: String,
            @Query("chat_name") chatName: String
    ): Response<GetMessagesResponse>

    @DELETE("message_history")
    suspend fun deleteAllMessages(
    ): Response<*>

    @GET("chat_users_info")
    suspend fun getChats(
            @Query("user_login") userLogin: String
    ): Response<ArrayList<UsersChatResponseItem>>

    @FormUrlEncoded
    @PATCH("chat")
    suspend fun changeNameOfChat(
            @Header("Prefer") string: String,
            @Query("id") id: String,
            @Field("name") name: String
    ): Response<ChangeNameOfChatResponse>

    @DELETE("chat")
    suspend fun deleteChat(
            @Header("Prefer") string: String,
            @Query("id") id: String
    ): Response<DeletedChatResponse>

    @DELETE("message_history")
    suspend fun deleteSms(
            @Header("Prefer") string: String,
            @Query("id") id: String
    ): Response<DeletedMessageResponse>

    @FormUrlEncoded
    @PATCH("message_history")
    suspend fun changeSMS(
            @Header("Prefer") string: String,
            @Query("id") id: String,
            @Field("message") name: String
    ): Response<ChangedSMSResponse>

    @PATCH("message_history")
    suspend fun changeSMSViewedUsers(
            @Header("Prefer") string: String,
            @Query("id") id: String,
            @Body getMsm: changeListOfUsersItem
    ): Response<*>

    @GET("message_history")
    suspend fun getSpecificSMS(
            @Header("Prefer") string: String,
            @Query("message") sms: String,
            @Query("chat_users_id") chatUsersId: String
    ): Response<GetSpecificSMSResponse>

    @GET("user")
    suspend fun getPeersData(
        @Header("Prefer") string: String,
        @Query("id") id: String
    ): Response<PeersDataModel>

    @PATCH("fcm")
    suspend fun saveFCMToken(
        @Query("userId") id: String,
        @Query("fcmToken") fcmToken: String
    ): Response<ResponseBody>
}
