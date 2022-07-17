package com.siltech.cryptochat.chat;

import ChatDBViewModel
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.AbsListView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar.LayoutParams
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.firebasenotifications.RetrofitInstance
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import com.siltech.cryptochat.*
import com.siltech.cryptochat.app.AppModule.Companion.context
import com.siltech.cryptochat.call.util.toast
import com.siltech.cryptochat.chat.chat_users.ChatUsersActivity
import com.siltech.cryptochat.chat.sheet.BottomFragment
import com.siltech.cryptochat.contacts.HomeActivityKotlin
import com.siltech.cryptochat.data.State
import com.siltech.cryptochat.data.db.messages.MessagesDBAdapter
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.ChatDatas
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.SmsDatas
import com.siltech.cryptochat.databinding.ActivityChatBinding
import com.siltech.cryptochat.model.*
import com.siltech.cryptochat.notificationCall.PushNotification
import com.siltech.cryptochat.utils.Resources
import com.siltech.cryptochat.utils.SessionManager
import com.siltech.cryptochat.utils.SocketManager
import com.siltech.cryptochat.utils.checkCallRequiredPermissions
import com.siltech.cryptochat.webRtcNative.WebRtcCallActivity

import id.zelory.compressor.Compressor
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.*
import java.io.File
import java.util.*
import java.util.zip.DataFormatException
import java.util.zip.Deflater
import java.util.zip.Inflater
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList


class Chat : AppCompatActivity() {
    private var userChatBlocked: Boolean = false
    lateinit var session: SessionManager

    //    private var socket: Socket? = null
    private var mSocket: Socket? = null
    var fireBaseDatabase: FirebaseDatabase? = null

    private var userLoginSocketId: String? = ""
    private var userChatSocketId: String? = ""

    private var userLoginRole: String? = ""
    private var userChatRole: String? = ""
    private var userChatId: Int? = 0
    private var userLoginName: String? = ""
    private var userChatLoginName: String? = ""
    private var callHitted = false


    private fun hasConnection(context: Context): Boolean {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNW = cm.activeNetworkInfo
        return activeNW != null && activeNW.isConnected
    }

    val secret =
        "ocIbuNzN1IQ1EvFsAP3JfBhcbkwZEmA2XMNzUBL0FMeb7hXRudfKR49m4O0ezkhYbwOXOMeyBxhz2LI9YyGx5AixSzVhaEOGAajmGuEgfptkv4gSCREuKSR9vn0CCfmJCWzFeyu5gtAn7gC5MGGMJ1mlZmiWXO6jLItgvOUzAziO1DmFBS4BZGfUWyZ2kqc6LWYh6opAyQ5szb2aSmAF2XU7F4wRBzfdjjk8kGdJLSpqZiyoCZivSHMmNyIaxvzb"

    override fun onStart() {
        if (hasConnection(this)) {
            // Toast.makeText(this, "Active networks OK ", Toast.LENGTH_LONG).show()
        } else Toast.makeText(this, "No active networks... ", Toast.LENGTH_LONG).show()
        super.onStart()
    }

//    private var loadingDialog: AlertDialog? = null

    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var fileAudio: File
    private lateinit var fileName: String
    private val vm: ChatViewModel by viewModel()
    private val vmDB: ChatDBViewModel by viewModel()
    private lateinit var adapter: ChatAdapter
    private lateinit var adapter2: ChatMainAdapter
    private lateinit var adapterDB: MessagesDBAdapter
    private lateinit var recyclerView: RecyclerView
    private var secreto: SecretKeySpec? = null
    lateinit var binding: ActivityChatBinding

    val timerTask: Timer = Timer()


    private var startMillis: Long? = null
    private var endMillis: Long? = null
    lateinit var cipher: Cipher

    private var startRecord = true
    private var isPlaying = false
    private val LOG_TAG = "AudioRecordTest"
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200

    private var mediaPlayer: MediaPlayer? = null
    private val minutes = 0
    private val seconds = 0

    private var countMessages: File? = null

    private var chatId: Int? = null

    private var selectedDoc: Uri? = null

    private var userIdInChat: Int? = null
    private var currentUserIdInChat: Int? = null
    private var userIntoChat: Any? = null
    private var userIntoChatID: Int? = null

    private var smsID: Int? = null

    private var listUsersSize: Int? = null

    private var chatNameForResult: String? = null

    private var publicKey: String? = null
    private var userChatLogin = ""
    private var userChatID: Int? = null
    private var isCheckedSize = false

    var keySS: String? = null
//    private val chatId: Int
//        get() = intent.getIntExtra("chat_id", 0)

    var a = false
    private val creatorId: Int
        get() = intent.getIntExtra("user_id", 0)

    private val chatID: Int
        get() = intent.getIntExtra("chatId", 0)

    private val chatDBName: String?
        get() = intent.getStringExtra("chatDBname")

    private val chatOD: Int
        get() = intent.getIntExtra("chatOD", 0)

    private val chat_name: String?
        get() = intent.getStringExtra("chat_name")

    private val userLogin: String?
        get() = intent.getStringExtra("userLogin")

    private val userLoginSecond: String?
        get() = intent.getStringExtra("secondUserLogin")

    private val userLoginId: Int?
        get() = intent.getIntExtra("secondUserId", 0)

    private val OPERATION_CHOOSE_PHOTO = 2

    private val REQUEST_CODE_PICKER = 1000
    private var isScrolling = true
    private var isSmoothScrolling = true
    private var chatHasThisUser = false


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.navigationBarColor = ContextCompat.getColor(this@Chat, R.color.light_blue)
        // supportActionBar?.hide()
        val actionbar = supportActionBar
        //set actionbar title
        // actionbar!!.title = chat_name
        //set back button


        //actionbar?.setDisplayHomeAsUpEnabled(true)

        //actionbar?.setDisplayHomeAsUpEnabled(true)
        val context = this

