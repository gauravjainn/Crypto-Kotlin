package com.siltech.cryptochat.callUtils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.siltech.cryptochat.R
import com.siltech.cryptochat.chat.NewCallActivity
import com.siltech.cryptochat.contacts.HomeActivityKotlin
import com.siltech.cryptochat.webRtcNative.WebRtcCallActivity


class CallNotification(context: Context): Dialog(context) {

    var signalConnectionId=""
    var creatorId=""
    var currentUserId=""
    var callerSocketId=""
    var receiverSocketId=""
    var callerUserId=""
    var receiverUserId=""
    var callerName:TextView

        init {
            window?.setGravity(Gravity.CENTER)
            window?.setBackgroundDrawableResource(R.drawable.white_corner_round)
            setTitle(null)
            setCancelable(false)
            setOnCancelListener(null)

            val v: View = LayoutInflater.from(context).inflate(R.layout.user_calling_dialogue, null)
            Dialog(context,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
            setContentView(v)
            callerName = v.findViewById(R.id.userName)


            v.findViewById<ImageView>(R.id.acceptCall).setOnClickListener {
                if((context as HomeActivityKotlin).ringtone.isPlaying){
                    context.ringtone.stop()
                }
                context.startActivity(Intent(context, WebRtcCallActivity::class.java).apply {
                    putExtra("newCall",signalConnectionId)
                    putExtra("callerSocketId",callerSocketId)
                    putExtra("receiverSocketId",receiverSocketId)
                    putExtra("callerUserId",callerUserId)
                    putExtra("receiverUserId",receiverUserId)

                })

                dismiss()
            }

            v.findViewById<ImageView>(R.id.declineCall).setOnClickListener {
                Toast.makeText(context, "Call DisConnected", Toast.LENGTH_SHORT).show()
                (context as HomeActivityKotlin).socketEmitForDisconnect(callerSocketId,receiverSocketId,creatorId)
                dismiss()
            }

        }
}