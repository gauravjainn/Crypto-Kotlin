package com.siltech.cryptochat.contacts

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.google.android.material.navigation.NavigationView

import com.google.common.reflect.TypeToken
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.siltech.cryptochat.*

import com.siltech.cryptochat.app.AppModule.Companion.context
import com.siltech.cryptochat.call.util.toast
import com.siltech.cryptochat.callUtils.CallNotification
import com.siltech.cryptochat.callUtils.PeerConnectionUsers
import com.siltech.cryptochat.chat.*
import com.siltech.cryptochat.contacts.addNewGroup.AddNewChatActivity
import com.siltech.cryptochat.contacts.addNewGroup.addNewGroupActivity
import com.siltech.cryptochat.contacts.userProfileSetting.UserProfileSettingActivity
import com.siltech.cryptochat.data.State
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.ChatDatas
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.ChatWithSMSDatas
import com.siltech.cryptochat.databinding.ActivityHomeBinding
import com.siltech.cryptochat.extensions.KdsWebSocketListener
import com.siltech.cryptochat.extensions.WebServicesProvider
import com.siltech.cryptochat.model.*
import com.siltech.cryptochat.utils.*
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.reflect.Type
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.net.ssl.*
import kotlin.collections.ArrayList
import android.media.RingtoneManager
import ChatDBViewModel
import android.media.Ringtone
import android.net.Uri
import org.json.JSONException


class HomeActivityKotlin : AppCompatActivity(), OnSocketConnectionListener {
    private lateinit var session: SessionManager
    private var mSocket: Socket? = null

    var btn_settings: ImageButton? = null
    val timerTask: Timer = Timer()

    lateinit var toggle: ActionBarDrawerToggle

    private val creatorId: Int
        get() = intent.getIntExtra("cr_id", 0)

    private val userLogin: String
        get() = intent.getStringExtra("user_login").toString()

    private val nameOfCreatedChat: String
        get() = intent.getStringExtra("nameOfCreatedChat").toString()

    private var userChatLogin = ""
    private var userChatLoginAdd = ""
    private var userIntoChatID: Int? = null
    private var chatDBName = ""
    private var chatForResult = false


    private var secondUserLogin = ""
    private var chatIDUSER = 0

    var chatId: Int? = null

    private var chat_name = ""

    var listas: ArrayList<ChatDatas>? = null

    var currentUserChatId: Int? = null

    private lateinit var binding: ActivityHomeBinding
    lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContactsAdapter
    private lateinit var adapterDB: ContactsDBAdapter

    private val vm: ChatViewModel by viewModel()
    private val vmDB: ChatDBViewModel by viewModel()

    var list: ArrayList<UsersChatResponseItem>? = null
    var listDB: List<ChatWithSMSDatas>? = null
    lateinit var builderLoadinfDialog: AlertDialog.Builder
    lateinit var callNotification: CallNotification

    lateinit var notification: Uri
    lateinit var ringtone: Ringtone


    @ExperimentalCoroutinesApi
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
//        connectToSocket()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.navigationBarColor =
            ContextCompat.getColor(this@HomeActivityKotlin, R.color.light_blue)
        supportActionBar?.hide()

        if (getCHATADDED(this) == false) {
            listas = arrayListOf()
        } else {
            var sharedPrefsa = PreferenceManager.getDefaultSharedPreferences(context)
            var gsona = Gson()
            var jsona = sharedPrefsa.getString(TAG, "")
            var type: Type = object : TypeToken<ArrayList<ChatDatas?>?>() {}.getType()
            var arrayList: ArrayList<ChatDatas> = gsona.fromJson(jsona, type)
            listas = arrayList
        }