        supportActionBar?.apply {
            // show custom title in action bar
            customView = actionBarCustomTitle()

            // action bar title click listener
            customView.setOnClickListener {
                val intent = Intent(applicationContext, ChatUsersActivity::class.java)
                intent.putExtra("chat_name", chat_name)
                intent.putExtra("userChatID", userIdInChat)
                intent.putExtra("chatID", chatId)
                startActivity(intent)


            }

            displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM

            setDisplayHomeAsUpEnabled(true)
            //setDisplayShowHomeEnabled(true)
            //setDisplayUseLogoEnabled(true)

        }

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        session = SessionManager(this)


        /**Connect to Socket
         *
         */

        vm.getChatForResult("eq." + chat_name.toString())

        Log.e("TAG", "eq." + chat_name.toString());
        vm.getUsersChat("eq." + chat_name.toString())

        val secret =
            "ocIbuNzN1IQ1EvFsAP3JfBhcbkwZEmA2XMNzUBL0FMeb7hXRudfKR49m4O0ezkhYbwOXOMeyBxhz2LI9YyGx5AixSzVhaEOGAajmGuEgfptkv4gSCREuKSR9vn0CCfmJCWzFeyu5gtAn7gC5MGGMJ1mlZmiWXO6jLItgvOUzAziO1DmFBS4BZGfUWyZ2kqc6LWYh6opAyQ5szb2aSmAF2XU7F4wRBzfdjjk8kGdJLSpqZiyoCZivSHMmNyIaxvzb"

        keySS = SecretKeySpec(secret.toByteArray(), "AES").toString()
        cipher =
            Cipher.getInstance(
                "AES",
                "BC"
            )//SunJCE provider AES algorithm, mode(optional) and padding schema(optional)

        publicKey = getSecretKey(this)
        val pswdIterations = 65536
        val keySize = 128

        val saltBytes = byteArrayOf(0, 1, 2, 3, 4, 5, 6)

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec = PBEKeySpec(secret?.toCharArray(), saltBytes, pswdIterations, keySize)
        val secretKey = factory.generateSecret(spec)
        secreto = SecretKeySpec(secretKey.encoded, "AES")

        // val decodedKeys =
        //     android.util.Base64.decode(secreto.toString(), android.util.Base64.NO_WRAP)
        // val originalKeys = SecretKeySpec(decodedKeys, 0, decodedKeys.size, "AES")
        // print(secreto)

        initRequests()


        mediaRecorder = MediaRecorder()

        adapter2 = ChatMainAdapter({

            CoroutineScope(Dispatchers.IO).launch() {
                val decoder = Base64.getDecoder()
                val encryptedTextByte = decoder.decode(it)
                cipher.init(
                    Cipher.DECRYPT_MODE, secreto
                )

                val decryptedByte = cipher.doFinal(encryptedTextByte)


                val mDe = String(decryptedByte)

                adapter2.setdecr(mDe)
                Log.d("aoalo", mDe.toString())
            }
        }, {
            onPlay(isPlaying, it.toString())
            Log.d("STRINFFROMSERVER", it)
            isPlaying = !isPlaying
        },
            {
                val dialogFragment = ShowImageFragment.newInstance(it)
                dialogFragment.show(supportFragmentManager, "showImage")

            }, { s: String, s2: String ->

                var indexOfPoint = s2.indexOf('.')
                var fileExtension = s2.substring(indexOfPoint, s2.length)
                println(fileExtension)

                var fileNameFromServer = s2.substring(0, indexOfPoint - 1)
                adapter2.setFileNameFromServer(fileNameFromServer)

                val fileso = saveFiles(this, s, fileExtension)
                if (fileso != null) {
                    openFiles(this, fileso.absoluteFile)
                }

            })

        adapterDB = MessagesDBAdapter({
            lifecycleScope.launch() {
                val decoder = Base64.getDecoder()
                val encryptedTextByte = decoder.decode(it)
                cipher.init(
                    Cipher.DECRYPT_MODE, secreto
                )
                val decryptedByte = cipher.doFinal(encryptedTextByte)
                val mDe = String(decryptedByte)
                adapterDB.setdecr(mDe)
                Log.d("aoalo", mDe.toString())
            }
        }, {
            onPlay(isPlaying, it.toString())
            Log.d("STRINFFROMSERVER", it)
            isPlaying = !isPlaying
        },
            {
                val dialogFragment = ShowImageFragment.newInstance(it)
                dialogFragment.show(supportFragmentManager, "showImage")

            }, { s: String, s2: String ->

//
                val bytes: ByteArray = Base64.getDecoder().decode(s)
                // Decompress the bytes
                // Decompress the bytes
                val decompresser = Inflater()
                decompresser.setInput(bytes)
                val result: ByteArray = s.toByteArray()
                val resultLength = decompresser.inflate(result)
                decompresser.end()

                // Decode the bytes into a String
                val outputString = String(result, 0, resultLength, charset("UTF-8"))
                println("Deflated String:$outputString")

                Log.d("chaaaat", "${s.toString()}")
                var indexOfPoint = s2.indexOf('.')
                var fileExtension = s2.substring(indexOfPoint, s2.length)
                var fileNameFromServer = s2.substring(0, indexOfPoint - 1)
                adapterDB.setFileNameFromServer(fileNameFromServer)

                val fileso = saveFiles(
                    this, decompress(s), fileExtension
                )
                if (fileso != null) {
                    openFiles(this, fileso.absoluteFile)
                }

            })

        vm.smsList.observe(this) {
            adapter.setListSMS(it)
        }

