package com.siltech.cryptochat.chat.chat_users

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.siltech.cryptochat.databinding.ChatItemBinding
import com.siltech.cryptochat.model.GetChatUsersResponseItemX


class ChatUsersAdapter(private val deleteUser: (GetChatUsersResponseItemX) -> Unit) :
    RecyclerView.Adapter<ChatUsersAdapter.SmsVH>() {
    var list = emptyList<GetChatUsersResponseItemX>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListSMS(list: List<GetChatUsersResponseItemX>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class SmsVH(val binding: ChatItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor")
        fun onBind(a: GetChatUsersResponseItemX) {
            binding.tvMessage.text = a.userLogin.toString()
            itemView.setOnLongClickListener {
                deleteUser(a)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsVH {
        val binding =
            ChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SmsVH(binding)
    }

    override fun onBindViewHolder(holder: SmsVH, position: Int) {
        holder.onBind(list[position])


    }

    override fun getItemCount(): Int {
        return list.size
    }


}