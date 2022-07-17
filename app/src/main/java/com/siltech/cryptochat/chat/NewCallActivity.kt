package com.siltech.cryptochat.chat

import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.siltech.cryptochat.R
import com.siltech.cryptochat.call.callBanner.CallBannerService
import com.siltech.cryptochat.call.callBanner.OnGoingCallBanner
import com.siltech.cryptochat.call.util.toast
import com.siltech.cryptochat.callUtils.InterfaceCall
import com.siltech.cryptochat.callUtils.PeerConnectionUsers
import com.siltech.cryptochat.chat.callViewModel.CallViewModel
import com.siltech.cryptochat.databinding.ActivityNewCallBinding
import com.siltech.cryptochat.getSecretKey
import com.siltech.cryptochat.utils.*
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewCallActivity : AppCompatActivity(), View.OnClickListener {
    private var fcmToken: String = ""
    private val callViewModel: CallViewModel by viewModel()

    private lateinit var binding: ActivityNewCallBinding
    private lateinit var session: SessionManager
    private var uniqueId = ""
    private var isPeerConnected = false
    private var isAudio = true
    private var isSpeaker = false
    private var callDeclined = false
    private lateinit var am: AudioManager
    private lateinit var mSocket: Socket
    private var peersUserIds = ""
    var token = ""
    override fun onCreate(savedInstanceState: Bundle?) {

//        StrictMode.setThreadPolicy(ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build())
//        StrictMode.setVmPolicy(VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build())

        super.onCreate(savedInstanceState)
        binding = ActivityNewCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        stopCallService()
        init()
        setViews()
        setObservers()
        getPeerData()
        setSocketListener()
        setOnClickListener()
    }


    private fun getPeerData() {
        if (intent.hasExtra("callerUserId")) {
            if (session.userLoggedInID == intent.getStringExtra("callerUserId")) {
                callViewModel.callerUserId = intent.getStringExtra("callerUserId").toString()
                callViewModel.receiverUserId = intent.getStringExtra("receiverUserId").toString()
            } else {
                callViewModel.receiverUserId = intent.getStringExtra("callerUserId").toString()
                callViewModel.callerUserId = intent.getStringExtra("receiverUserId").toString()
            }
        }
    }

    private fun setSocketListener() {
        mSocket.on("callUser", onCallConnected)
    }

    private val onCallConnected: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            try {

                val data = args[0] as JSONObject
                val signalData = data.getString("signal")
                val gson = Gson()
                val peerData = gson.fromJson(signalData, PeerConnectionUsers::class.java)

                if (peerData.connected!!) {

                    if (peerData.peerConnected!! == 1) {
                        callViewModel.emitSocketForCallConnected(
                            connId = peerData.connId!!,
                            callConnectedBy = 2
                        )
                    }

                    binding.speakerBtn.setImageResource(R.drawable.btn_loudspeaker_mute)
                    lifecycleScope.launch {
                        delay(350)
                        am.isSpeakerphoneOn = false

                    }

                    binding.chronometer.base = SystemClock.elapsedRealtime()
                    binding.chronometer.start()

                    toast("Connected to Server: divishanetworks.com")
                    if (peerData?.available!! && peerData.connected) {
//                                        startService(Intent(this@NewCallActivity, OnGoingCallBanner::class.java).apply {
//                                            putExtra("callerUserId", callViewModel.callerUserId)
//                                            putExtra("receiverUserId", callViewModel.receiverUserId)
//                                            val otherUserName = if (intent.hasExtra("newCall")) {
//                                                intent.getStringExtra("otherUserName").toString()
//                                            } else {
//                                                intent.getStringExtra("userCallingName").toString()
//                                            }
//                                            putExtra("callerName", otherUserName)
//                                        })
                    }


                    binding.chronometer.setOnChronometerTickListener {
                        val timeRunning = SystemClock.elapsedRealtime() - it.base
                        if (timeRunning > 120000) {
                            callViewModel.emitSocketForCallDisconnect()
                            callViewModel.updateCallStatus(false)
                            stopOnGoingCall()
                            finish()
                        }
                    }
                }

                if (!peerData?.available!! && !peerData.connected) {
                    callViewModel.updateCallStatus(false)
                    stopOnGoingCall()
                    finish()
                }
            } catch (e: Exception) {
                println("${e.message}")
            }
        }

    }


    private val permissionsResultCallback =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            checkCallRequiredPermission()
        }

    private fun checkCallRequiredPermission() {
        if (checkCallRequiredPermissions(this).isEmpty()) {
            setUpWebView()
        } else {
            permissionsResultCallback.launch(checkCallRequiredPermissions(this).toTypedArray())
        }
    }

    private fun init() {
        callViewModel.updateCallStatus(true)
        session = SessionManager(this)
        mSocket = SocketManager.instance?.getSocket()!!
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        am = getSystemService(AUDIO_SERVICE) as AudioManager
        am.mode = AudioManager.MODE_IN_CALL

        peersUserIds =
            "in.(${intent.getStringExtra("callerUserId")},${intent.getStringExtra("receiverUserId")})"
        token = "Bearer ${getSecretKey(this)}"
        callViewModel.getPeersDetails(token, peersUserIds)
    }

    private fun setViews() {
        binding.chronometer.text = "Connecting..."
        binding.callingUserName.text = if (intent.hasExtra("newCall")) {
            intent.getStringExtra("otherUserName").toString()[0].toString()
        } else {
            intent.getStringExtra("userCallingName").toString()[0].toString()
        }
    }

    private fun setObservers() {
        var callStatus = false
        var otherUserId = ""
        callViewModel.peersData.observe(this) { response ->
            when (response) {
                is Resources.Success -> {
                    response.data!!.forEach { peerData ->
                        if (peerData.id.toString() == session.userLoggedInID) {
                            callViewModel.callerSocketId = peerData.socket_id
                        } else {
                            callViewModel.receiverSocketId = peerData.socket_id
                            otherUserId = peerData.id.toString()
                            fcmToken = peerData.fcm_token
                            callStatus = peerData.on_call as Boolean
                        }
                    }

                    if (otherUserId == callViewModel.receiverUserId) {
                        checkCallRequiredPermission()
                    } else {
                        if (!callStatus) {
                            checkCallRequiredPermission()
                        } else {
                            binding.chronometer.text = "Busy on other call"
                            binding.callControls.visibility = View.VISIBLE
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


    private fun setOnClickListener() {
        with(binding) {
            micBtn.setOnClickListener(this@NewCallActivity)
            speakerBtn.setOnClickListener(this@NewCallActivity)
            endCall.setOnClickListener(this@NewCallActivity)
        }
    }


    private fun setUpWebView() {
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.resources)
                }
            }
        }
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.mediaPlaybackRequiresUserGesture = false
        binding.webView.addJavascriptInterface(InterfaceCall(this), "Android")
        loadCall()
    }

    private fun loadCall() {
        val filePath = "file:android_asset/call.html"
        binding.webView.loadUrl(filePath)
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (!callDeclined) {
                    initializePeer()
                }
            }
        }
    }


    fun initializePeer() {
        uniqueId = getUniqueId()
        callJavaScriptFunction("javascript:init(\"$uniqueId\")")
        lifecycleScope.launch {
            delay(3000)
            sendCallRequest()
        }
    }

    fun onPeerConnected() {
        isPeerConnected = true
    }

    fun onPeerError() {
        Log.d("TAG","==Error===")
    }

    private fun sendCallRequest() {
        if (!isPeerConnected) {
            checkCallRequiredPermission()
            return
        }

        listenConnId()
    }

    private fun listenConnId() {

        val connId = if (intent.hasExtra("newCall") && intent.getStringExtra("newCall") != null) {
            intent.getStringExtra("connId")!!
        } else {
            uniqueId
        }

        Log.d("TAG==", "newCallActivity $connId")

        if (intent.hasExtra("newCall")) {
            binding.callControls.visibility = View.VISIBLE
            callJavaScriptFunction("javascript:startCall(\"$connId\")")
            callViewModel.emitSocketForCallConnected(connId, callConnectedBy = 1)
        } else {
            binding.callControls.visibility = View.VISIBLE
            callJavaScriptFunction("javascript:startCall(\"$connId\")")
            callViewModel.initiateCall(intent, connId, fcmToken)
        }
    }

    private fun callJavaScriptFunction(function: String?) {
        with(binding) { webView.post { webView.evaluateJavascript(function!!, null) } }
    }

    private fun stopCallService() {
        if (startService(Intent(this, CallBannerService::class.java)) != null) {
            stopService(Intent(this, CallBannerService::class.java).apply {
                action = "ACTION_STOP_FOREGROUND_SERVICE"
            })
        }
    }

    private fun stopOnGoingCall() {
        if (startService(Intent(this, OnGoingCallBanner::class.java)) != null) {
            stopService(Intent(this, OnGoingCallBanner::class.java).apply {
                action = "ACTION_STOP_FOREGROUND_SERVICE"
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopOnGoingCall()
        callViewModel.updateCallStatus(false)
        binding.webView.loadUrl("about:blank")
        callViewModel.emitSocketForCallDisconnect()
        callDeclined = !callDeclined
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.micBtn -> {
                isAudio = !isAudio
                callJavaScriptFunction("javascript:toggleAudio(\"$isAudio\")")
                if (isAudio) {
                    binding.micBtn.setImageResource(R.drawable.btn_unmute_normal)
                } else {
                    binding.micBtn.setImageResource(R.drawable.btn_mute_normal)
                }
            }

            R.id.speakerBtn -> {
                isSpeaker = !isSpeaker
                if (isSpeaker) {
                    am.isSpeakerphoneOn = true
                    binding.speakerBtn.setImageResource(R.drawable.btn_loud_speaker)
                } else {
                    binding.speakerBtn.setImageResource(R.drawable.btn_loudspeaker_mute)
                    am.isSpeakerphoneOn = false
                }
            }

            R.id.endCall -> {
                callViewModel.getPeersDetails(token, peersUserIds)
                callViewModel.sendCallDeclineNotification(fcmToken)
                callViewModel.emitSocketForCallDisconnect()
                callViewModel.updateCallStatus(false)
                stopOnGoingCall()
                finish()
            }
        }
    }
}

