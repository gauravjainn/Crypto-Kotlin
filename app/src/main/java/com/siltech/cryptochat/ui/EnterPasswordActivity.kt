package com.siltech.cryptochat.ui

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import android.os.Bundle
import android.content.pm.ActivityInfo
import androidx.core.content.ContextCompat
import android.content.Intent
import android.content.SharedPreferences
import com.siltech.cryptochat.contacts.HomeActivityKotlin
import android.widget.Toast
import android.os.Build
import android.app.ActivityManager
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import com.siltech.cryptochat.*
import com.siltech.cryptochat.app.AppModule.Companion.context
import com.siltech.cryptochat.chat.ChatViewModel
import com.siltech.cryptochat.data.State
import com.siltech.cryptochat.model.CreateChatResponseItem
import com.siltech.cryptochat.model.CreateUserResponse
import com.siltech.cryptochat.model.User
import com.siltech.cryptochat.updater.AppUpdater
import com.siltech.cryptochat.updater.helper.Display
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Exception

class EnterPasswordActivity : AppCompatActivity() {

    private var appUpdater: AppUpdater? = null
    var editTextPassword: EditText? = null
    var button: Button? = null
    var resetPassword: TextView? = null
    var id: Int? = null
    private var counter = 5
    var userId: Int? = null
    var userLogin: String? = null
    var password1: String? = null
    private val viewModel: ChatViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_password)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.navigationBarColor = ContextCompat.getColor(this, R.color.light_blue)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        val intent = intent
        userId = intent.getIntExtra("cr_id", 0)
        userLogin = intent.getStringExtra("user_login")
        password1 = getSharedPreferences("PREFS1", 0).getString("password1", "")

        editTextPassword = findViewById<View>(R.id.editTextTextPassword) as EditText
        button = findViewById<View>(R.id.enter_button) as Button
        button!!.setOnClickListener {

            Log.d("userID", "" + userId)
            Log.d("userLogin", "" + userLogin)
            Log.d("password1", "" + password1)
            Log.e("PASSWORD", "getUserID : " + getUserID(this));
            Log.e("PASSWORD", "getUserLogin : " + getUserLogin(this));
            Log.e("PASSWORD", "getUserChatId : " + getUserChatId(this));

            viewModel.getUserInfoCurrent("return=representation", "eq." + getUserLogin(this).toString())
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

                            Log.d("PASSWORD", "userID : " + userId)
                            Log.d("PASSWORD", "userLogin : " + userLogin)
                            Log.d("PASSWORD", "password1 : " + password1)
                            Log.e("PASSWORD", "getUserID : " + getUserID(this));
                            Log.e("PASSWORD", "getUserLogin : " + getUserLogin(this));
                            Log.e("PASSWORD", "getUserChatId : " + getUserChatId(this));
                            Log.e("PASSWORD", "getSecretKey : " + getSecretKey(this).toString())
                            Log.e("PASSWORD", "getUserToken : " + getUserToken(this).toString())

                            var public_key = getSecretKey(this);
                            if (it.data[0].publicKey == public_key) {
                                if (editTextPassword!!.text.toString() == password1) {
                                    val id = getIntent().getIntExtra("id", 1)
                                    val intent = Intent(applicationContext, HomeActivityKotlin::class.java)
                                    intent.putExtra("cr_id", userId)
                                    intent.putExtra("user_login", userLogin)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    counter--
                                    //Toast.makeText(EnterPasswordActivity.this, "Неверный пароль, осталось попыток "+ counter , Toast.LENGTH_SHORT).show();
                                    showToastMessage("Неверный пароль, осталось попыток $counter", 500)
                                    if (counter == 0) {
                                        clearAppData()
                                        //eLogin.setEnabled(false);
                                        Toast.makeText(
                                                this@EnterPasswordActivity,
                                                "Вы использовали все свои попытки, попробуйте еще раз позже!",
                                                Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            } else {
                                Toast.makeText(
                                        this@EnterPasswordActivity,
                                        "Ваш аккаунт заблокирован, обратитесь в тех.поддержку",
                                        Toast.LENGTH_LONG
                                ).show()
                            }

                        }

                    }
                }
                is State.SuccessListState<*> -> {
                    when (if (it.data.isEmpty()) null else it.data[0]) {
                        is User -> {

                        }
                        is CreateChatResponseItem -> {
                            Log.e("userPublicKey", "${it.data}")

                        }
                    }
                }
            }
        }
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showToastMessage(text: String?, duration: Int) {
        val toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast.show()
        val handler = Handler()
        handler.postDelayed({ toast.cancel() }, duration.toLong())
    }


    override fun onResume() {
        super.onResume()

        appUpdater = AppUpdater(this)
        appUpdater!!.setDisplay(Display.NOTIFICATION)
        appUpdater!!.setUpGithub("HelicopterHig", "ChatUpdater")
        appUpdater!!.start()
    }
}