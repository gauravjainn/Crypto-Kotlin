package com.siltech.cryptochat.chat.sms_rv.view_holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.siltech.cryptochat.R
import com.siltech.cryptochat.chat.sms_rv.views.MessageInt

class AppHolderFactory {
    companion object {
        fun getHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                MessageInt.MESSAGE_IMAGE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.rv_items_messages_image, parent, false)
                    HolderImage(view)
                }

                MessageInt.MESSAGE_TEXT -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.rv_items_messages_text, parent, false)
                    HolderText(view)
                }
                else ->{
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.rv_items_messages_voice, parent, false)
                    HolderAudio(view)                }
            }
        }
    }
}