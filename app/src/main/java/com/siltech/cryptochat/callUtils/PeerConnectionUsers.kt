package com.siltech.cryptochat.callUtils

import org.json.JSONObject

data class PeerConnectionUsers (
    val connId:String?="",
    val userID:String?="",
    val available:Boolean?=false,
    val connected:Boolean?=false,
    val callerSocketId:String?="",
    val receiverSocketId:String?="",
    val peerConnected:Int?=0,
    val userCallingName:String?="",
    val message:JSONObject?=null
)