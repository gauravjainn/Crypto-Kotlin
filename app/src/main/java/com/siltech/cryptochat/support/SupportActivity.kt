package com.siltech.cryptochat.support

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.siltech.cryptochat.R
import com.siltech.cryptochat.chat.ChatAdapter
import com.siltech.cryptochat.chat.ChatViewModel
import com.siltech.cryptochat.databinding.ActivityChatBinding
import java.io.File

class SupportActivity : AppCompatActivity() {


    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var file: File
    private lateinit var fileName: String
    private lateinit var vm: ChatViewModel
    private lateinit var adapter: ChatAdapter

    private var startMillis: Long? = null
    private var endMillis: Long? = null

    private var startRecord = true
    private var isPlaying = false
    private val LOG_TAG = "AudioRecordTest"
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200

    private var mediaPlayer: MediaPlayer? = null
    private val minutes = 0
    private val seconds = 0

    private var selectedDoc: Uri? = null

    companion object {
        val REQUEST_CODE_PICKER = 1000
    }

    fun hasConnection(context: Context): Boolean {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNW = cm.activeNetworkInfo
        return if (activeNW != null && activeNW.isConnected) {
            true
        } else false
    }


    override fun onStart() {
        if (hasConnection(this)) {
            Toast.makeText(this, "Active networks OK ", Toast.LENGTH_LONG).show()
        } else Toast.makeText(this, "No active networks... ", Toast.LENGTH_LONG).show()
        super.onStart()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm = defaultViewModelProviderFactory.create(ChatViewModel::class.java)

        mediaRecorder = MediaRecorder()

//        adapter = ChatAdapter({
//            Toast.makeText(this, "clickd", Toast.LENGTH_SHORT).show()
////            onPlay(isPlaying, it.path)
////            isPlaying = !isPlaying
//        }, {
//            val intent = Intent(Intent.ACTION_VIEW, selectedDoc)
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//
//            val chooser = Intent.createChooser(intent, "Open with")
//            intent.setDataAndType(selectedDoc, ".pdf")
//
//            if (intent.resolveActivity(packageManager) != null)
//                startActivity(chooser)
//            else
//                Toast.makeText(applicationContext, "No suitable application to open file", Toast.LENGTH_LONG).show()
//
//            //            Log.d("tag", "${it.path}")
////            openFile(it.path)
//        }}

        binding.recyclerView2.adapter = adapter
//        vm.getAllSms.observe(this) {
//            adapter.setListSMS(it)
//        }

        binding.btnAttach.setOnClickListener {
//            val intent: Intent
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//                intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//            } else {
//                intent = Intent(Intent.ACTION_PICK)
//            }
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            intent.type = "*/*"
//            if (intent.resolveActivity(packageManager) != null)
//                startActivityForResult(intent, REQUEST_CODE_PICKER)
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent,  1)
        }

        binding.btnSend.setOnClickListener {

            val textFromField = binding.editTextTextPersonName.text

//            vm.addSms(textSMS)
            binding.editTextTextPersonName.text = null
        }

        binding.btnVoiceMessage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                ActivityCompat.requestPermissions(this, permissions, 0)
            } else {
                onStartRecord(startRecord)
                startRecord = !startRecord
                if (!startRecord) {
                    it.setBackgroundResource(R.drawable.round_button_red)
                } else {
                    it.setBackgroundResource(R.drawable.round_button)
                }

            }
        }
    }

    private fun openFile(path: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        val file = File(path)
        intent.setDataAndType(
            FileProvider.getUriForFile(
                this,
                this.getApplicationContext().getPackageName() + ".provider",
                file
            ), "application/pdf"
        )
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)

//
//        val myFile = File(path)
//        FileOpen.openFile(this, myFile)
    }

    object FileOpen {
        fun openFile(context: Context, url: File) {
            // Create URI
            val uri = Uri.fromFile(url)
            val intent = Intent(Intent.ACTION_VIEW)
            // Check what kind of file you are trying to open, by comparing the url with extensions.
            // When the if condition is matched, plugin sets the correct intent (mime) type,
            // so Android knew what application to use to open the file
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword")
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf")
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel")
            } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav")
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf")
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav")
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif")
            } else if (url.toString().contains(".jpg") || url.toString()
                    .contains(".jpeg") || url.toString().contains(".png")
            ) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg")
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain")
            } else if (url.toString().contains(".3gp") || url.toString()
                    .contains(".mpg") || url.toString().contains(".mpeg") || url.toString()
                    .contains(".mpe") || url.toString().contains(".mp4") || url.toString()
                    .contains(".avi")
            ) {
                // Video files
                intent.setDataAndType(uri, "video/*")
            } else {
                //if you want you can also define the intent type for any other file

                //additionally use else clause below, to manage other unknown extensions
                //in this case, Android will show all applications installed on the device
                //so you can choose which application to use
                intent.setDataAndType(uri, "*/*")
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            selectedDoc = data?.data


            if (selectedDoc != null) {
//                vm.addSms(sms)
            }
        }
    }

    private fun onPlay(isPlayong: Boolean, path: String) {
        if (!isPlayong) {
            startPlaying(path)
        } else {
            pausePlaying()
        }
    }

    private fun startPlaying(path: String) {
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setDataSource(path)
        mediaPlayer!!.prepare()
        mediaPlayer!!.setOnPreparedListener {
            mediaPlayer!!.start()
        }
        mediaPlayer!!.setOnCompletionListener {
            stopPlay()
        }
    }

    private fun pausePlaying() {
        mediaPlayer?.pause()
    }

    private fun stopPlay() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        isPlaying = !isPlaying
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun onStartRecord(record: Boolean) {

        if (record) {
            val file: File = File(getExternalFilesDir(null), "/Mysounds")

            if (!file.exists()) {
                file.mkdir()
            }

            startRecording()

            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        } else {
            stopRecording()
        }

    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecording() {


        fileName = "voice" + System.currentTimeMillis() / 1000
        file = File(getExternalFilesDir(null)?.absolutePath + "/Mysounds/", "$fileName.mp3")


        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setOutputFile(file)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder.setAudioChannels(1)

        try {
            mediaRecorder.prepare()
            mediaRecorder.start()
            Toast.makeText(this, "RecordingStart", Toast.LENGTH_SHORT).show()
            startMillis = System.currentTimeMillis()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        mediaRecorder.stop()
        endMillis = (System.currentTimeMillis() - startMillis!!)

        mediaRecorder.release()

        Toast.makeText(this, "RecordingStop", Toast.LENGTH_SHORT).show()
//        vm.addSms(sms)
//        vm.getAllSms.observe(this) {
//            adapter.setListSMS(it)
//        }
        adapter.notifyDataSetChanged()

    }


    override fun onDestroy() {
        finish()
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}