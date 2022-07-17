package com.siltech.cryptochat.chat.callViewModel


import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.firebasenotifications.RetrofitInstance
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.siltech.cryptochat.callUtils.PeerConnectionUsers
import com.siltech.cryptochat.chat.model.PeersDataModel
import com.siltech.cryptochat.network.RetrofitFcmInstance
import com.siltech.cryptochat.notificationCall.NotificationData
import com.siltech.cryptochat.notificationCall.PushNotification
import com.siltech.cryptochat.repo.SmsRepository
import com.siltech.cryptochat.utils.Resources
import com.siltech.cryptochat.utils.RetrofitIntances.RetrofitGetPeerInstance
import com.siltech.cryptochat.utils.SessionManager
import com.siltech.cryptochat.utils.SingleLiveEvent
import com.siltech.cryptochat.utils.SocketManager
import io.socket.client.Socket
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class CallViewModel constructor(val context: Application, private val repository: SmsRepository) :
    AndroidViewModel(context) {

    val peersData = SingleLiveEvent<Resources<PeersDataModel>>()
    var session = SessionManager(context)
    var mSocket:Socket = SocketManager.instance?.getSocket()!!
    var emitSocketCallForConnected = SingleLiveEvent<Resources<String>>()

    var callerSocketId = ""
    var receiverSocketId = ""
    var callerUserId = ""
    var receiverUserId = ""


    fun getPeersDetails(string: String, id: String) {
        viewModelScope.launch {
            peersData.postValue(Resources.Loading())
            val response = repository.getPeersData(string, id)
            when {
                response == null -> {
                    peersData.postValue(Resources.Error("SomeThing went wrong!!"))
                }

                response.isSuccessful -> {
                    if (response.body() != null) {
                        response.body()?.let { responseData ->
                            peersData.postValue(Resources.Success(responseData))
                        }
                    }
                }
                else -> {
                    peersData.postValue(Resources.Error(response.message()))
                }

            }

        }
    }

   fun updateCallStatus(status:Boolean) {
        viewModelScope.launch {
            val jsonObject = JsonObject()
            jsonObject.addProperty("userId",session.userLoggedInID)
            jsonObject.addProperty("status",status.toString())
            val response = RetrofitFcmInstance.api.updateStatus(jsonObject)
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        Log.d("TAG","Call Status Updated")
                    }
                }
                else -> {
                    Log.d("TAG","Call Status Update Error ${response.message()}")
                }
            }
        }
    }



    fun initiateCall(intent: Intent,connId:String, fcmToken: String){
        viewModelScope.launch {
            PushNotification(
                NotificationData(
                    uid = intent.getStringExtra("uid"),
                    userCallingName = intent.getStringExtra("userCallingName"),
                    otherUserName = intent.getStringExtra("otherUserName"),
                    callerUserId = intent.getStringExtra("callerUserId"),
                    receiverUserId = intent.getStringExtra("receiverUserId"),
                    callerSocketId = intent.getStringExtra("callerSocketId"),
                    receiverSocketId = intent.getStringExtra("receiverSocketId"),
                    connId = connId
                ),
                fcmToken
            ).also {
                val response = repository.sendCallNotification(it)
                when{
                    response.isSuccessful->{
                        if(response.body()!=null){
                            response.body()?.let {

                                Log.d("TAG","Notification for Call Success")
                            }
                        }
                    }else -> {
                    Log.d("TAG","Notification for Call Error")
                    peersData.postValue(Resources.Error(response.message()))
                  }
                }
            }


        }
    }

    fun sendCallDeclineNotification(fcmToken: String) {

        viewModelScope.launch {
            PushNotification(
                NotificationData(connId = ""),
                fcmToken
            ).also {
                val response = repository.sendCallNotification(it)
                when{
                    response.isSuccessful->{
                        if(response.body()!=null){
                            response.body()?.let {

                                Log.d("TAG","Notification for Call Success")
                            }
                        }
                    }else -> {
                    Log.d("TAG","Notification for Call Error")
                    peersData.postValue(Resources.Error(response.message()))
                }
                }
            }
        }
    }


    fun emitSocketForCallConnected(
        connId: String,
        callConnectedBy: Int,

    ) {
        if(mSocket.connected()){
            val peerConnectionUsers = PeerConnectionUsers(
                connId = connId,
                userID = session.userLoggedInID,
                available = true,
                connected = true,
                callerSocketId = callerSocketId,
                receiverSocketId = receiverSocketId,
                peerConnected = callConnectedBy
            )

            val userCallObject = JSONObject()
            val peerJson = Gson().toJson(peerConnectionUsers)
            try {
                userCallObject.put("userToCall", peerConnectionUsers.receiverSocketId)
                userCallObject.put("from", peerConnectionUsers.callerSocketId)
                userCallObject.put("receiverId", receiverUserId)
                userCallObject.put("userId", callerUserId)
                userCallObject.put("signalData", peerJson)
                userCallObject.put("name", "Android")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            mSocket.emit("callUser", userCallObject)
            emitSocketCallForConnected.postValue(Resources.Success("Call Connected"))
        }else{
            emitSocketCallForConnected.postValue(Resources.Error("Socket Not Connected"))
        }
    }

    fun emitSocketForCallDisconnect(){
        val peerConnectionUsers = PeerConnectionUsers(
            connId = "",
            userID = session.userLoggedInID,
            available = false,
            connected = false,
            callerSocketId = callerSocketId,
            receiverSocketId = receiverSocketId,
            peerConnected = 0
        )

        val peerJson = Gson().toJson(peerConnectionUsers)
        val userCallObject = JSONObject()

        try {
            userCallObject.put("userToCall", receiverSocketId)
            userCallObject.put("from", callerSocketId)
            userCallObject.put("receiverId", receiverUserId)
            userCallObject.put("userId", callerUserId)
            userCallObject.put("signalData", peerJson)
            userCallObject.put("name", "Android")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        mSocket.emit("callUser", userCallObject)
    }
    fun emitSocketToExChangeIceCandidates(message: JSONObject) {
        val peerConnectionUsers = PeerConnectionUsers(
            connId = "iceCandidates",
            userID = session.userLoggedInID,
            available = true,
            connected = true,
            callerSocketId = callerSocketId,
            receiverSocketId = receiverSocketId,
            peerConnected = 2,
            message = message
        )

        val peerJson = Gson().toJson(peerConnectionUsers)
        val userCallObject = JSONObject()

        try {
            userCallObject.put("userToCall", receiverSocketId)
            userCallObject.put("from", callerSocketId)
            userCallObject.put("receiverId", receiverUserId)
            userCallObject.put("userId", callerUserId)
            userCallObject.put("signalData", peerJson)
            userCallObject.put("name", "Android")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        mSocket.emit("callUser", userCallObject)
    }
}