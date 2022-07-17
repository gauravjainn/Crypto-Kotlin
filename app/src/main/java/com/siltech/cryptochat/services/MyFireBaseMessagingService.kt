package com.siltech.cryptochat.services

import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.siltech.cryptochat.call.callBanner.AnswerCallActivity
import com.siltech.cryptochat.call.callBanner.CallBannerService
import com.siltech.cryptochat.utils.isAppIsInBackground
import android.widget.Toast





class MyFireBaseMessagingService : FirebaseMessagingService() {
    val TAG = "Service"


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Notification Message Body: ${remoteMessage.data}")
        val data = remoteMessage.data
        setCallNotification(data)

    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    private fun setCallNotification(data: MutableMap<String, String>) {
        val uid = data["uid"]
        val userCallingName = data["userCallingName"]
        val otherUserName = data["otherUserName"]
        val callerUserId = data["callerUserId"]
        val receiverUserId = data["receiverUserId"]
        val callerSocketId = data["callerSocketId"]
        val receiverSocketId = data["receiverSocketId"]
        val connId = data["connId"]

        if (isAppIsInBackground(this)) {
            if (!connId.isNullOrEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(
                        Intent(
                            applicationContext,
                            CallBannerService::class.java
                        ).apply {
                            putExtra("uid", uid)
                            putExtra("userCallingName", userCallingName)
                            putExtra("otherUserName", otherUserName)
                            putExtra("callerUserId", callerUserId)
                            putExtra("receiverUserId", receiverUserId)
                            putExtra("callerSocketId", callerSocketId)
                            putExtra("receiverSocketId", receiverSocketId)
                            putExtra("connId", connId)
                        })
                } else {
                    startService(Intent(applicationContext, CallBannerService::class.java).apply {
                        putExtra("uid", uid)
                        putExtra("userCallingName", userCallingName)
                        putExtra("otherUserName", otherUserName)
                        putExtra("callerUserId", callerUserId)
                        putExtra("receiverUserId", receiverUserId)
                        putExtra("callerSocketId", callerSocketId)
                        putExtra("receiverSocketId", receiverSocketId)
                        putExtra("connId", connId)
                    })
                }

            } else {
                if (startService(Intent(this, CallBannerService::class.java)) != null) {
                    stopService(Intent(this, CallBannerService::class.java).apply {
                        action = "ACTION_STOP_FOREGROUND_SERVICE"
                    })
                }
            }
        } else {
            if (!connId.isNullOrEmpty()) {
                startActivity(Intent(this, AnswerCallActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("uid", uid)
                    putExtra("userCallingName", userCallingName)
                    putExtra("otherUserName", otherUserName)
                    putExtra("callerUserId", callerUserId)
                    putExtra("receiverUserId", receiverUserId)
                    putExtra("callerSocketId", callerSocketId)
                    putExtra("receiverSocketId", receiverSocketId)
                    putExtra("connId", connId)
                })
            } else {
                val intent = Intent("finish_activity")
                sendBroadcast(intent)
            }
        }
    }
}