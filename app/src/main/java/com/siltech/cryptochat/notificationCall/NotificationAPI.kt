package com.androiddevs.firebasenotifications

import com.google.gson.JsonObject
import com.siltech.cryptochat.notificationCall.PushNotification
import com.siltech.cryptochat.utils.CONTENT_TYPE
import com.siltech.cryptochat.utils.SERVER_KEY
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*

interface NotificationAPI {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>

    @PATCH("fcm")
    suspend fun saveFCMToken(
        @Body jsonObject: JsonObject
    ): Response<ResponseBody>

    @PATCH("updatestatus")
    suspend fun updateStatus(
        @Body jsonObject: JsonObject
    ): Response<ResponseBody>
}