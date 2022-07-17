package com.siltech.cryptochat.call.callBanner

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.widget.Chronometer
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.siltech.cryptochat.R
import com.siltech.cryptochat.callUtils.HungUpBroadCastReceiver

class OnGoingCallBanner : Service() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val customView = RemoteViews(packageName, R.layout.ongoing_call_notification)

        val hungUpIntent = Intent(this, HungUpBroadCastReceiver::class.java).apply {
            putExtra("callerUserId", intent?.getStringExtra("callerUserId"))
            putExtra("receiverUserId", intent?.getStringExtra("receiverUserId"))
        }


        val hungUpPendingIntent: PendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(this, 0, hungUpIntent, PendingIntent.FLAG_IMMUTABLE)
            } else {
                PendingIntent.getBroadcast(this, 0, hungUpIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            }

        customView.setTextViewText(R.id.callUserName, intent?.getStringExtra("callerName"))
        val myChronometer = Chronometer(this)

        val elapsedMillis: Long = SystemClock.elapsedRealtime() - myChronometer.base

        customView.setChronometer(
            R.id.callTimer,
            (SystemClock.elapsedRealtime() - elapsedMillis),
            null,
            true
        )
        customView.setOnClickPendingIntent(R.id.callDeclineButton, hungUpPendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                "OnGoingCall",
                "OnGoingCall",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager.createNotificationChannel(notificationChannel)
            val notification = NotificationCompat.Builder(this, "OnGoingCall")
            notification.setContentTitle("CryptoChat")
            notification.setTicker("Call_STATUS")
            notification.setContentText("OnGoingCall")
            notification.setSmallIcon(R.drawable.call_icon)
            notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            notification.setCategory(NotificationCompat.CATEGORY_CALL)
            notification.setVibrate(null)
            notification.setOngoing(true)
            notification.priority = NotificationManager.IMPORTANCE_DEFAULT
            notification.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            notification.setCustomContentView(customView)
            notification.setCustomBigContentView(customView)
            startForeground(1124, notification.build())
        } else {
            val notification = NotificationCompat.Builder(this)
            notification.setContentTitle("CryptoChat")
            notification.setTicker("Call_STATUS")
            notification.setContentText("OnGoingCall")
            notification.setSmallIcon(R.drawable.call_icon)
            notification.setLargeIcon(
                BitmapFactory.decodeResource(
                    this.resources,
                    R.drawable.add_user
                )
            )
            notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            notification.setVibrate(null)
            notification.setOngoing(true)
            notification.setCategory(NotificationCompat.CATEGORY_CALL)
            notification.priority = NotificationManager.IMPORTANCE_DEFAULT
            val hangupAction = NotificationCompat.Action.Builder(
                android.R.drawable.sym_action_chat,
                "HANG UP",
                hungUpPendingIntent
            )
                .build()
            notification.addAction(hangupAction)
            startForeground(1124, notification.build())
        }

        return customView.layoutId

    }


    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}