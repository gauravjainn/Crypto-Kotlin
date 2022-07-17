package com.siltech.cryptochat.webRtcNative

import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.siltech.cryptochat.R
import com.siltech.cryptochat.call.callBanner.CallBannerService
import com.siltech.cryptochat.call.callBanner.OnGoingCallBanner
import com.siltech.cryptochat.call.util.toast
import com.siltech.cryptochat.callUtils.PeerConnectionUsers
import com.siltech.cryptochat.chat.SimpleSdpObserver
import com.siltech.cryptochat.chat.callViewModel.CallViewModel
import com.siltech.cryptochat.databinding.ActivityNewCallBinding
import com.siltech.cryptochat.getSecretKey
import com.siltech.cryptochat.utils.*
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.webrtc.*
import org.webrtc.MediaConstraints.KeyValuePair
import org.webrtc.PeerConnection.*

class WebRtcCallActivity : AppCompatActivity(), View.OnClickListener {
    private var fcmToken: String = ""
    private val callViewModel: CallViewModel by viewModel()

    private lateinit var binding: ActivityNewCallBinding
    private lateinit var session: SessionManager
    private var isAudio = true
    private var isSpeaker = false
    private var callDeclined = false
    private lateinit var am: AudioManager
    private lateinit var mSocket: Socket
    private var peersUserIds = ""
    var token = ""


    private var factory: PeerConnectionFactory? = null
    var audioConstraints: MediaConstraints? = null
    var audioSource: AudioSource? = null
    var localAudioTrack: AudioTrack? = null
    private var peerConnection: PeerConnection? = null

    var connectedStream: MediaStream? = null
    var mediaStream: MediaStream? = null

    override fun onCreate(savedInstanceState: Bundle?) {
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
                if (peerData.connId == "iceCandidates") {
                    val message = peerData.message
                    val candidate = IceCandidate(
                        message?.getString("id"),
                        message?.getInt("label")!!,
                        message.getString("candidate")
                    )
                    peerConnection!!.addIceCandidate(candidate)
                } else {
                    if (peerData.connected!!) {
                        peerConnection!!.setRemoteDescription(
                            SimpleSdpObserver(),
                            SessionDescription(SessionDescription.Type.ANSWER, peerData.connId)
                        )
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
                        peerConnection?.removeStream(mediaStream)
                    }
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
            initializePeerConnectionFactory()
            createVideoTrackFromCameraAndShowIt()
            initializePeerConnections()
            startStreamingVideo()

            if (intent.hasExtra("newCall")) {
                peerConnection!!.setRemoteDescription(
                    SimpleSdpObserver(),
                    SessionDescription(
                        SessionDescription.Type.OFFER,
                        intent.getStringExtra("connId")
                    )
                )
                doAnswer()
            } else {
                sendCallRequest()
            }
        } else {
            permissionsResultCallback.launch(checkCallRequiredPermissions(this).toTypedArray())
        }
    }

    private fun startStreamingVideo() {
        mediaStream = factory!!.createLocalMediaStream("ARDAMS")
        mediaStream?.addTrack(localAudioTrack)
        peerConnection!!.addStream(mediaStream)
    }

    private fun createVideoTrackFromCameraAndShowIt() {
        audioConstraints = MediaConstraints()
        audioSource = factory!!.createAudioSource(audioConstraints)
        localAudioTrack = factory!!.createAudioTrack("101", audioSource)
    }