        val webSockeLis = KdsWebSocketListener()
        val prod = WebServicesProvider()
        val repo = MainRepository(prod)
        val iterator = MainInteractor(repo)
        val weBsocketChat = WeBsocketChat(iterator)
        weBsocketChat.subscribeToSocketEvents()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        generateSecretKey()


//        Navigation Drawer
        setSupportActionBar(findViewById(R.id.my_toolbar))

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val userLogin = userLogin

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.white)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.add_new_user -> {
                    val intent = Intent(this, AddNewChatActivity::class.java)
                    startActivity(intent)

                }
                R.id.new_group -> startActivity(
                    Intent(
                        this@HomeActivityKotlin,
                        addNewGroupActivity::class.java
                    )
                )
                R.id.setting -> startActivity(
                    Intent(
                        this@HomeActivityKotlin,
                        UserProfileSettingActivity::class.java
                    )
                )

            }
            true
        }
//        setting value to Nav Header
        var navHeader = navView.getHeaderView(0)
        var loginOfUser = navHeader.findViewById<TextView>(R.id.user_login)
        loginOfUser.text = userLogin
//      set app Name
        val appName = navView.findViewById<TextView>(R.id.name_of_app)
        val appNameFromGradle = applicationInfo.loadLabel(packageManager).toString()
        appName.text = appNameFromGradle

//        set Version
        val appVersion = navView.findViewById<TextView>(R.id.version)
        val appVersionFromGradle = BuildConfig.VERSION_NAME
        appVersion.text = appVersionFromGradle

////      set red button
        val getMenu = navView.menu
        val redButton = binding.wipeDataBtn
        redButton.setOnClickListener {
            createAlertDialoge()
        }





        builderLoadinfDialog = AlertDialog.Builder(this)

        binding.refreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
