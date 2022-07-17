package com.siltech.cryptochat.chat

import android.Manifest
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.siltech.cryptochat.R
import com.siltech.cryptochat.call.util.toast
import com.siltech.cryptochat.callUtils.PeerConnectionUsers
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException
import java.util.ArrayList
import com.siltech.cryptochat.databinding.ActivitySamplePeerConnectionBinding
import com.siltech.cryptochat.utils.SessionManager
import com.siltech.cryptochat.utils.SocketManager
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.AfterPermissionGranted
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.webrtc.*
import org.webrtc.PeerConnection.IceServer
import org.webrtc.PeerConnection.RTCConfiguration
import org.webrtc.PeerConnection.SignalingState
import org.webrtc.PeerConnection.IceConnectionState
import org.webrtc.PeerConnection.IceGatheringState

class ChatUserCallActivity : AppCompatActivity() {
    private var socket: Socket? = null
    private var isInitiator = false
    private var isChannelReady = false
    private var isStarted = false
    var audioConstraints: MediaConstraints? = null
    var videoConstraints: MediaConstraints? = null
    var sdpConstraints: MediaConstraints? = null
    var videoSource: VideoSource? = null
    var localVideoTrack: VideoTrack? = null
    var audioSource: AudioSource? = null
    var localAudioTrack: AudioTrack? = null
    var surfaceTextureHelper: SurfaceTextureHelper? = null
    private var binding: ActivitySamplePeerConnectionBinding? = null
    private var peerConnection: PeerConnection? = null
    private var rootEglBase: EglBase? = null
    private var factory: PeerConnectionFactory? = null
    private var videoTrackFromCamera: VideoTrack? = null


