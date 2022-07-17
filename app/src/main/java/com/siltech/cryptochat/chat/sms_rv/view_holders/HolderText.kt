package com.siltech.cryptochat.chat.sms_rv.view_holders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.siltech.cryptochat.R

class HolderText(view: View): RecyclerView.ViewHolder(view) {
    val image: TextView = view.findViewById(R.id.sms_send_user_text)
}