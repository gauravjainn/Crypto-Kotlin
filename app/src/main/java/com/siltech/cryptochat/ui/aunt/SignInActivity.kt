package com.siltech.cryptochat.ui.aunt

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.siltech.cryptochat.*
import com.siltech.cryptochat.chat.ChatViewModel
import com.siltech.cryptochat.contacts.HomeActivityKotlin
import com.siltech.cryptochat.data.State
import com.siltech.cryptochat.databinding.ActivitySigninBinding
import com.siltech.cryptochat.model.CreateUserResponse
import com.siltech.cryptochat.model.User
import com.siltech.cryptochat.ui.EnterPasswordActivity
import com.siltech.cryptochat.ui.Test
import com.siltech.cryptochat.updater.AppUpdater
import com.siltech.cryptochat.updater.helper.Display
import com.siltech.cryptochat.utils.SessionManager
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.nio.charset.StandardCharsets
import javax.crypto.SecretKey

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding
    private val viewModel: ChatViewModel by viewModel()
    private lateinit var session:SessionManager
    private val viewModelLogin: LoginViewModel by viewModel()

    private var login: String? = null
    private var appUpdater: AppUpdater? = null
    var createId: Int? = null
    var password1: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.navigationBarColor = ContextCompat.getColor(this@SignInActivity, R.color.light_blue)
        supportActionBar?.hide()
        setContentView(R.layout.activity_signin)
        init()


        val settings = getSharedPreferences("PREFS1", 0)
        password1 = settings.getString("password1", "")

        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.signinBtn.setOnClickListener {
            if (hasConnection(this)) {
                login = binding.loginEditText.text.toString()

                if (generateSecretKey() != null) {
                    saveSecretKey(this, generateSecretKey().toString())
                }
                if (generateJWTToken() != null) {
                    saveUserToken(this, generateJWTToken().toString())
                }

                Log.e("SIGNIN", "getSecretKey : "+getSecretKey(this).toString())
                Log.e("SIGNIN", "getUserToken : "+getUserToken(this).toString())

                viewModel.getUserInfo(login.toString())
                viewModelLogin.userModel.observe(this) {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivityKotlin::class.java)
                    startActivity(intent)
                }
                viewModel.state.observe(this) {
                    when (it) {
                        is State.LoadingState -> {
                            if (it.isLoading) {
                            } else {
                            }
                        }
                        is State.ErrorState -> {
                            ""
                        }
                        is State.SuccessObjectState<*> -> {
                            when (it.data) {
                                is CreateUserResponse -> {

                                    if (it.data[0].publicKey == null) {
                                        Log.e("SIGNIN", "getSecretKey : "+getSecretKey(this).toString())
                                        Log.e("SIGNIN", "getUserToken : "+getUserToken(this).toString())
                                        viewModel.updateUserInfoPublicKey("eq." + it.data[0].id.toString(),
                                                getSecretKey(this).toString()
                                        )
                                    }

                                    val handler = Handler()
                                    handler.postDelayed({

                                        if (it.data[0].publicKey != null) {

                                            saveSecretKey(this, it.data[0].publicKey.toString())
                                            saveUserID(this, it.data[0].id)
                                            saveUserLOGIN(this, login.toString())
                                            saveUserChatId(this, it.data[0].id)
                                            session.setUserLoginId(it.data[0].id.toString())
                                            session.setUserLoginName(it.data[0].login)
                                            Log.d("userID", "${it.data[0].id}")
                                            Log.d("login", "$login")
                                            Log.e("SIGNIN","getUserID : "+getUserID(this));
                                            Log.e("SIGNIN","getUserLogin : "+getUserLogin(this));
                                            Log.e("SIGNIN","getUserChatId : "+ getUserChatId(this));
                                            Log.e("SIGNIN", "getSecretKey : "+getSecretKey(this).toString())
                                            Log.e("SIGNIN", "getUserToken : "+getUserToken(this).toString())

                                            if (password1 == "") {

                                                val intent = Intent(applicationContext, Test::class.java)
                                                val settings2: SharedPreferences.Editor = getSharedPreferences("PREFS2", 0).edit()
                                                settings2.putInt("id", it.data[0].id)
                                                settings2.putString("user_login", login)
                                                intent.putExtra("cr_id", it.data[0].id)
                                                intent.putExtra("user_login", login)
                                                startActivity(intent)
                                                finish()

                                            } else {
                                                session.setUserLoginId(it.data[0].id.toString())
                                                session.setUserLoginName(it.data[0].login)
                                                val intent = Intent(
                                                        applicationContext,
                                                        EnterPasswordActivity::class.java
                                                )

                                                intent.putExtra("cr_id", it.data[0].id)
                                                intent.putExtra("user_login", login)

                                                startActivity(intent)
                                                finish()
                                            }
                                        } else {
                                            Toast.makeText(this, "Вы не можете авторизоваться под эти логином", Toast.LENGTH_SHORT).show()
                                        }
                                    }, 0)
                                }

                            }
                        }
                        is State.SuccessListState<*> -> {
                            when (if (it.data.isEmpty()) null else it.data[0]) {
                                is User -> {

                                }
                            }
                        }
                    }

                }

            } else Toast.makeText(this, "Нет интернет соединения... ", Toast.LENGTH_LONG).show()
        }


    }

    fun init(){
        session = SessionManager(this)

    }

    fun publicKeyCondition(): Boolean {

        return true
    }

    private fun keyForSecret(): SecretKey? {
        val secretDev =
                "ULm3UyMOtGb0sa6bzGPZSWt7ovGQpORRQ7tXJzj2W5fGkVMhV57645JVqolE9sKISB1aHlazMM6gp2M0UPZdUSGg3rOUUJ9YLO4cX5ANaJel5OsojLrtJFgWceHmyqp9uewU7m3r9JobzaLksGKR19YNHraiMEOMTPHebQeR82DolmvvayVd0DK0NhcFGoN5wgLqwFKNlD1F16o2LE3je2IoWLgXyvzkkTb4bPFXZACfjoZfqiNx7FXWCB8b3BZR"

        var devSecret = Keys.hmacShaKeyFor(secretDev.toByteArray(StandardCharsets.UTF_8))
        return devSecret
    }

    fun generateJWTToken(): String? {

        val key = keyForSecret()
        val tokenGen = Jwts.builder()
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT").claim("role", login)
                .signWith(key, SignatureAlgorithm.HS256).compact()
        return tokenGen
    }

    fun generateSecretKey(): String? {
        val deviceID =
                Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)

        val publicKeyGen = Jwts.builder()
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT").claim("role", login).claim("role", deviceID)
                .signWith(keyForSecret(), SignatureAlgorithm.HS256).compact()

        return publicKeyGen
    }


    fun hasConnection(context: Context): Boolean {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNW = cm.activeNetworkInfo
        return activeNW != null && activeNW.isConnected
    }

    override fun onResume() {
        super.onResume()

        appUpdater = AppUpdater(this)
        appUpdater!!.setDisplay(Display.NOTIFICATION)
        appUpdater!!.setUpGithub("HelicopterHig", "ChatUpdater")
        appUpdater!!.start()
    }


    override fun onStop() {
        super.onStop()

        Log.d("ACTIVITY_LIFECYCLE", "onStop Called")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("ACTIVITY_LIFECYCLE", "onDestroy Called")
    }


}