    private lateinit var session: SessionManager
    private var uniqueId = ""
    private var isPeerConnected = false
    private var isAudio = true
    private var isSpeaker = false
    private var callDeclined = false
    private lateinit var am: AudioManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sample_peer_connection)
        setSupportActionBar(binding?.toolbar)
        init()
        start()
    }
    private fun init(){
        session = SessionManager(this)
        socket = SocketManager.instance?.getSocket()
        socket?.on("callUser", onCallConnected)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onDestroy() {
        if (socket != null) {
//            sendMessage("bye")
//            socket!!.disconnect()
        }
        super.onDestroy()
    }

    @AfterPermissionGranted(RC_CALL)
    private fun start() {
        val perms = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        if (EasyPermissions.hasPermissions(this, *perms)) {
//            connectToSignallingServer()
            connectToSignalServer()
            connectToPeers()
            initializeSurfaceViews()
            initializePeerConnectionFactory()
            createVideoTrackFromCameraAndShowIt()
            initializePeerConnections()
            startStreamingVideo()
        } else {
            EasyPermissions.requestPermissions(this, "Need some permissions", RC_CALL, *perms)
        }
    }

    private fun connectToPeers() {
        isInitiator = true
        isChannelReady = true

    }

    private fun connectToSignalServer(){

        isInitiator = true
        isChannelReady = true

    }

    private val onCallConnected: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            try {
                val data = args[0] as JSONObject
                val signalData = data.getString("signal")
                val gson = Gson()
                val peerData = gson.fromJson(signalData, PeerConnectionUsers::class.java)

            /*    if (peerData.message is String) {
                    val message = peerData.message
                    if (message == "got user media") {
                        maybeStart()
                    }
                } else {
                    val data = args[0] as JSONObject
                    val signalData = data.getString("signal")
                    val gson = Gson()
                    val peerData = gson.fromJson(signalData, PeerConnectionUsers::class.java)

                    val message = JSONObject(peerData.message.toString())



                    Log.e(TAG, "connectToSignallingServer: got message $message")
                    if (message.getString("type") == "offer") {
                        Log.e(
                            TAG,
                            "connectToSignallingServer: received an offer $isInitiator $isStarted"
                        )
                        if (!isInitiator && !isStarted) {
                            maybeStart()
                        }
                        peerConnection!!.setRemoteDescription(
                            SimpleSdpObserver(),
                            SessionDescription(
                                SessionDescription.Type.OFFER,
                                message.getString("sdp")
                            )
                        )
                        doAnswer()
                    } else if (message.getString("type") == "answer" && isStarted) {
                        peerConnection!!.setRemoteDescription(
                            SimpleSdpObserver(),
                            SessionDescription(
                                SessionDescription.Type.ANSWER,
                                message.getString("sdp")
                            )
                        )
                    } else if (message.getString("type") == "candidate" && isStarted) {
                        Log.e(TAG, "connectToSignallingServer: receiving candidates")
                        val candidate = IceCandidate(
                            message.getString("id"),
                            message.getInt("label"),
                            message.getString("candidate")
                        )
                        peerConnection!!.addIceCandidate(candidate)
                    }

                }*/
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

    }


    private fun connectToSignallingServer() {
        try {

            val URL = "https://messangerchatserver.herokuapp.com/"
            val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiNzA5MmRiMmQ1MzAzZTEzNSJ9.kyMcUV6mOF6wRe0Q80wBaFRP86F6oDmqLv4HflGpZ8I"

//            val URL = "https://cryptochatapi.herokuapp.com/?EIO=4&transport=polling&user_id=224&token=$token"
            Log.e(TAG, "REPLACE ME: IO Socket:$URL")

            socket = IO.socket(URL)
//            Log.e(TAG, "REPLACE ME: IO Socket: ID..."+socket?.id())
            socket?.on(Socket.EVENT_CONNECT) { args: Array<Any?>? ->
                Log.e(TAG, "connectToSignallingServer: connect")
                socket?.emit("create or join", "foo")
            }?.on("ipaddr") { args: Array<Any?>? ->
                Log.e(
                        TAG,
                        "connectToSignallingServer: ipaddr"
                )
            }
                    ?.on("created") { args: Array<Any?>? ->
                        Log.e(TAG, "connectToSignallingServer: created")
                        isInitiator = true
                    }
                    ?.on("full") { args: Array<Any?>? -> Log.e(TAG, "connectToSignallingServer: full") }
                    ?.on("join") { args: Array<Any?>? ->
                        Log.e(TAG, "connectToSignallingServer: join")
                        Log.e(
                                TAG,
                                "connectToSignallingServer: Another peer made a request to join room"
                        )
                        Log.e(TAG, "connectToSignallingServer: This peer is the initiator of room")
                        isChannelReady = true
                    }?.on("joined") { args: Array<Any?>? ->
                        Log.e(TAG, "connectToSignallingServer: joined")
                        isChannelReady = true
                    }?.on("log") { args: Array<Any> ->
                        for (arg in args) {
                            Log.e(TAG, "connectToSignallingServer: $arg")
                        }
                    }?.on("message") { args: Array<Any?>? ->
                        Log.e(
                                TAG,
                                "connectToSignallingServer: got a message"
                        )
                    }
                    ?.on("message") { args: Array<Any> ->
                        try {
                            if (args[0] is String) {
                                val message = args[0] as String
                                if (message == "got user media") {
                                    maybeStart()
                                }
                            } else {
                                val message = args[0] as JSONObject
                                Log.e(TAG, "connectToSignallingServer: got message $message")
                                if (message.getString("type") == "offer") {
                                    Log.e(
                                            TAG,
                                            "connectToSignallingServer: received an offer $isInitiator $isStarted"
                                    )
                                    if (!isInitiator && !isStarted) {
                                        maybeStart()
                                    }
                                    peerConnection!!.setRemoteDescription(
                                            SimpleSdpObserver(),
                                            SessionDescription(
                                                    SessionDescription.Type.OFFER,
                                                    message.getString("sdp")
                                            )
                                    )
                                    doAnswer()
                                } else if (message.getString("type") == "answer" && isStarted) {
                                    peerConnection!!.setRemoteDescription(
                                            SimpleSdpObserver(),
                                            SessionDescription(
                                                    SessionDescription.Type.ANSWER,
                                                    message.getString("sdp")
                                            )
                                    )
                                } else if (message.getString("type") == "candidate" && isStarted) {
                                    Log.e(TAG, "connectToSignallingServer: receiving candidates")
                                    val candidate = IceCandidate(
                                            message.getString("id"),
                                            message.getInt("label"),
                                            message.getString("candidate")
                                    )
                                    peerConnection!!.addIceCandidate(candidate)
                                }
                                /*else if (message === 'bye' && isStarted) {
                            handleRemoteHangup();
                        }*/
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }?.on(Socket.EVENT_DISCONNECT) { args: Array<Any?>? ->
                        Log.e(
                                TAG,
                                "connectToSignallingServer: disconnect"
                        )
                    }
            socket?.connect()

            if(socket?.connected() == true){
                Log.d("TAG","Connected")
            }else{
                Log.d("TAG","DisConnected")
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    private fun initializeSurfaceViews() {
        rootEglBase = EglBase.create()
        binding!!.surfaceView.init(rootEglBase?.eglBaseContext, null)
        binding!!.surfaceView.setEnableHardwareScaler(true)
        binding!!.surfaceView.setMirror(true)
        binding!!.surfaceView2.init(rootEglBase?.eglBaseContext, null)
        binding!!.surfaceView2.setEnableHardwareScaler(true)
        binding!!.surfaceView2.setMirror(true)

        //add one more
    }

    private fun initializePeerConnectionFactory() {
        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true)
        factory = PeerConnectionFactory(null)
        factory!!.setVideoHwAccelerationOptions(
                rootEglBase!!.eglBaseContext,
                rootEglBase!!.eglBaseContext
        )
    }

    private fun createVideoTrackFromCameraAndShowIt() {
        audioConstraints = MediaConstraints()
        val videoCapturer = createVideoCapturer()
        val videoSource = factory!!.createVideoSource(videoCapturer)
        videoCapturer!!.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS)
        videoTrackFromCamera = factory!!.createVideoTrack(VIDEO_TRACK_ID, videoSource)
        videoTrackFromCamera?.setEnabled(true)
        videoTrackFromCamera?.addRenderer(VideoRenderer(binding!!.surfaceView))

        //create an AudioSource instance
        audioSource = factory!!.createAudioSource(audioConstraints)
        localAudioTrack = factory!!.createAudioTrack("101", audioSource)
    }

    private fun initializePeerConnections() {
        peerConnection = createPeerConnection(factory)
    }

    private fun startStreamingVideo() {
        val mediaStream = factory!!.createLocalMediaStream("ARDAMS")
        mediaStream.addTrack(videoTrackFromCamera)
        mediaStream.addTrack(localAudioTrack)
        peerConnection!!.addStream(mediaStream)

        sendMessage("got user media", "call")
    }

    //MirtDPM4
    private fun doAnswer() {
        peerConnection!!.createAnswer(object : SimpleSdpObserver() {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                peerConnection!!.setLocalDescription(SimpleSdpObserver(), sessionDescription)
                val message = JSONObject()
                try {
                    message.put("type", "answer")
                    message.put("sdp", sessionDescription.description)
                    sendMessage(message, "call")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, MediaConstraints())
    }

    private fun maybeStart() {
        Log.e(TAG, "maybeStart: $isStarted $isChannelReady")
        if (!isStarted && isChannelReady) {
            isStarted = true
            if (isInitiator) {
                doCall()
            }
        }
    }

    private fun doCall() {
        val sdpMediaConstraints = MediaConstraints()
        sdpMediaConstraints.mandatory.add(
                MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
        )
        sdpMediaConstraints.mandatory.add(
                MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true")
        )
        peerConnection!!.createOffer(object : SimpleSdpObserver() {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                Log.e(TAG, "onCreateSuccess: ")
                peerConnection!!.setLocalDescription(SimpleSdpObserver(), sessionDescription)
                val message = JSONObject()
                try {
                    message.put("type", "offer")
                    message.put("sdp", sessionDescription.description)
                    sendMessage(message,"call")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, sdpMediaConstraints)
    }

    private fun sendMessage(message: Any, isCall: String) {
        socket = SocketManager.instance?.getSocket()
        if (socket!!.connected()) {
            var callerSocketId = ""
            var receiverSocketId = ""
            var userCallingName = ""

            var callerUserId = ""
            var receiverUserId = ""

            if (intent.hasExtra("callerSocketId")) {
                callerSocketId = intent.getStringExtra("callerSocketId").toString()
            }
            if (intent.hasExtra("receiverSocketId")) {
                receiverSocketId = intent.getStringExtra("receiverSocketId").toString()
            }
            if (intent.hasExtra("userCallingName")) {
                userCallingName = intent.getStringExtra("userCallingName").toString()
            }



            if (intent.hasExtra("callerUserId")) {
                callerUserId = intent.getStringExtra("callerUserId").toString()
            }

            if (intent.hasExtra("receiverUserId")) {
                receiverUserId = intent.getStringExtra("receiverUserId").toString()
            }


            val peerConnectionUsers = PeerConnectionUsers(
                userID = session.userLoggedInID,
                available = true,
                connected = false,
                callerSocketId = callerSocketId,
                receiverSocketId = receiverSocketId,
                peerConnected = 0,
                userCallingName = userCallingName
            )

            val peerJson = Gson().toJson(peerConnectionUsers)
            val userCallObject = JSONObject()
            try {
                if(isCall =="call"){
                    userCallObject.put("userId", callerUserId)
                    userCallObject.put("receiverId", receiverUserId)
                }else{
                    userCallObject.put("userId", receiverUserId)
                    userCallObject.put("receiverId", callerUserId)
                }

                userCallObject.put("userToCall", receiverSocketId)
                userCallObject.put("signalData", peerJson)
                userCallObject.put("from", callerSocketId)
                userCallObject.put("name", "Android")
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            socket?.emit("callUser", userCallObject)
            println("USerSocketData $userCallObject")
            toast("Socket Emitted for Call")
        } else {
            toast("Socket is not Connected")
        }
/*
        if(callConnectedBy==1){
            userCallObject.put("userToCall", callerSocketId)
            userCallObject.put("from", receiverSocketId)

            userCallObject.put("receiverId", callerUserId)
            userCallObject.put("userId", receiverUserId)
        }else{
            userCallObject.put("userToCall", receiverSocketId)
            userCallObject.put("from", callerSocketId)

            userCallObject.put("receiverId", receiverUserId)
            userCallObject.put("userId", callerUserId)
        }


        userCallObject.put("signalData", peerJson)
        userCallObject.put("name", "Android")*/
//        socket!!.emit("message", message)
    }

    private fun createPeerConnection(factory: PeerConnectionFactory?): PeerConnection {
        val iceServers = ArrayList<IceServer>()
        val URL = "stun:stun.l.google.com:19302"
        iceServers.add(IceServer(URL))
        val rtcConfig = RTCConfiguration(iceServers)
        val pcConstraints = MediaConstraints()
        val pcObserver: PeerConnection.Observer = object : PeerConnection.Observer {
            override fun onSignalingChange(signalingState: SignalingState) {
                Log.e(TAG, "onSignalingChange: ")
            }

            override fun onIceConnectionChange(iceConnectionState: IceConnectionState) {
                Log.e(TAG, "onIceConnectionChange: ")
            }

            override fun onIceConnectionReceivingChange(b: Boolean) {
                Log.e(TAG, "onIceConnectionReceivingChange: ")
            }

            override fun onIceGatheringChange(iceGatheringState: IceGatheringState) {
                Log.e(TAG, "onIceGatheringChange: ")
            }

            override fun onIceCandidate(iceCandidate: IceCandidate) {
                Log.e(TAG, "onIceCandidate: ")
                val message = JSONObject()
                try {
                    message.put("type", "candidate")
                    message.put("label", iceCandidate.sdpMLineIndex)
                    message.put("id", iceCandidate.sdpMid)
                    message.put("candidate", iceCandidate.sdp)
                    Log.e(TAG, "onIceCandidate: sending candidate $message")
                    sendMessage(message, "call")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {
                Log.e(TAG, "onIceCandidatesRemoved: ")
            }

            override fun onAddStream(mediaStream: MediaStream) {
                Log.e(TAG, "onAddStream: " + mediaStream.videoTracks.size)
                val remoteVideoTrack = mediaStream.videoTracks[0]
                val remoteAudioTrack = mediaStream.audioTracks[0]
                remoteAudioTrack.setEnabled(true)
                remoteVideoTrack.setEnabled(true)
                remoteVideoTrack.addRenderer(VideoRenderer(binding!!.surfaceView2))
            }

            override fun onRemoveStream(mediaStream: MediaStream) {
                Log.e(TAG, "onRemoveStream: ")
            }

            override fun onDataChannel(dataChannel: DataChannel) {
                Log.e(TAG, "onDataChannel: ")
            }

            override fun onRenegotiationNeeded() {
                Log.e(TAG, "onRenegotiationNeeded: ")
            }
        }
        return factory!!.createPeerConnection(rtcConfig, pcConstraints, pcObserver)
    }

    private fun createVideoCapturer(): VideoCapturer? {
        val videoCapturer: VideoCapturer?
        videoCapturer = if (useCamera2()) {
            createCameraCapturer(Camera2Enumerator(this))
        } else {
            createCameraCapturer(Camera1Enumerator(true))
        }
        return videoCapturer
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }

    private fun useCamera2(): Boolean {
        return Camera2Enumerator.isSupported(this)
    }

    companion object {
        private const val TAG = "ChatUserCallActivity"
        private const val RC_CALL = 111
        const val VIDEO_TRACK_ID = "ARDAMSv0"
        const val VIDEO_RESOLUTION_WIDTH = 1280
        const val VIDEO_RESOLUTION_HEIGHT = 720
        const val FPS = 30
    }
}