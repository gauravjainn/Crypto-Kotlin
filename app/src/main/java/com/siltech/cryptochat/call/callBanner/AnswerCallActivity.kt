package com.siltech.cryptochat.call.callBanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.siltech.cryptochat.R
import com.siltech.cryptochat.call.util.toast
import com.siltech.cryptochat.chat.NewCallActivity
import com.siltech.cryptochat.chat.callViewModel.CallViewModel
import com.siltech.cryptochat.databinding.ActivityAnswerCallBinding
import com.siltech.cryptochat.getSecretKey
import com.siltech.cryptochat.utils.Resources
import com.siltech.cryptochat.utils.SessionManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.content.IntentFilter

import android.media.Ringtone
import android.media.RingtoneManager
import com.siltech.cryptochat.webRtcNative.WebRtcCallActivity


class AnswerCallActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityAnswerCallBinding
    private lateinit var session: SessionManager
    private val callViewModel: CallViewModel by viewModel()
    private var userCallingName: String = ""
    private var otherUserName: String = ""
    private var callerUserId: String = ""
    private var receiverUserId: String = ""
    private var callerSocketId: String = ""
    private var receiverSocketId: String = ""
    private var connId: String = ""
    private lateinit var ringtone: Ringtone
    private var token = ""
    private var peerUserIds = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnswerCallBinding.inflate(layoutInflater)
        init()
        setContentView(binding.root)
        checkAndStopService()
        getIntentData()
        getPeerData()
        setOnClickListeners()
    }

    private fun init() {
        session = SessionManager(this)
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(applicationContext, notification)
        ringtone.play()
        registerReceiver(broadcastReceiver, IntentFilter("finish_activity"))
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(arg0: Context?, intent: Intent) {
            val action = intent.action
            if (action == "finish_activity") {
                finish()
            }
        }
    }

    private fun getPeerData() {
        peerUserIds = "in.(${intent.getStringExtra("callerUserId")},${intent.getStringExtra("receiverUserId")})"
        token = "Bearer ${getSecretKey(this)}"
        println("userIds $peerUserIds")
        callViewModel.getPeersDetails(token, peerUserIds)
        callViewModel.peersData.observe(this) { response ->
            when (response) {
                is Resources.Success -> {
                    response.data!!.forEach { peerData ->
                        if (peerData.id.toString() == session.userLoggedInID) {
                            callViewModel.callerSocketId = peerData.socket_id
                        } else {
                            callViewModel.receiverSocketId = peerData.socket_id
                        }
                    }
                }
                is Resources.Loading -> {
                }
                is Resources.Error -> {
                    toast(response.message.toString())
                }
            }
        }
    }

    private fun getIntentData() {
        with(intent) {
            userCallingName = getStringExtra("userCallingName").toString()
            otherUserName = getStringExtra("otherUserName").toString()
            callerUserId = getStringExtra("callerUserId").toString()
            receiverSocketId = getStringExtra("receiverSocketId").toString()
            receiverUserId = getStringExtra("receiverUserId").toString()
            callerUserId = getStringExtra("callerUserId").toString()
            connId = getStringExtra("connId").toString()
            callViewModel.callerUserId = callerUserId
            callViewModel.receiverUserId = receiverUserId
        }
    }

    private fun setOnClickListeners() {
        with(binding) {
            acceptCall.setOnClickListener(this@AnswerCallActivity)
            declineCall.setOnClickListener(this@AnswerCallActivity)
        }
    }

    private fun checkAndStopService() {
        if (startService(Intent(this, CallBannerService::class.java)) != null) {
            stopService(Intent(this, CallBannerService::class.java).apply {
                action = "ACTION_STOP_FOREGROUND_SERVICE"
            })
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.acceptCall -> {
                goToNewCallActivity()
                ringtone.stop()
            }

            R.id.declineCall -> {
                emitCallDeclineSocket()
                ringtone.stop()
            }
        }
    }

    private fun goToNewCallActivity() {
        startActivity(Intent(this, WebRtcCallActivity::class.java).apply {
            putExtra("newCall", "yes")
            putExtra("userCallingName", userCallingName)
            putExtra("otherUserName", otherUserName)
            putExtra("callerUserId", callerUserId)
            putExtra("receiverUserId", receiverUserId)
            putExtra("callerSocketId", callerSocketId)
            putExtra("receiverSocketId", receiverSocketId)
            putExtra("connId", connId)
        })
        finish()
    }

    private fun emitCallDeclineSocket() {
        callViewModel.getPeersDetails(token,peerUserIds)
        callViewModel.emitSocketForCallDisconnect()
        finish()
    }

    override fun onPause() {
        super.onPause()
        if(ringtone.isPlaying){
            ringtone.stop()
        }

    }
}