    private fun initializePeerConnectionFactory() {
        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true)
        factory = PeerConnectionFactory(null)
    }

    private fun initializePeerConnections() {
        peerConnection = createPeerConnection(factory!!)
    }

    private fun init() {
        callViewModel.updateCallStatus(true)
        session = SessionManager(this)
        mSocket = SocketManager.instance?.getSocket()!!
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        am = getSystemService(AUDIO_SERVICE) as AudioManager
        am.mode = AudioManager.MODE_IN_CALL

        peersUserIds = "in.(${intent.getStringExtra("callerUserId")},${intent.getStringExtra("receiverUserId")})"
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
            micBtn.setOnClickListener(this@WebRtcCallActivity)
            speakerBtn.setOnClickListener(this@WebRtcCallActivity)
            endCall.setOnClickListener(this@WebRtcCallActivity)
        }
    }

    private fun sendCallRequest() {
        val sdpMediaConstraints = MediaConstraints()
        sdpMediaConstraints.mandatory.add(KeyValuePair("OfferToReceiveAudio", "true"))

        peerConnection!!.createOffer(object : SimpleSdpObserver() {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                Log.d("TAG", "onCreateSuccess: ")
                peerConnection!!.setLocalDescription(SimpleSdpObserver(), sessionDescription)
                callViewModel.initiateCall(intent, sessionDescription.description, fcmToken)
                runOnUiThread {
                    binding.callControls.visibility = View.VISIBLE
                }

            }
        }, sdpMediaConstraints)
    }

    private fun doAnswer() {
        peerConnection!!.createAnswer(object : SimpleSdpObserver() {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                peerConnection!!.setLocalDescription(SimpleSdpObserver(), sessionDescription)
                callViewModel.emitSocketForCallConnected(
                    sessionDescription.description,
                    callConnectedBy = 1
                )
                runOnUiThread {
                    binding.callControls.visibility = View.VISIBLE
                }


            }
        }, MediaConstraints())
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
        peerConnection?.removeStream(mediaStream)
        callViewModel.updateCallStatus(false)
        callViewModel.emitSocketForCallDisconnect()
        callDeclined = !callDeclined
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.micBtn -> {
                isAudio = !isAudio
                if (isAudio) {
                    am.isMicrophoneMute = false
                    binding.micBtn.setImageResource(R.drawable.btn_unmute_normal)
                } else {
                    am.isMicrophoneMute = true
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
                peerConnection?.removeStream(mediaStream)
                callViewModel.sendCallDeclineNotification(fcmToken)
                callViewModel.emitSocketForCallDisconnect()
                callViewModel.updateCallStatus(false)
                stopOnGoingCall()
                finish()
            }
        }
    }

    private fun createPeerConnection(factory: PeerConnectionFactory): PeerConnection? {
        val iceServers = ArrayList<IceServer>()
        iceServers.add(IceServer("stun:stun.l.google.com:19302"))
        iceServers.add(IceServer("stun:stun.divishanetworks.com:3478"))
        iceServers.add(
            IceServer(
                "turn:turn.divishanetworks.com:3478?transport=udp",
                "gaurav",
                "Champ@123"
            )
        )
        iceServers.add(
            IceServer(
                "turn:turn.divishanetworks.com:5349?transport=tcp",
                "gaurav",
                "Champ@123"
            )
        )

        val rtcConfig = RTCConfiguration(iceServers)
        val pcConstraints = MediaConstraints()
        val pcObserver: Observer = object : Observer {
            override fun onSignalingChange(signalingState: SignalingState) {
                Log.d("TAG", "onSignalingChange: ")
            }

            override fun onIceConnectionChange(iceConnectionState: IceConnectionState) {
                Log.d("TAG", "onIceConnectionChange: ")
            }

            override fun onIceConnectionReceivingChange(b: Boolean) {
                Log.d("TAG", "onIceConnectionReceivingChange: ")
            }

            override fun onIceGatheringChange(iceGatheringState: IceGatheringState) {
                Log.d(
                    "TAG", "onIceGatheringChange: "
                )
            }

            override fun onIceCandidate(iceCandidate: IceCandidate) {
                Log.d("TAG", "onIceCandidate: ")
                val message = JSONObject()
                try {
                    message.put("type", "candidate")
                    message.put("label", iceCandidate.sdpMLineIndex)
                    message.put("id", iceCandidate.sdpMid)
                    message.put("candidate", iceCandidate.sdp)
                    Log.d("TAG", "onIceCandidate: sending candidate $message")
                    callViewModel.emitSocketToExChangeIceCandidates(message)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {
                Log.d("TAG", "onIceCandidatesRemoved: ")
            }

            override fun onAddStream(mediaStream: MediaStream) {
                connectedStream = mediaStream
                val remoteAudioTrack = mediaStream.audioTracks[0]
                remoteAudioTrack.setEnabled(true)

            }

            override fun onRemoveStream(mediaStream: MediaStream) {
                Log.d("TAG", "onRemoveStream: ")
            }

            override fun onDataChannel(dataChannel: DataChannel) {
                Log.d("TAG", "onDataChannel: ")
            }

            override fun onRenegotiationNeeded() {
                Log.d("TAG", "onRenegotiationNeeded: ")
            }
        }
        return factory.createPeerConnection(rtcConfig, pcConstraints, pcObserver)
    }


}

