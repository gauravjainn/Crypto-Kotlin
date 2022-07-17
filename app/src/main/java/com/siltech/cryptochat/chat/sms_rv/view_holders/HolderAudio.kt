package com.siltech.cryptochat.chat.sms_rv.view_holders

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.siltech.cryptochat.R

class HolderAudio(view: View): RecyclerView.ViewHolder(view) {
    val image: ImageView = view.findViewById(R.id.play_current_user_btn)
}