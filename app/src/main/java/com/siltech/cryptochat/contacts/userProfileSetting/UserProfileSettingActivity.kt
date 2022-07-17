package com.siltech.cryptochat.contacts.userProfileSetting

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.siltech.cryptochat.R
import com.siltech.cryptochat.databinding.ActivityUserProfileSettingBinding
import com.siltech.cryptochat.getUserLogin
import com.siltech.cryptochat.settings.SettingsActivity
import com.siltech.cryptochat.support.SupportActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi

class UserProfileSettingActivity: AppCompatActivity() {


    lateinit var binding: ActivityUserProfileSettingBinding
    private val userLogin: String
        get() = intent.getStringExtra("user_login").toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityUserProfileSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showUserLogin()
        addNewPhoto()
        notification()
        changePIN()
        language()
        nameOfUser()
//        help()

    }

    private fun nameOfUser() {
        val userName = binding.userNameProfileTv
        userName.setOnClickListener {
            Toast.makeText(this, "User name fragment should be added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addNewPhoto() {
        val floatingActionButton = binding.floatingActionButton
        floatingActionButton.setOnClickListener {
            Toast.makeText(this, "Add new photo fragment should be added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun language() {
        val languageTv = binding.languageTv
        languageTv.setOnClickListener {
            Toast.makeText(this, "Language fragment should be added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun notification() {
        val notificationTv = binding.notificationTv
        notificationTv.setOnClickListener {
            Toast.makeText(this, "Notification fragment should be added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun help() {
        val helpTv = binding.helpTv
        helpTv.setOnClickListener{
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
        }

    }

    private fun showUserLogin() {
        val userLogin1 = binding.userNumberProfileTv
        val userLoginFromDb = getUserLogin(this)
        userLogin1.text  = userLoginFromDb
    }

    private fun changePIN() {
        val changePIN = binding.changePinTv
        changePIN.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}