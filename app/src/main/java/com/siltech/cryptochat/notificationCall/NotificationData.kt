package com.siltech.cryptochat.notificationCall

import org.json.JSONObject

data class NotificationData(
   val uid:String?="",
   val userCallingName:String?="",
   val otherUserName:String?="",
   val callerUserId:String?="",
   val receiverUserId:String?="",
   val callerSocketId:String?="",
   val receiverSocketId:String?="",
   val connId:String?="",
   val jsonObject: Any?=null,
)