package com.siltech.cryptochat.addUser

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.siltech.cryptochat.R

class addUserActivity : AppCompatActivity() {

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)
    }
}
