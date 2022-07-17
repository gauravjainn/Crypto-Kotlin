package com.siltech.cryptochat.callUtils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.gson.Gson
import com.siltech.cryptochat.app.AppModule
import com.siltech.cryptochat.call.callBanner.CallBannerService
import com.siltech.cryptochat.call.callBanner.OnGoingCallBanner
import com.siltech.cryptochat.utils.SessionManager
import com.siltech.cryptochat.utils.SocketManager
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject
import com.siltech.cryptochat.getSecretKey
import com.siltech.cryptochat.utils.RetrofitIntances.RetrofitGetPeerInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HungUpBroadCastReceiver : BroadcastReceiver() {
    lateinit var mSocket: Socket
    lateinit var session: SessionManager

    override fun onReceive(context: Context?, intent: Intent) {
        mSocket = SocketManager.instance?.getSocket()!!
        setSocket(context, intent)
    }

    private fun setSocket(context: Context?, intent: Intent) {
        Log.d("TAG== ","Decline ${intent.getStringExtra("callerUserId")}")
        Log.d("TAG== ","Decline ${intent.getStringExtra("receiverUserId")}")
        session = SessionManager(context!!)
        if (!mSocket.connected()) {
            session = SessionManager(AppModule.context)
            val publicKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiMDQzZDU4NjU3NDE2MzNiNyJ9.eQBCfKO38fAT2oZ9m5L6Ty2UtBshSu-ggWCoqCopBKg"
            val socketUrl = "https://cryptochatapi.herokuapp.com"
            SocketManager.instance!!.connectSocket(
                session.userLoggedInID.toString(),
                publicKey,
                socketUrl
            )
        }

        var callerSocketId = ""
        var receiverSocketId = ""
        val callerUserId = intent.getStringExtra("callerUserId")
        val receiverUserId = intent.getStringExtra("receiverUserId")


        val userId = "in.(${intent.getStringExtra("callerUserId")},${intent.getStringExtra("receiverUserId")})"
        val token = "Bearer ${getSecretKey(context)}"

        CoroutineScope(Dispatchers.IO).launch {
            val getPeers = RetrofitGetPeerInstance.api.getPeersData(token, userId)
            if (getPeers.isSuccessful) {
                getPeers.body().let { responseData ->
                    responseData?.forEach { peerData ->
                        if (peerData.id.toString() == session.userLoggedInID) {
                            callerSocketId = peerData.socket_id
                        } else {
                            receiverSocketId = peerData
                                .socket_id
                        }
                    }
                    callSocketForDiCline(
                        context,
                        callerUserId,
                        receiverUserId,
                        callerSocketId,
                        receiverSocketId
                    )
                }
            }
        }
    }

    private fun callSocketForDiCline(
        context: Context,
        callerUserId: String?,
        receiverUserId: String?,
        callerSocketId: String?,
        receiverSocketId: String?
    ) {
        if (mSocket.connected()) {
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

            context.stopService(Intent(context, CallBannerService::class.java).apply {
                action = "ACTION_STOP_FOREGROUND_SERVICE"
            })

            context.stopService(Intent(context, OnGoingCallBanner::class.java).apply {
                action = "ACTION_STOP_FOREGROUND_SERVICE"
            })

        }

    }
}