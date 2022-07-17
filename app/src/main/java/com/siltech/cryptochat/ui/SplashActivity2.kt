package com.siltech.cryptochat.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.siltech.cryptochat.R
import com.siltech.cryptochat.chat.ChatViewModel
import com.siltech.cryptochat.getUserID
import com.siltech.cryptochat.getUserLogin
import com.siltech.cryptochat.ui.aunt.SignInActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.*
import java.net.URL


@SuppressLint("CustomSplashScreen")
class SplashActivity2 : AppCompatActivity() {
    var userId: Int? = null
    var userLogin: String? = null
    var password1: String? = null
    val chatViewModel:ChatViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.navigationBarColor = ContextCompat.getColor(this@SplashActivity2, R.color.light_blue)
        setContentView(R.layout.splash_activty)
        supportActionBar!!.hide()

        val settings2 = getSharedPreferences("PREFS2", 0)
        userId = settings2.getInt("id", 0)
        userLogin = settings2.getString("user_login", "")
        val settings = getSharedPreferences("PREFS1", 0)
        password1 = settings.getString("password1", "")
        val handler = Handler()
        handler.postDelayed({
            // if no password
            if (password1 == "") {
                val intent = Intent(applicationContext, SignInActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // if there is a password
                val intent = Intent(applicationContext, EnterPasswordActivity::class.java)
                intent.putExtra("cr_id", getUserID(this))
                intent.putExtra("user_login", getUserLogin(this))
                Log.e("SPLASH","cr_id : "+getUserID(this));
                Log.e("SPLASH","user_login : "+getUserLogin(this));
                startActivity(intent)
                finish()
            }
        }, 500)


    }
}