package com.siltech.cryptochat.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.persistableBundleOf
import java.util.*
import kotlin.collections.ArrayList
import androidx.core.content.ContextCompat.getSystemService

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.ActivityManager.RunningTaskInfo
import android.content.ComponentName
import android.content.Context.ACTIVITY_SERVICE
import android.os.Build
import com.siltech.cryptochat.app.AppModule


const val BASE_URL = "https://fcm.googleapis.com"
const val CONTENT_TYPE = "application/json"
const val SERVER_KEY = "AAAA0zcSZjI:APA91bG5IWCl5OvBzWPNtAE37Jk0Z-tO8H32glz2eZdzAdMprJ26xAD-5Ty2uQ5Yp78Ms_xopq9fcQJk6l4hAHQswikyZTK0Sq1nGBTxaWzzZFIlcn9l7iyGoKSVg1etv9Yv2iE0rAdh"
const val TOPIC = "/topics/myTopic2"
const val baseUrlDev = "http://194.67.110.76:3000/"

fun getUniqueId(): String = UUID.randomUUID().toString()

fun isCallRequiredPermissionsGiven(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.MODIFY_AUDIO_SETTINGS
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
}

fun checkCallRequiredPermissions(context: Context): ArrayList<String> {
    val requiredPermissions  = ArrayList<String>()

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        requiredPermissions.add(Manifest.permission.CAMERA)
    }
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
        requiredPermissions.add(Manifest.permission.RECORD_AUDIO)
    }
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
        requiredPermissions.add(Manifest.permission.MODIFY_AUDIO_SETTINGS)
    }
    return requiredPermissions
}


fun isAppIsInBackground(context: Context): Boolean {
    var isInBackground = true
    val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
        val runningProcesses = am.runningAppProcesses
        for (processInfo in runningProcesses) {
            if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (activeProcess in processInfo.pkgList) {
                    if (activeProcess == context.packageName) {
                        isInBackground = false
                    }
                }
            }
        }
    } else {
        val taskInfo = am.getRunningTasks(1)
        val componentInfo = taskInfo[0].topActivity
        if (componentInfo!!.packageName == context.packageName) {
            isInBackground = false
        }
    }
    return isInBackground
}

fun connectWithSocket(context: Context) {
    val session = SessionManager(context)
    val publicKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiMDQzZDU4NjU3NDE2MzNiNyJ9.eQBCfKO38fAT2oZ9m5L6Ty2UtBshSu-ggWCoqCopBKg"
    val socketUrl = "https://cryptochatapi.herokuapp.com"
    SocketManager.instance!!.connectSocket(
        session.userLoggedInID.toString(),
        publicKey,
        socketUrl
    )
}