package com.siltech.cryptochat.services

import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Environment
import android.os.IBinder
import com.siltech.cryptochat.chat.ChatViewModel
import java.io.File
import java.lang.Exception

class RecordingService : Service() {

    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var file: File
    private lateinit var fileName: String
    private lateinit var vm: ChatViewModel

    private var startMillis: Long? = null
    private var endMillis: Long? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startRecording()
        return START_STICKY
    }

    private fun startRecording() {
        fileName = "voice" + System.currentTimeMillis() / 1000
        file = File(Environment.getExternalStorageDirectory(), "/smsVoices/" + fileName + "mp3")

        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setOutputFile(file.absolutePath)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder.setAudioChannels(1)

        try {
            mediaRecorder.prepare()
            mediaRecorder.start()
            startMillis = System.currentTimeMillis()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        mediaRecorder.stop()
        endMillis = (System.currentTimeMillis() - startMillis!!)

        mediaRecorder.release()
    }

    override fun onDestroy() {
        if (mediaRecorder != null) {
            stopRecording()
        }
        super.onDestroy()
    }
}
