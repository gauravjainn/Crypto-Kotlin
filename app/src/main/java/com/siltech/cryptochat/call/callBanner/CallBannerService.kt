package com.siltech.cryptochat.call.callBanner

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.siltech.cryptochat.R
import com.siltech.cryptochat.chat.NewCallActivity
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.util.Log
import com.siltech.cryptochat.callUtils.AcceptBroadCastReceiver
import com.siltech.cryptochat.callUtils.HungUpBroadCastReceiver


class CallBannerService : Service() {


    @SuppressLint("RemoteViewLayout")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val customView = RemoteViews(packageName, R.layout.call_notification)

        val notificationIntent = Intent(this, AnswerCallActivity::class.java).apply {
            putExtra("newCall", intent?.getStringExtra("yes"))
            putExtra("userCallingName", intent?.getStringExtra("userCallingName"))
            putExtra("otherUserName", intent?.getStringExtra("otherUserName"))
            putExtra("callerUserId", intent?.getStringExtra("callerUserId"))
            putExtra("receiverUserId", intent?.getStringExtra("receiverUserId"))
            putExtra("callerSocketId", intent?.getStringExtra("callerSocketId"))
            putExtra("receiverSocketId", intent?.getStringExtra("receiverSocketId"))
            putExtra("connId", intent?.getStringExtra("connId"))
        }

        val hungUpIntent = Intent(this, HungUpBroadCastReceiver::class.java).apply {
            putExtra("callerUserId", intent?.getStringExtra("callerUserId"))
            putExtra("receiverUserId", intent?.getStringExtra("receiverUserId"))
        }

        val answerIntent = Intent(this, AcceptBroadCastReceiver::class.java).apply {
            putExtra("newCall", "yes")
            putExtra("userCallingName", intent?.getStringExtra("userCallingName"))
            putExtra("otherUserName", intent?.getStringExtra("otherUserName"))
            putExtra("callerUserId", intent?.getStringExtra("callerUserId"))
            putExtra("receiverUserId", intent?.getStringExtra("receiverUserId"))
            putExtra("callerSocketId", intent?.getStringExtra("callerSocketId"))
            putExtra("receiverSocketId", intent?.getStringExtra("receiverSocketId"))
            putExtra("connId", intent?.getStringExtra("connId"))

        }

        if (intent?.hasExtra("userCallingName")!!) {
//            answerIntent.putExtra("caller_text", intent.getStringExtra("userCallingName"))
            customView.setTextViewText(R.id.callType, "Incoming Call")
        } else {
            customView.setTextViewText(R.id.callUserName, "CryptoChat")
        }


        val pendingIntents = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val hungUpPendingIntent: PendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(this, 0, hungUpIntent, PendingIntent.FLAG_IMMUTABLE)
            } else {

                PendingIntent.getBroadcast(this, 0, hungUpIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            }


        val answerPendingIntent: PendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(this, 0, answerIntent, PendingIntent.FLAG_IMMUTABLE)
            } else {
                PendingIntent.getBroadcast(this, 0, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            }




        customView.setOnClickPendingIntent(R.id.callDeclineButton, hungUpPendingIntent)
        customView.setOnClickPendingIntent(R.id.callAcceptBtn, answerPendingIntent)
//        customView.setOnClickPendingIntent(R.id.callAcceptBtn, answerPendingIntent)

        val ringSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                "IncomingCall",
                "IncomingCall", NotificationManager.IMPORTANCE_HIGH
            )
            val att = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()

            notificationChannel.setSound(ringSound, att)
            notificationManager.createNotificationChannel(notificationChannel)
            val notification = NotificationCompat.Builder(this, "IncomingCall")
            notification.setContentTitle("CryptoChat")
            notification.setTicker("Call_STATUS")
            notification.setContentText("IncomingCall")
            notification.setSmallIcon(R.drawable.call_icon)
            notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            notification.setCategory(NotificationCompat.CATEGORY_CALL)
            notification.setVibrate(null)
            notification.setOngoing(true)
            notification.setFullScreenIntent(pendingIntents, true)
            notification.priority = NotificationManager.IMPORTANCE_HIGH
            notification.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            notification.setCustomContentView(customView)
            notification.setCustomBigContentView(customView)
            startForeground(1124, notification.build())
        } else {
            val notification = NotificationCompat.Builder(this)
            notification.setContentTitle("CryptoChat")
            notification.setTicker("Call_STATUS")
            notification.setContentText("IncomingCall")
            notification.setSmallIcon(R.drawable.call_icon)
            notification.setLargeIcon(
                BitmapFactory.decodeResource(
                    this.resources,
                    R.drawable.add_user
                )
            )
            notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            notification.setVibrate(null)
            notification.setSound(ringSound)
            notification.setContentIntent(pendingIntents)
            notification.setOngoing(true)
            notification.setCategory(NotificationCompat.CATEGORY_CALL)
            notification.priority = NotificationManager.IMPORTANCE_HIGH
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

    override fun onDestroy() {
        super.onDestroy()

    }
}