//                initial()
            }
            binding.refreshLayout.isRefreshing = false
        }
        listDB = ArrayList()
        adapterDB = ContactsDBAdapter(listDB!!, {
            vm.getChatForResult("eq." + it.owner.name)
            val intent = Intent(this, Chat::class.java)
            intent.putExtra("chatOD", it.owner.idChat)
            intent.putExtra("currentUserChatId", currentUserChatId)
            intent.putExtra("user_id", creatorId)
            intent.putExtra("chat_name", it.owner.name)
            intent.putExtra("userLogin", userLogin)
            intent.putExtra("chatId", chatId)
            intent.putExtra("chatDBname", it.owner.nameDB)
            intent.putExtra("secondUserId", userIntoChatID)
            intent.putExtra("secondUserLogin", userChatLoginAdd)

            startActivity(intent)
        }, {
            if (isOnline(this)) {
                chooseActionForChat(it.owner.name, it.owner.idChat)
            }
        })

        list = ArrayList()
        adapter = ContactsAdapter(list!!, {
            startDialog(it.chatName)
        }) {
            val name = it.chatName
//            chooseActionForChat(name)
        }
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapterDB
        vm.getChats("eq.$userLogin")

        binding.fabBtn.setOnClickListener {
            if (isOnline(this)) {
                showAddNewChatDialog()
                adapter.notifyDataSetChanged()
            }
        }

        vm.state.observe(this) {
            when (it) {
                is State.LoadingState -> {
                    if (it.isLoading) {
                    }
                }
                is State.ErrorState -> {
                    ""
                }
                is State.SuccessListState<*> -> {
                    when (if (it.data.isEmpty())

                        lifecycleScope.launch {
                            adapterDB.setListSMS(vmDB.getAllChats())

                        }
                    else it.data[0]) {

                        is UsersChatResponseItem -> {
                            if (it.data.isNotEmpty()) {
                                lifecycleScope.launch {
                                    adapterDB.setListSMS(vmDB.getAllChats())
                                }

//                                adapter.setListSMS(it.data as ArrayList<UsersChatResponseItem>)
                                for (chatss in it.data as ArrayList<UsersChatResponseItem>) {
                                    val chat = ChatDatas(chatss.chatId, chatss.chatName)
                                    val listoofCHats = ArrayList<ChatDatas>()

                                    if (listas!!.size == it.data.size && !listas!!.contains(chat)) {
                                        listas!!.add(chat)
                                        vmDB.addChat(chat)
                                        Log.d("LIST is ADDED", listas.toString())

                                    } else if (listas!!.size != it.data.size && !listas!!.contains(
                                            chat
                                        )
                                    ) {
                                        listas!!.add(chat)
                                        vmDB.addChat(chat)
                                        Log.d("LIST IS EMPTY", listas.toString())

                                        val sharedPrefs =
                                            PreferenceManager.getDefaultSharedPreferences(context)
                                        val editor: SharedPreferences.Editor = sharedPrefs.edit()
                                        val gson = Gson()

                                        val json = gson.toJson(listas)

                                        editor.putString(TAG, json)
                                        editor.commit()
                                        saveCHATADDED(this, true)
                                    }
                                }


                            }
                        }

                        is GetChatUsersResponseItemX -> {
                            Log.d("listUsers", "YOUHERERRR")

                            Toast.makeText(
                                this,
                                "CHE TAJ!",
                                Toast.LENGTH_SHORT
                            ).show()

                            val listUsersss =
                                it.data as java.util.ArrayList<GetChatUsersResponseItemX>
                            Log.d("listUsers", listUsersss.toString())
                            Log.d("userIntoChatId is emtpy", "${userIntoChatID}")

                            val user = GetChatUsersResponseItemX(
                                chat_name.toString(), false,
                                userChatLoginAdd.toString(), userIntoChatID.toString()
                            )

                            if (userIntoChatID != null) {

                            } else {
                                Log.d("userIntoChatId is emtpy", "${userIntoChatID}")

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
                                Log.d("userIntoChatID", "${it.data[0].id}")

                                if (chatForResult) {
                                    chatForResult = false
                                    Toast.makeText(
                                        this,
                                        "Такой чат уже существует",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    vm.createNewChat(
                                        "return=representation", ChatCreateModel(
                                            "$userLogin-$userChatLoginAdd",
                                            creatorId
                                        )
                                    )
                                }

                            } else {
                                Toast.makeText(
                                    this,
                                    "Такого пользователя не существует!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
//
//                            if(it.data.isNotEmpty()){
//
//                                val addNewUsersRequest = AddNewUsersRequest()
//                                val mainUser = AddNewUsersRequestItem(
//                                    it.data[0].id as Int,
//                                    chatIDUSER!!,
//                                    false)
//                                Log.d("userStartTOADD","userID:${it.data[0].id}")
//
//                                addNewUsersRequest.add(mainUser)
//                                vm.addUsersIntoChat("return=representation", addNewUsersRequest)
//                            }else{
//                                Toast.makeText(
//                                    this,
//                                    "Такого пользователя не существует!",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
                        }

                        is UsersChatResponse -> {
                        }

                        is GetUserIdInChatResponse -> {
                        }

                        is ChatsListResponse -> {

                            vm.getChats("eq." + userLogin)
                            if (it.data.isNotEmpty()) {
                                chatId = it.data[0].id
                                chatForResult = true
                                vm.getSpecificUserByLogin(
                                    "return=representation",
                                    "eq." + userChatLoginAdd
                                )

                            } else {
                                vm.getSpecificUserByLogin(
                                    "return=representation",
                                    "eq." + userChatLoginAdd
                                )

                            }

                        }

                        is AddUsersResponse -> {
//                            saveUserChatId(this, it.data[0].id)
                            vm.getChats("eq." + userLogin)
                            currentUserChatId = it.data[0].id
                        }

                        is CreateChatResponse -> {
                            chat_name = it.data[0].name
                            chatIDUSER = it.data[0].id
                            val addNewUsersRequest = AddNewUsersRequest()
                            vm.getUsersChat("eq." + chat_name.toString())

                            addNewUsersRequest.add(
                                AddNewUsersRequestItem(
                                    creatorId.toInt(),
                                    it.data[0].id, true
                                )
                            )

                            Log.d("CHATCREATED", "chatID:${chatIDUSER}")
//                            vm.getSpecificUserByLogin("return=representation", "eq." + userChatLogin)
                            vm.addUsersIntoChat("return=representation", addNewUsersRequest)

                            val addNewUsersRequesta = AddNewUsersRequest()

                            val mainUser = AddNewUsersRequestItem(
                                userIntoChatID as Int,
                                chatIDUSER!!,
                                false
                            )

                            addNewUsersRequesta.add(mainUser)
                            vm.addUsersIntoChat("return=representation", addNewUsersRequesta)
                            Toast.makeText(
                                this,
                                "Добавлен: ${userChatLoginAdd}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("size", "${it.data.size}")

//                            vm.getChats("eq." + userLogin)
//                            vm.getUserIdInChat(
//                                "return=representation",
//                                "eq." + creatorId.toString(),
//                                "eq." + chatId
//                            )

                        }

                        is ChangeNameOfChatResponse -> {
                            vm.getChats("eq.$userLogin")
                        }

                        is DeletedChatResponse -> {
                            vm.getChats("eq.$userLogin")
                        }
                    }
                }
                else ->{}
            }
        }
//        btn_settings = findViewById<View>(R.id.btn_setting) as ImageButton
//        btn_settings!!.setOnClickListener { //send to choose screen
//            val intent = Intent(applicationContext, SettingsActivity::class.java)
//            startActivity(intent)
//        }

//        setSocketEvents()
    }

    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        checkCallRequiredPermission()
    }

    private fun checkCallRequiredPermission() {
        if (checkCallRequiredPermissions(this).isNotEmpty()) {
            permissionsResultCallback.launch(checkCallRequiredPermissions(this).toTypedArray())
        }
    }

    private fun init() {
        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(applicationContext, notification)

        callNotification = CallNotification(this)
        session = SessionManager(this)
        if (checkCallRequiredPermissions(this).isNotEmpty()) {
            permissionsResultCallback.launch(checkCallRequiredPermissions(this).toTypedArray())
        }
    }


    private fun connectToSocket() {
        mSocket = SocketManager.instance!!.getSocket()
        mSocket!!.connect()
    }


    private fun setSocketEvents() {
        mSocket!!.on("callUser", onNewCall)
    }

    fun socketEmitForDisconnect(
        callerSocketId: String,
        receiverSocketId: String,
        creatorId: String
    ) {
        ringtone.stop()
        if (mSocket!!.connected()) {
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

                userCallObject.put("userId", session.userLoggedInID)
                userCallObject.put("receiverId", creatorId)

                userCallObject.put("userToCall", callerSocketId)
                userCallObject.put("from", receiverSocketId)

                userCallObject.put("signalData", peerJson)
                userCallObject.put("name", "Android")


            } catch (e: JSONException) {
                e.printStackTrace()
            }

            mSocket!!.emit("callUser", userCallObject)

        } else {
            Toast.makeText(context, "Socket is not Connected", Toast.LENGTH_SHORT).show()
        }
    }

    private val onNewCall: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            Log.d("TAG", "SocketCallData==> $data")
            val signalData = data.getString("signal")
            val gson = Gson()
            val peerData = gson.fromJson(signalData, PeerConnectionUsers::class.java)

            if (!peerData?.connected!!) {
                if (peerData.available!!) {
                    ringtone.play()
                    callNotification.signalConnectionId = peerData.connId!!
                    callNotification.callerName.text = peerData.userCallingName!!
                    callNotification.creatorId = peerData.userID!!
                    callNotification.currentUserId = session.userLoggedInID!!

                    callNotification.callerSocketId = peerData.callerSocketId!!
                    callNotification.receiverSocketId = peerData.receiverSocketId!!

                    callNotification.callerUserId = session.userLoggedInID!!
                    callNotification.receiverUserId = peerData.userID
                    callNotification.show()
                } else {
                    ringtone.stop()
                    callNotification.dismiss()
                }

            } else {
                ringtone.stop()
                callNotification.dismiss()
            }
        }

    }


    private fun createAlertDialoge() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Вы уверены что хотите очистить данные? \nЭто действие необратимо.")
        builder.setPositiveButton(
            "Да"
        ) { dialog, which -> clearAppData() }
        builder.setNegativeButton(
            "Нет"
        ) { dialog, which -> }
        builder.create()
        builder.show()
    }

    private fun clearAppData() {
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                (getSystemService(ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData() // note: it has a return value!
            } else {
                val packageName = applicationContext.packageName
                val runtime = Runtime.getRuntime()
                runtime.exec("pm clear $packageName")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)


        val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                    return arrayOf()
                }
            }
        )
        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

        OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        OkHttpClient.Builder().hostnameVerifier { hostname, session -> true }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    suspend fun initial() {
        if (!internetAvailability()) {
//            Log.d("aChats", vmDB.getAllChats().toString())
//            binding.recyclerView.adapter = adapterDB
            adapterDB.setListSMS(vmDB.getAllChats())

        } else {
            vm.getChats("eq." + userLogin)
//            binding.recyclerView.adapter = adapter
        }
    }

    private fun startDialog(string: String) {
        vm.getChatForResult("eq." + string)
        val intent = Intent(this, Chat::class.java)
        intent.putExtra("currentUserChatId", currentUserChatId)
        intent.putExtra("user_id", creatorId)
        intent.putExtra("chat_name", string)
        intent.putExtra("userLogin", userLogin)
        intent.putExtra("chatId", chatId)

        println(currentUserChatId)
        println(chatId)
        println(creatorId)
        println(string)

        startActivity(intent)
    }

    private fun chooseActionForChat(string: String, id: Int) {
        val builder = AlertDialog.Builder(this)

        builder
            .setPositiveButton("Изменить") { dialog, id1 ->
                showChangeNameOfChatDialog(string, id)
                dialog.cancel()
            }
            .setNegativeButton("Удалить") { dialog, id2 ->
                deleteChatDialog(string, id)
                dialog.cancel()
            }
        builder.setTitle("Выберите, что хотите сделать?")
        builder.setCancelable(true)
        builder.show()
    }

    private fun deleteChatDialog(string: String, id: Int) {
        vm.getChatForResult("eq." + string)
        Toast.makeText(this, chatId.toString(), Toast.LENGTH_SHORT).show()

        val builder = AlertDialog.Builder(this)

        builder
            .setPositiveButton("Да") { dialog, id1 ->
                vm.deleteChat("return=representation", "eq.$chatId")
                vmDB.deleteChat(ChatDatas(id, string))

            }
            .setNegativeButton("Нет") { dialog, id2 ->
                dialog.dismiss()
            }
        builder.setTitle("Вы уверены, что хотите удалить чат: ${string}?")
        builder.setCancelable(false)
        builder.show()
    }

    private fun deleteChatDialogIfNotExist() {
        Toast.makeText(this, chatId.toString(), Toast.LENGTH_SHORT).show()

        val builder = AlertDialog.Builder(this)

        builder
            .setPositiveButton("Ок") { dialog, id1 ->
//                vmDB.deleteChat(ChatDatas(id, string))

            }
            .setNegativeButton("Отмена") { dialog, id2 ->
                dialog.dismiss()
            }
        builder.setTitle("Данный чат уже удален.Пожалуйста удалите этот чат. ")
        builder.setCancelable(false)
        builder.show()
    }

    private fun showChangeNameOfChatDialog(string: String, id: Int) {
//        vm.getChatForResult("eq." + string)
//        Toast.makeText(this, chatId.toString(), Toast.LENGTH_SHORT).show()

        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater

        val dialogView = inflater.inflate(R.layout.add_new_chat_name, null)
        var name = dialogView.findViewById<EditText>(R.id.et_name_of_chat)

        builder.setView(dialogView)
            .setPositiveButton("OK") { dialog, id1 ->
                val chatName = name.text.toString()
                if (chatName.isNotEmpty()) {
                    chatDBName = chatName
                    vmDB.updateChat(
                        ChatDatas(
                            id,
                            string,
                            chatName
                        )
                    )//                    vm.changeNameOfChat(
//                        "return=representation",
//                        "eq.$chatId",
//                        chatName
//                   )
//                    adapter.notifyDataSetChanged()
                    lifecycleScope.launch {
                        Log.d("12345", "${vmDB.getAllChats()}")
                    }
                } else {
                    Toast.makeText(this, "Заполните поле", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, id2 ->
                dialog.cancel()
            }
        builder.setTitle("Выберите название чата:")
        builder.setCancelable(false)
        builder.show()
    }

    private fun showAddNewChatDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater

        val dialogView = inflater.inflate(R.layout.add_new_chat_name, null)
        var name = dialogView.findViewById<EditText>(R.id.et_name_of_chat)

        builder.setView(dialogView)
            .setPositiveButton("Add") { dialog, id ->
                val chatName = name.text.toString()
                secondUserLogin = chatName
                if (chatName.isNotEmpty() && chatName != userLogin) {
                    vm.getChatForResult("eq." + "${userLogin}-${chatName}")
                    userChatLoginAdd = chatName
                    vm.getChats("eq." + userLogin)
                } else {
                    Toast.makeText(this, "Этот пользователь уже есть в чате", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton("Cancel") { dialog, id ->
                dialog.cancel()
            }
        builder.setTitle("Добавьте пользователя по его логину:")

        builder.setCancelable(false)
        builder.show()
    }

    private fun showDialog(msg: String): AlertDialog {
        val dialog = MaterialAlertDialogBuilder(this)
        dialog.background = ResourcesCompat.getDrawable(resources, R.drawable.rounded, theme)
        dialog.setTitle(msg)
        dialog.setView(layoutInflater.inflate(R.layout.simple_dialog, binding.root, false))
        dialog.setCancelable(false)
        return dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        val intent = Intent(Intent.ACTION_MAIN)
//        intent.addCategory(Intent.CATEGORY_HOME)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        startActivity(intent)
//        finish()
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
                vm.getChats("eq." + userLogin)
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
//                Log.d("inet net","inet net")
                lifecycleScope.launch {
                    adapterDB.setListSMS(vmDB.getAllChats())
                }
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket!!.disconnect()
        timerTask.cancel()
        timerTask.purge()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
//        lifecycleScope.launch(Dispatchers.IO) {
//            timerTask.scheduleAtFixedRate(object : TimerTask() {
//                override fun run() {
//                    vm.getChats("eq." + userLogin)
//                }
//            }, 100, 2500)
//        }

        lifecycleScope.launch(Dispatchers.IO) {
            timerTask.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    lifecycleScope.launch {
//                        isOnline(this@HomeActivityKotlin)
                    }
                }
            }, 100, 1000)
        }
    }

    override fun onSocketEventFailed() {


    }

    override fun onSocketConnectionStateChange(socketState: Int) {
        toast(socketState.toString())
    }

    override fun onInternetConnectionStateChange(socketState: Int) {
    }
}

fun savePublickey() {

}


@Throws(Exception::class)
fun generateSecretKey(): SecretKey? {
    val secureRandom = SecureRandom()
    val keyGenerator = KeyGenerator.getInstance("AES")
    //generate a key with secure random
    keyGenerator?.init(128, secureRandom)
    Log.d("secretKeyCreated", "${keyGenerator?.generateKey()}")
    return keyGenerator?.generateKey()
}