        adapter = ChatAdapter({
            vm.getSpecifciSMS(
                "return=representation",
                "eq." + it.message,
                "eq." + userIdInChat.toString()
            )
            showActionDialog(it.message)
        },
            {

                isPlaying = !isPlaying
            }
        ) {
            lifecycleScope.launch() {
                val decoder = Base64.getDecoder()
                val encryptedTextByte = decoder.decode(it)
                cipher.init(
                    Cipher.DECRYPT_MODE, secreto
                )

                val decryptedByte = cipher.doFinal(encryptedTextByte)
                val mDe = String(decryptedByte)

                adapter.setdecr(mDe)
                Log.d("aoalo", mDe.toString())

//                    if(it.isNotEmpty()){
//                        vmDB.addSMS(
//                            sdata(
//                                chatID, mDe,
//                                chatId?.toLong()!!
//                            )
//                        )
//                    }

            }
        }

//        CoroutineScope(Dispatchers.IO).launch {
//            Timer().scheduleAtFixedRate(object : TimerTask() {
//                override fun run() {
//                    vm.getSMSS("return=representation", "eq." + chat_name.toString())
//                }
//            }, 100, 2600)
//        }

        binding.recyclerView2.adapter = adapterDB
        binding.recyclerView2.smoothScrollToPosition(adapterDB.itemCount)

        binding.recyclerView2.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isScrolling) {
                    updateData()
                }

            }

            private fun updateData() {
                isScrolling = false
            }
        })

