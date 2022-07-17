package com.siltech.cryptochat.callUtils

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.siltech.cryptochat.app.AppModule
import com.siltech.cryptochat.chat.NewCallActivity
import com.siltech.cryptochat.getSecretKey
import com.siltech.cryptochat.utils.RetrofitIntances.RetrofitGetPeerInstance
import com.siltech.cryptochat.utils.SessionManager
import com.siltech.cryptochat.utils.SocketManager
import com.siltech.cryptochat.webRtcNative.WebRtcCallActivity
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AcceptBroadCastReceiver: BroadcastReceiver() {
    lateinit var mSocket: Socket
    lateinit var session: SessionManager

    override fun onReceive(context: Context?, intent: Intent) {
        mSocket = SocketManager.instance?.getSocket()!!
        setSocket(context, intent)
    }

    private fun setSocket(context: Context?, intent: Intent) {
        Log.d("TAG== ","Accept ${intent.getStringExtra("callerUserId")}")
        Log.d("TAG== ","Accept ${intent.getStringExtra("connId")}")
        callSocketForDiCline(
            context!!,
            intent
        )
    }

    private fun callSocketForDiCline(
        context: Context,
        intent: Intent,
    ) {
            val acceptIntent = Intent(context, WebRtcCallActivity::class.java).apply {
                putExtra("newCall","yes")
                putExtra("userCallingName", intent.getStringExtra("userCallingName"))
                putExtra("otherUserName", intent.getStringExtra("otherUserName"))
                putExtra("callerUserId", intent.getStringExtra("callerUserId"))
                putExtra("receiverUserId", intent.getStringExtra("receiverUserId"))
                putExtra("callerSocketId", intent.getStringExtra("callerSocketId"))
                putExtra("receiverSocketId", intent.getStringExtra("receiverSocketId"))
                putExtra("connId", intent.getStringExtra("connId"))
            }
        val pendingIntents = PendingIntent.getActivity(context, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        pendingIntents.send()
    }
}