//        vm.getAllSms.observe(this) {
//            adapter.setListSMS(it)
//        }

        vm.getSMSS("return=representation", "eq." + chat_name.toString())
        vm.getChatForResult("eq." + chat_name.toString())

        binding.btnAttach.setOnClickListener {

            if (isOnline(this)) {
                var bottomFragment = BottomFragment()
                bottomFragment.show(supportFragmentManager, "TAG")
            }
        }

        binding.btnSend.setOnClickListener {
            if (isOnline(this)) {

                val seed: String = ""
                var message = ""
                val pswdIterations = 65536
                val keySize = 128

                val saltBytes = byteArrayOf(0, 1, 2, 3, 4, 5, 6)

                val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                val spec = PBEKeySpec(secret?.toCharArray(), saltBytes, pswdIterations, keySize)
                val secretKey = factory.generateSecret(spec)
                secreto = SecretKeySpec(secretKey.encoded, "AES")

                CoroutineScope(Dispatchers.IO).launch {

                    if (binding.editTextTextPersonName.text.toString().isNotEmpty()) {
                        val text = binding.editTextTextPersonName.text.toString()
                        val plainTextByte: ByteArray =
                            text.toByteArray((Charsets.UTF_8))
                        cipher!!.init(Cipher.ENCRYPT_MODE, secreto)
                        val encryptedByte: ByteArray = cipher!!.doFinal(plainTextByte)
                        val encoder = Base64.getEncoder()
                        message = encoder.encodeToString(encryptedByte)
                        Log.d("encrtext", message)


                        Log.e("SIGNIN", "message : " + text)
                        Log.e("SIGNIN", "encrypted message : " + message)
                        Log.e("SIGNIN", "currentUserIdInChat : " + currentUserIdInChat)
                        Log.e("SIGNIN", "getUserChatId(this@Chat) : " + getUserChatId(this@Chat))


                        vm.createMessage(
                            "return=representation",
                            CreateMessageRequest(
                                currentUserIdInChat!!,
                                message,
                                "text",
                                "",
                                arrayListOf(getUserChatId(this@Chat)!!)
                            )
                        )
                    }
                }
            }
            binding.editTextTextPersonName.text = null
        }
        binding.btnVoiceMessage.setOnClickListener {
            if (isOnline(this)) {

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

        vm.state.observe(this) {
            when (it) {
                is State.LoadingState -> {
                    if (it.isLoading) {
                    } else {
                    }
                }
                is State.ErrorState -> {
                    ""
                }

                is State.SuccessListState<*> -> {
                    when (if (it.data.isEmpty())
                        lifecycleScope.launch {
                            val a = vmDB.getAlls(chatOD)
                            adapterDB.submitList(a)
                        }
                    else it.data[0]) {
                        is GetMessagesResponseItem -> {
                            if (it.data.isNotEmpty()) {

//                                adapter2.submitList(it.data as ArrayList<GetMessagesResponseItem>)

                                CoroutineScope(Dispatchers.IO).launch {
//                                    vmDB.deleteAlls()
                                    for (i in it.data as ArrayList<GetMessagesResponseItem>) {
                                        val sms = SmsDatas(
                                            i.messageId,
                                            i.message,
                                            i.chatId.toLong(),
                                            i.userLogin,
                                            i.type,
                                            i.filetype,
                                            i.date
                                        )
                                        vmDB.addSMS(sms)
                                    }
                                }

                                lifecycleScope.launch {
                                    val a = vmDB.getAlls(chatOD)
                                    Log.d("ADDEDMESSAGES", a.toString())
                                    adapterDB.submitList(a)
                                }
                                for (sms in it.data as ArrayList<GetMessagesResponseItem>) {
                                    Log.d("IDDD", "${sms.messageId}")

                                    if (listUsersSize != sms.viwedUsers.size && !sms.viwedUsers.contains(
                                            getUserChatId(this)!!
                                        )
                                    ) {

                                        var listID = arrayListOf<Int>()
                                        for (id in sms.viwedUsers) {
                                            listID.add(id)
                                        }
                                        listID.add(getUserChatId(this))
                                        Log.d("LISTOFID", "${listID.toString()}")

                                        vm.changeSMSViewedUsers(
                                            "return=representation",
                                            "eq." + sms.messageId.toString(),
                                            changeListOfUsersItem(
                                                sms.chatUsersId,
                                                sms.date,
                                                sms.filetype,
                                                sms.messageId,
                                                sms.message,
                                                sms.type,
                                                listID
                                            )
                                        )
                                    }

                                    if (listUsersSize == sms.viwedUsers.size) {
                                        vm.deleteSMS(
                                            "return=representation",
                                            "eq." + sms.messageId.toString()
                                        )
                                        Log.d("deleteSMS", "DELETEDSMS")
                                    }

                                }

//                                vm.deleteAllSMSS()


                            } else {
                                val list = arrayListOf<GetMessagesResponseItem>()
                                list.add(
                                    GetMessagesResponseItem(
                                        " ",
                                        " ",
                                        " ",
                                        " ",
                                        " ",
                                        " ",
                                        0,
                                        0, 0, arrayListOf(0)
                                    )
                                )
                                adapter2.submitList(emptyList())
                            }
                        }
                        is GetChatUsersResponseItemX -> {
                            listUsersSize = it.data.size
                            if (it.data.size < 2) {
                                Log.d("size", "${it.data.size}")
                                println("${it.data.size}")
                                if (!isCheckedSize) {
                                    addNecessaryNewUserIntoChat()
                                } else {

                                }
                            }
                            val addNewUsersRequest = AddNewUsersRequest()
                            val listUsersss = it.data as ArrayList<GetChatUsersResponseItemX>
                            Log.d("listUsers", listUsersss.toString())
                            if (userIntoChatID != null) {
                                val user = GetChatUsersResponseItemX(
                                    chat_name.toString(), false,
                                    userChatLogin.toString(), userIntoChatID.toString()
                                )

                                if (listUsersss.contains(user)) {
                                    Toast.makeText(
                                        this,
                                        "Такой пользователь уже в чате",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    val mainUser = AddNewUsersRequestItem(
                                        userIntoChatID as Int,
                                        chatId!!,
                                        false
                                    )

                                    addNewUsersRequest.add(mainUser)
                                    vm.addUsersIntoChat("return=representation", addNewUsersRequest)
                                    userIntoChatID = null
                                    vm.getUsersChat("eq." + chat_name.toString())
                                    Toast.makeText(
                                        this,
                                        "Добавлен: ${userChatLogin}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            Log.d("size", "${it.data.size}")
                        }
                    }
                }

                is State.SuccessObjectState<*> -> {
                    when (it.data) {
                        is CreateUserResponse -> {
                            if (it.data.isNotEmpty()) {
                                userIntoChatID = it.data[0].id
                                vm.getUsersChat("eq." + chat_name.toString())
                            } else {
                                Toast.makeText(
                                    this,
                                    "Такого пользователя не существует!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is CheckCallUserResponse -> {

                            Log.e("TAG", "CheckCallUserResponse called....");

                            if (it.data.isNotEmpty()) {
                                Log.e(
                                    "TAG",
                                    "CheckCallUserResponse it.data.size : " + it.data.size
                                );
                                var isBlockedUser = false
                                for (item in it.data) {
                                    if (item.isBlocked) {
                                        isBlockedUser = true
                                        return@observe
                                    }
                                }
                                Log.e("TAG", "isBlockedUser : " + isBlockedUser);
                                if (isBlockedUser == false) {
                                    /*val intent = Intent(applicationContext, ChatUserCallActivity::class.java)
                                    intent.putExtra("chat_name", chat_name)
                                    intent.putExtra("userChatID", userIdInChat)
                                    intent.putExtra("chatID", chatId)
                                    intent.putExtra("currentUserIdInChat", currentUserIdInChat)
                                    intent.putExtra("getUserChatId", getUserChatId(this@Chat))
                                    startActivity(intent)*/
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Call not allowed to block user.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Такого пользователя не существует!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is AddUsersResponse -> {
                            userIdInChat = it.data[0].id
                        }
                        is ChatsListResponse -> {
                            if (it.data.isNotEmpty()) {
                                saveChatId(this, it.data[0].id)
                                chatId = it.data[0].id
                                vm.getUserIdInChat(
                                    "return=representation",
                                    "eq." + creatorId,
                                    "eq." + it.data[0].id
                                )
                            } else {
                                chatDBName?.let { it1 -> deleteChatDialogIfNotExist(chatOD, it1) }
                            }

                        }
                        is GetUserIdInChatResponse -> {
                            currentUserIdInChat = it.data[0].id
                            saveUserIsAdmin(this, it.data[0].isAdministrator)
                        }
                        is CreateMessageResponse -> {
                            vm.getSMSS("return=representation", "eq." + chat_name.toString())
                            a = true
                        }
                        is DeletedMessageResponse -> {
                            vm.getSMSS("return=representation", "eq." + chat_name.toString())
                        }
                        is GetSpecificSMSResponse -> {
                            smsID = it.data[0].id
                        }
                        is ChangedSMSResponse -> {
                            vm.getSMSS("return=representation", "eq." + chat_name.toString())
                        }
                        is GetChatUsersResponseX -> {
                            vm.getSpecificUserByLogin(
                                "return=representation",
                                "eq." + chatNameForResult
                            )
                        }
                    }
                }
            }
        }

        connectToSocket()
    }

    private fun getUserChatData() {
        val chatUsers = chat_name!!.replace("-", ",")
        vm.getUsersChatData("($chatUsers)")
        vm.chatUsersData.observe(this) { response ->
            when (response) {
                is Resources.Success -> {
                    if (response.data != null) {
                        response.data.forEach {
                            if (it.id != creatorId) {
                                getPeersData(it.id)
                            }
                        }
                    }
                }
                is Resources.Loading -> {
                }
                is Resources.Error -> {
                    toast("Something went wrong please try again after some time")
                }
            }
        }
    }

    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        hitCallUserSocketEvent()

    }

    private fun connectToSocket() {
        mSocket = SocketManager.instance!!.getSocket()
    }


    private fun hitCallUserSocketEvent() {

        if (checkCallRequiredPermissions(this).isEmpty()) {
            if (callHitted) {
                getUserChatData()
            }
        } else {
            permissionsResultCallback.launch(checkCallRequiredPermissions(this).toTypedArray())
        }

    }

    private fun getPeersData(chatUserId: Int) {
        val id = "in.($creatorId,$chatUserId)"
        println("id $id")
        session.callUserIds = id
        vm.getPeersDetails("Bearer $publicKey", id)
        vm.peersData.observe(this) {
            when (it) {
                is Resources.Loading -> {
                }
                is Resources.Error -> {
                    toast("Something went wrong please try again after some time")
                }
                is Resources.Success -> {

                    it.data!!.forEach { peerData ->
                        if (peerData.id == creatorId) {
                            userLoginSocketId = peerData.socket_id
                            userLoginRole = peerData.role
                            userLoginName = peerData.login
                        } else if (peerData.id == chatUserId) {
                            userChatBlocked = peerData.is_blocked
                            userChatSocketId = peerData.socket_id
                            userChatRole = peerData.role
                            userChatId = peerData.id
                            userChatLoginName = peerData.login
                        }
                    }
                    if (userChatBlocked) {
                        Toast.makeText(this, "Call not allowed to block user.", Toast.LENGTH_SHORT).show()
                    } else {
                        startActivity(
                            Intent(this, WebRtcCallActivity::class.java).apply {
                                putExtra("uid", creatorId)
                                putExtra("userCallingName", userLoginName)
                                putExtra("otherUserName", userChatLoginName)
                                putExtra("callerUserId", session.userLoggedInID)
                                putExtra("receiverUserId", userChatId.toString())
                                putExtra("callerSocketId", userLoginSocketId)
                                putExtra("receiverSocketId", userChatSocketId)
                            })
                    }
                }
            }
        }

    }

    private suspend fun sendCallNotification(notification: PushNotification) {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful) {
                Log.e("TAG", response.body().toString())

            } else {
                Log.e("TAG", response.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.e("TAG", e.toString())
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_add_user -> {
                vm.getChatForResult("eq." + chat_name.toString())
                addNewUserIntoChat()
            }

            R.id.calls -> {
                try {
                    callHitted = true
                    Log.e("calls", "chat_name : $chat_name");
                    Log.e("calls", "userChatID : $userChatID");
                    Log.e("calls", "chatID : $chatID");
                    Log.e("calls", "currentUserIdInChat : $currentUserIdInChat")
                    Log.e("calls", "getUserChatId(this@Chat) : " + getUserChatId(this@Chat))


                    /*val strs = chat_name!!.split("-").toTypedArray()
                    Log.e("calls", "checkCallUser : " + "(" + strs[0] + "," + strs[1] + ")");
                    vm.checkCallUser("(" + strs[0] + "," + strs[1] + ")")*/
                    hitCallUserSocketEvent()
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showImage(it: String) {

        /** replace with your own uri */

//
//        val a = saveImage2(this, it)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val chooser = Intent.createChooser(intent, "Open with")
//        intent.setDataAndType(Uri.fromFile(a), "*/*")


        if (intent.resolveActivity(packageManager) != null)
            startActivity(chooser)
        else
            Toast.makeText(
                applicationContext,
                "No suitable application to open file",
                Toast.LENGTH_LONG
            ).show()
    }

    private fun showActionDialog(str: String) {
        val builder = AlertDialog.Builder(this)

        builder
            .setPositiveButton("Изменить") { dialog, id1 ->
                showChangeNameOfSMS(str)
                dialog.cancel()
            }
            .setNegativeButton("Удалить") { dialog, id2 ->
                deleteSMS(str)
                dialog.cancel()
            }
        builder.setTitle("Выберите, что хотите сделать?")
        builder.setCancelable(true)
        builder.show()
    }

    private fun deleteSMS(string: String) {

        val builder = AlertDialog.Builder(this)

        builder
            .setPositiveButton("Да") { dialog, id1 ->
                vm.deleteSMS("return=representation", "eq." + smsID)

            }
            .setNegativeButton("Нет") { dialog, id2 ->
                dialog.dismiss()
            }
        builder.setTitle("Вы уверены, что хотите удалить сообщение?")
        builder.setCancelable(false)
        builder.show()
    }

    private fun showChangeNameOfSMS(string: String) {

        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater

        val dialogView = inflater.inflate(R.layout.add_new_chat_name, null)
        var name = dialogView.findViewById<EditText>(R.id.et_name_of_chat)

        builder.setView(dialogView)
            .setPositiveButton("Add") { dialog, id1 ->
                val sms = name.text.toString()
                if (sms.isNotEmpty()) {
                    vm.changeSMS("return=representation", "eq." + smsID, sms)
                } else {
                    Toast.makeText(this, "Заполните поле", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, id2 ->
                dialog.cancel()
            }
        builder.setTitle("Изменить сообщение:")
        builder.setCancelable(false)
        builder.show()
    }

    private fun addNewUserIntoChat() {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater

        val dialogView = inflater.inflate(R.layout.add_new_chat_name, null)
        var name = dialogView.findViewById<EditText>(R.id.et_name_of_chat)

        builder.setView(dialogView)
            .setPositiveButton("Add") { dialog, id ->
                val chatName = name.text.toString()
                if (chatName.isNotEmpty() && chatName != userLogin) {
                    vm.getSpecificUserByLogin("return=representation", "eq." + chatName)
                    userChatLogin = chatName
                    chatNameForResult = chatName
                } else {
                    Toast.makeText(this, "Этот пользователь уже есть в чате", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton("Cancel") { dialog, id ->
                dialog.cancel()
            }
        builder.setTitle("Добавить пользователя:")
        builder.setCancelable(false)
        builder.show()
    }

    private fun addNecessaryNewUserIntoChat() {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater

        val dialogView = inflater.inflate(R.layout.add_new_chat_name, null)
        var name = dialogView.findViewById<EditText>(R.id.et_name_of_chat)

        builder.setView(dialogView)
            .setPositiveButton("Add") { dialog, id ->
                val chatName = name.text.toString()
                if (chatName.isNotEmpty()) {
                    vm.getSpecificUserByLogin("return=representation", "eq." + chatName)
                    userChatLogin = chatName
                    isCheckedSize = true
                    vm.getSpecificUserByLogin("return=representation", "eq." + chatName)
                    userChatLogin = chatName
                } else {
                    Toast.makeText(this, "Заполните поле", Toast.LENGTH_SHORT).show()
                }
            }

        builder.setTitle("Добавить пользователя:")
        builder.setCancelable(false)
        builder.show()
    }

    private fun initRequests() {
    }

    private fun openFile(path: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        val file = File(path)
        var fileUri = Uri.fromFile(file)
        intent.setDataAndType(fileUri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    fun openFiles(context: Context, url: File) {
        // Create URI
        val uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", url)
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
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
    }

    @SuppressLint("MissingSuperCall")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
        var message = ""

        when (requestCode) {
            OPERATION_CHOOSE_PHOTO -> {
                if (resultCode == Activity.RESULT_OK) {

                    val pswdIterations = 65536
                    val keySize = 128

                    val saltBytes = byteArrayOf(0, 1, 2, 3, 4, 5, 6)

                    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                    val spec =
                        PBEKeySpec(secret?.toCharArray(), saltBytes, pswdIterations, keySize)
                    val secretKey = factory.generateSecret(spec)
                    secreto = SecretKeySpec(secretKey.encoded, "AES")

                    val image: String? = data?.data?.path
                    val path = this.filesDir.absolutePath

//                  val file = File(getExternalFilesDir(Environment.getExternalStorageState(externalMediaDirs)).absolutePath)

                    Log.d("aaaaa", "${data?.data}")

                    var selectedImageURI: Uri? = data?.data

                    val file = File(createCopyAndReturnRealPath(this, selectedImageURI!!)!!)

                    CoroutineScope(Dispatchers.IO).launch() {
                        val compressedImageFile = Compressor.compress(context, file)

                        val inputStream: InputStream =
                            FileInputStream(compressedImageFile) // You can get an inputStream using any I/O API
                        val bytes: ByteArray
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        val output = ByteArrayOutputStream()

                        try {
                            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        bytes = output.toByteArray()
                        var encodedString: String = Base64.getEncoder().encodeToString(bytes)
                        encodedString = encodedString
                        Log.d("chiko", encodedString.length.toString())

                        var compressedString: String? = ""
                        val bytess: ByteArray = encodedString.toByteArray(charset("UTF-8"))
                        val deflater = Deflater(1, true)
                        deflater.setInput(bytes)
                        deflater.finish()

                        val outputStream = ByteArrayOutputStream(bytes.size)

                        try {
                            val bytesCompressed = ByteArray(Short.MAX_VALUE.toInt())
                            val numberOfBytesAfterCompression = deflater.deflate(bytesCompressed)
                            val returnValues = ByteArray(numberOfBytesAfterCompression)
                            System.arraycopy(
                                bytesCompressed,
                                0,
                                returnValues,
                                0,
                                numberOfBytesAfterCompression
                            )

                            compressedString = Base64.getEncoder().encodeToString(returnValues)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } finally {
                            deflater.end()
                            outputStream.close()
                        }


//                        if (file == null) {
//
                        val plainTextByte: ByteArray =
                            encodedString!!.toByteArray((Charsets.UTF_8))
                        cipher!!.init(Cipher.ENCRYPT_MODE, secreto)
                        val encryptedByte: ByteArray = cipher!!.doFinal(plainTextByte)
                        val encoder = Base64.getEncoder()
                        message = encoder.encodeToString(encryptedByte)
                        Log.d("encrtext", message)
                        Log.d("BLOOOB", encodedString)
                        vm.createMessage(
                            "return=representation",
                            CreateMessageRequest(
                                currentUserIdInChat!!,
                                message.toString(),
                                "image",
                                "", arrayListOf(getUserChatId(this@Chat))
                            )
                        )
//                        }
                    }
                }
            }

            REQUEST_CODE_PICKER -> {
                var fileName = ""
                var selectedFileURI: Uri? = null

                if (data?.data != null) {
                    selectedFileURI = data?.data
                    val file = File(createCopyAndReturnRealPath(this, selectedFileURI!!)!!)

                    CoroutineScope(Dispatchers.IO).launch() {
                        var uriFile = getFilenameFromUri(data.data!!)

//                    uriFile = uriFile.dropLast(3)
//                    fileName = uriFile.replaceFirst(".$$$","");

//                    var indexOfPoint = uriFile.indexOf('.')
//                    fileName = uriFile.substring(indexOfPoint, uriFile.length)
//


                        Log.d("uriFile", "$fileName")
                        val inputStream: InputStream =
                            FileInputStream(file) // You can get an inputStream using any I/O API
                        val bytes: ByteArray
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        val output = ByteArrayOutputStream()

                        try {
                            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        bytes = output.toByteArray()
                        var encodedString: String = Base64.getEncoder().encodeToString(bytes)
                        Log.d("chiko", encodedString.toString())

                        var compressedString: String? = ""
                        val bytess: ByteArray = encodedString.toByteArray(Charsets.UTF_8)
                        val deflater = Deflater(1, true)
                        deflater.setInput(bytess)
                        deflater.finish()

                        val outputStream = ByteArrayOutputStream(bytess.size)

                        try {
                            val bytesCompressed = ByteArray(Short.MAX_VALUE.toInt())
                            val numberOfBytesAfterCompression = deflater.deflate(bytesCompressed)
                            val returnValues = ByteArray(numberOfBytesAfterCompression)
                            System.arraycopy(
                                bytesCompressed,
                                0,
                                returnValues,
                                0,
                                numberOfBytesAfterCompression
                            )
                            compressedString = Base64.getEncoder().encodeToString(returnValues)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } finally {
                            deflater.end()
                            outputStream.close()
                        }

//                        val input = encodedString.toByteArray(charset("UTF-8"))
//                        val output1 = ByteArray(input.size)
//                        val compresser = Deflater()
//                        compresser.setInput(input)
//                        compresser.finish()
//                        val compressedDataLength = compresser.deflate(output1)
//                        compresser.end()
//
//                        val str: ByteArray? = Base64.getEncoder().encode(output1)
//                        println("Deflated String:$str")

//                        val output2: ByteArray = Base64.getDecoder().decode(str)


                        val inputString =
                            "Pehla nasha Pehla khumaar Naya pyaar hai naya intezaar Kar loon main kya apna haal Aye dil-e-bekaraar Mere dil-e-bekaraar Tu hi bata Pehla nasha Pehla khumaar Udta hi firoon in hawaon mein kahin Ya main jhool jaoon in ghataon mein kahin Udta hi firoon in hawaon mein kahin Ya main jhool jaoon in ghataon mein kahin Ek kar doon aasmaan zameen Kaho yaaron kya karoon kya nahin Pehla nasha Pehla khumaar Naya pyaar hai naya intezaar Kar loon main kya apna haal Aye dil-e-bekaraar Mere dil-e-bekaraar Tu hi bata Pehla nasha Pehla khumaar Usne baat ki kuchh aise dhang se Sapne de gaya vo hazaaron range ke Usne baat ki kuchh aise dhang se Sapne de gaya vo hazaaron range ke Reh jaoon jaise main haar ke Aur choome vo mujhe pyaar se Pehla nasha Pehla khumaar Naya pyaar hai naya intezaar Kar loon main kya apna haal Aye dil-e-bekaraar Mere dil-e-bekaraar"
                        val input = encodedString.toByteArray(charset("UTF-8"))

                        // Compress the bytes

                        // Compress the bytes
                        val output1 = ByteArray(input.size)
                        val compresser = Deflater()
                        compresser.setInput(input)
                        compresser.finish()
                        val compressedDataLength = compresser.deflate(output1)
                        compresser.end()

                        val compressedStringa = Base64.getEncoder().encodeToString(output1)

//                        val output2: ByteArray = Base64.getDecoder().decode(compressedStringa)


                        val plainTextByte: ByteArray =
                            compressedString!!.toByteArray((Charsets.UTF_8))

                        cipher.init(Cipher.ENCRYPT_MODE, secreto)
                        val encryptedByte: ByteArray = cipher.doFinal(plainTextByte)
                        val encoder = Base64.getEncoder()
                        var message = encoder.encodeToString(encryptedByte)

                        Log.d("encrFILE", message + fileName)
                        Log.d("BLOOOBFILE", encodedString)
                        vm.createMessage(
                            "return=representation",
                            CreateMessageRequest(
                                currentUserIdInChat!!,
                                message.toString(),
                                "file",
                                uriFile, arrayListOf(getUserChatId(this@Chat))
                            )
                        )
                    }
                }
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
        val a = saveImage(this, path)
        startPlaying2(a!!.absolutePath)
    }


    private fun startPlaying2(filePath: String) {
        mediaPlayer = MediaPlayer()
        try {
            mediaPlayer!!.setDataSource(filePath) // pass reference to file to be played
            mediaPlayer!!.setAudioAttributes(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            ) // optional step
            mediaPlayer!!.prepare() // may take a while depending on the media, consider using .prepareAsync() for streaming
        } catch (e: IOException) { // we need to catch both errors in case of invalid or inaccessible resources
            // handle error
        } catch (e: IllegalArgumentException) {
            // handle error
        }
        mediaPlayer!!.start()
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
        mediaPlayer = null
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
        fileAudio = File(getExternalFilesDir(null)?.absolutePath + "record.mp3")
//        fileAudio = File.createTempFile("${fileName}.mp3", null, context.cacheDir)

//        val bytes = ByteArray(file.length().toInt())

        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder.setOutputFile(fileAudio)

        try {
            mediaRecorder.prepare()
            mediaRecorder.start()
            Toast.makeText(this, "RecordingStart", Toast.LENGTH_SHORT).show()
            startMillis = System.currentTimeMillis()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun stopRecording() {

        mediaRecorder.stop()
        mediaRecorder.release()
        endMillis = (System.currentTimeMillis() - startMillis!!)
//        fileAudio = File(getExternalFilesDir(null)?.absolutePath , "$fileName.mp3")

        val inputStream: InputStream =
            FileInputStream(fileAudio)
        fileAudio.delete()// You can get an inputStream using any I/O API
        val bytes: ByteArray
        val buffer = ByteArray(8192)
        var bytesRead: Int
        val output = ByteArrayOutputStream()

        try {
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        bytes = output.toByteArray()
        var encodedString: String = Base64.getEncoder().encodeToString(bytes)

        val plainTextByte: ByteArray =
            encodedString.toByteArray((Charsets.UTF_8))
        cipher.init(Cipher.ENCRYPT_MODE, secreto)
        val encryptedByte: ByteArray = cipher.doFinal(plainTextByte)
        val encoder = Base64.getEncoder()
        val message = encoder.encodeToString(encryptedByte)
        Log.d("encrFILE", message)
        Log.d("BLOOOBFILE", encodedString)
        vm.createMessage(
            "return=representation",
            CreateMessageRequest(
                currentUserIdInChat!!,
                message.toString(),
                "voice",
                "",
                arrayListOf(getUserChatId(this@Chat))
            )
        )

        Toast.makeText(this, "RecordingStop", Toast.LENGTH_SHORT).show()
//        vm.addSms(sms)
//        vm.getAllSms.observe(this) {
//            adapter.setListSMS(it)
//        }
    }

    fun saveImage(context: Context, imageData: String?): File? {
        val imgBytesData = android.util.Base64.decode(
            imageData,
            android.util.Base64.DEFAULT
        )
        val fileName = "voice" + System.currentTimeMillis() / 1000
        val file = File.createTempFile("${fileName}.mp3", null, context.cacheDir)

        val fileAudio =
            File(getExternalFilesDir(null)?.absolutePath + "/Mysounds/", "$fileName.mp3")

        val fileOutputStream: FileOutputStream
        fileOutputStream = try {
            FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        }
        val bufferedOutputStream = BufferedOutputStream(
            fileOutputStream
        )
        try {
            bufferedOutputStream.write(imgBytesData)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            try {
                bufferedOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    fun saveFiles(context: Context, imageData: String?, ext: String): File? {
        val imgBytesData = android.util.Base64.decode(
            imageData,
            android.util.Base64.DEFAULT
        )
        var fileName = "file" + System.currentTimeMillis() / 1000

        val file = File.createTempFile("CHIKOBEK" + "${ext}", null, context.cacheDir)

        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(imageData)

        val fileFile = File(getExternalFilesDir(null)?.absolutePath, "CHIKOBEK" + "${ext}")

        if (!fileFile.exists()) {
            fileFile.createNewFile()
        } else {
            return fileFile
        }

        val fileOutputStream: FileOutputStream
        fileOutputStream = try {
            FileOutputStream(fileFile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        }
        val bufferedOutputStream = BufferedOutputStream(
            fileOutputStream
        )
        try {
            bufferedOutputStream.write(imgBytesData)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            try {
                bufferedOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return fileFile
    }

    @Nullable
    fun createCopyAndReturnRealPath(
        context: Context, uri: Uri
    ): String? {
        val contentResolver = context.contentResolver ?: return null

        // Create file path inside app's data dir
        val filePath = context.applicationInfo.dataDir + File.separator.toString() + "temp_file"
        val file = File(filePath)
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()
        } catch (ignore: IOException) {
            return null
        }
        return file.absolutePath
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // finish()
    }

    private fun getInputData(): String? {
        val id = "owner.id=..."
        val name = "owner.name=..."
        val contact = "owner.contact=..."
        val tel = "owner.tel=..."
        val rc = System.getProperty("line.separator")
        val buf = StringBuffer()
        buf.append(id)
        buf.append(rc)
        buf.append(name)
        buf.append(rc)
        buf.append(contact)
        buf.append(rc)
        buf.append(tel)
        return buf.toString()
    }

    @Throws(IOException::class)
    private fun save(buf: ByteArray, file: String) {
        val fos = FileOutputStream(file)
        fos.write(buf)
        fos.close()
    }

    private fun showDialog(msg: String): AlertDialog {
        val dialog = MaterialAlertDialogBuilder(this)
        dialog.background = ResourcesCompat.getDrawable(resources, R.drawable.rounded, theme)
        dialog.setTitle(msg)
        dialog.setView(layoutInflater.inflate(R.layout.simple_dialog, binding.root, false))
        dialog.setCancelable(false)
        return dialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun actionBarCustomTitle(): TextView {
        return TextView(this).apply {
            if (chatDBName == "name") {
                text = chat_name
            } else {
                text = chatDBName
            }

            val params = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            // center align the text view/ action bar title
            params.gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = params

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setTextAppearance(
                    android.R.style.TextAppearance_DeviceDefault_Medium

                )
            } else {
                // define your own text style

                setTextSize(TypedValue.COMPLEX_UNIT_SP, 17F)
                setTypeface(null, Typeface.BOLD)

            }
            setTextColor(Color.parseColor("#FFFFFF"))
            //setTypeface(typeface,Typeface.BOLD)
        }
    }

    @SuppressLint("Range")
    fun getFilenameFromUri(uri: Uri): String {
        var result = ""
        val cursor = this.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        } catch (e: java.lang.Exception) {
        } finally {
            cursor?.close()
            return result
        }
    }

    override fun onResume() {
        super.onResume()
        callHitted = false
        CoroutineScope(Dispatchers.IO).launch {
            timerTask.scheduleAtFixedRate(object : TimerTask() {
                @RequiresApi(Build.VERSION_CODES.M)
                override fun run() {
                    lifecycleScope.launch {
//                        initial()
                        isOnline(this@Chat)
                    }
                }
            }, 100, 1000)
        }
    }

    override fun onPause() {
        super.onPause()
//        timerTask.cancel()
//        timerTask.purge()
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
        timerTask.cancel()
        timerTask.purge()
    }

    @Throws(IOException::class)
    private fun uncompressBytesInflateDeflate(inBytes: ByteArray): ByteArray? {
        val inflater = Inflater()
        inflater.setInput(inBytes)
        val bos = ByteArrayOutputStream(inBytes.size)
        val buffer = ByteArray(inBytes.size)
        while (!inflater.finished()) {
            var count: Int
            count = try {
                inflater.inflate(buffer)
            } catch (e: DataFormatException) {
                throw IOException(e)
            }
            bos.write(buffer, 0, count)
        }
        return bos.toByteArray()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decompress(string: String): String? {
        var decompressedString: String? = ""
        try {
            val bytes: ByteArray = Base64.getDecoder().decode(string)

            val inflater = Inflater(true)
            val outputStream = ByteArrayOutputStream()
            val buffer = ByteArray(string.length)
            inflater.setInput(bytes)

            while (!inflater.finished()) {
                val count = inflater.inflate(buffer)
                outputStream.write(buffer, 0, count)
            }
            inflater.end()
            outputStream.close()
            decompressedString = outputStream.toString("UTF8")

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return decompressedString
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ServiceCast")
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {

            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                vm.getSMSS("return=representation", "eq." + chat_name.toString())
//                Log.d("inet est","inet est")
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
//                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            } else {
                Log.d("inet net", "000")
                lifecycleScope.launch {
                    val a = vmDB.getAlls(chatOD)
                    Log.d("aaa", "${a}")
                    adapterDB.submitList(a)
                }
            }
        }
        return false
    }


    private fun deleteChatDialogIfNotExist(id: Int, string: String) {
        Toast.makeText(this, chatId.toString(), Toast.LENGTH_SHORT).show()

        val builder = AlertDialog.Builder(this)

        builder
            .setPositiveButton("Удалить") { dialog, id1 ->
                vmDB.deleteChat(ChatDatas(id, string))
                startActivity(Intent(this, HomeActivityKotlin::class.java))
            }
            .setNegativeButton("Отмена") { dialog, id2 ->
                dialog.dismiss()
                startActivity(Intent(this, HomeActivityKotlin::class.java))
            }
        builder.setTitle("Данный чат уже удален.Удалить чат?")
        builder.setCancelable(false)
        builder.show()
    }


}
