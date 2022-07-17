package com.siltech.cryptochat.chat

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.siltech.cryptochat.R
import com.siltech.cryptochat.databinding.RvItemsMessagesBinding
import com.siltech.cryptochat.getUserLogin
import com.siltech.cryptochat.model.GetMessagesResponseItem
import java.util.*

class ChatMainAdapter(
    private val encrypt: (String) -> Unit,
    private val onClick: (String) -> Unit,
    private val onImageClick: (String) -> Unit,
    private val onFileClick: (String, String) -> Unit

) : ListAdapter<GetMessagesResponseItem, ChatMainAdapter.ItemViewholder>(DiffCallbacks()) {
    var decrypt: String? = null
    var fileName = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        val binding =
            RvItemsMessagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewholder(binding)
    }

    override fun onBindViewHolder(holder: ChatMainAdapter.ItemViewholder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class ItemViewholder(val binding: RvItemsMessagesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GetMessagesResponseItem) = with(itemView) {
//
//            item.id = uniqueID

            if (item.message != null) {
                encrypt(item.message)
            }

            when (item.type) {
                "text" -> {
                    if (item.userLogin == getUserLogin(binding.clCurrentUserAudio.context)) {
                        binding.clCurrentUserText.visibility = View.VISIBLE
                        binding.clSendUserText.visibility = View.GONE

                        binding.clCurrentUserAudio.visibility = View.GONE
                        binding.clSendUserAudio.visibility = View.GONE

                        binding.clCurrentUserImage.visibility = View.GONE
                        binding.clSendUserImage.visibility = View.GONE

                        binding.clCurrentUserFile.visibility = View.GONE
                        binding.clSendUserFile.visibility = View.GONE

                        var indexOfPoint = item.date.indexOf('T')
                        var date = item.date.substring(0,indexOfPoint)
                        var time = item.date.substring(indexOfPoint+1, item.date.length-7)
                        var allTime = date + " " + time
                        binding.timeOfSmsText.text = allTime

                        binding.smsCurrentUserText.text = decrypt

                    } else {
                        binding.clCurrentUserText.visibility = View.GONE
                        binding.clSendUserText.visibility = View.VISIBLE

                        binding.clCurrentUserAudio.visibility = View.GONE
                        binding.clSendUserAudio.visibility = View.GONE

                        binding.clCurrentUserImage.visibility = View.GONE
                        binding.clSendUserImage.visibility = View.GONE

                        binding.clCurrentUserFile.visibility = View.GONE
                        binding.clSendUserFile.visibility = View.GONE

                        var indexOfPoint = item.date.indexOf('T')
                        var date = item.date.substring(0,indexOfPoint)
                        var time = item.date.substring(indexOfPoint+1, item.date.length-7)
                        var allTime = date + " " + time
                        binding.timeOfSmsText2.text = allTime

                        binding.smsSendUserText.text = decrypt

                    }
                }

                "image" -> {
                    if (item.userLogin == getUserLogin(binding.clCurrentUserAudio.context)) {

                        val decodedByte = Base64.decode(decrypt, Base64.DEFAULT)
                        var bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)

                        binding.clCurrentUserText.visibility = View.GONE
                        binding.clSendUserText.visibility = View.GONE

                        binding.clCurrentUserAudio.visibility = View.GONE
                        binding.clSendUserAudio.visibility = View.GONE

                        binding.clCurrentUserImage.visibility = View.VISIBLE
                        binding.clSendUserImage.visibility = View.GONE

                        binding.clCurrentUserFile.visibility = View.GONE
                        binding.clSendUserFile.visibility = View.GONE

                        Glide.with(binding.clSendUserAudio.context).load(bitmap)
                            .into(binding.imageCurrentUserSms)

                        var indexOfPoint = item.date.indexOf('T')
                        var date = item.date.substring(0,indexOfPoint)
                        var time = item.date.substring(indexOfPoint+1, item.date.length-7)
                        var allTime = date + " " + time
                        binding.timeOfSmsImage.text = allTime

                        binding.clCurrentUserImage.setOnClickListener {
                            encrypt(item.message)
                            onImageClick(decrypt!!)
                        }
                    } else {
                        val decodedByte = Base64.decode(decrypt, Base64.DEFAULT)
                        var bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)

                        binding.clCurrentUserText.visibility = View.GONE
                        binding.clSendUserText.visibility = View.GONE

                        binding.clCurrentUserAudio.visibility = View.GONE
                        binding.clSendUserAudio.visibility = View.GONE

                        binding.clCurrentUserImage.visibility = View.GONE
                        binding.clSendUserImage.visibility = View.VISIBLE

                        binding.clCurrentUserFile.visibility = View.GONE
                        binding.clSendUserFile.visibility = View.GONE

                        var indexOfPoint = item.date.indexOf('T')
                        var date = item.date.substring(0,indexOfPoint)
                        var time = item.date.substring(indexOfPoint+1, item.date.length-7)
                        var allTime = date + " " + time
                        binding.timeOfSmsTextImage2.text = allTime

                        Glide.with(binding.clSendUserAudio.context).load(bitmap)
                            .into(binding.imageSendUserSms)
                        binding.clSendUserImage.setOnClickListener {
                            encrypt(item.message)
                            onImageClick(decrypt!!)
                        }
                    }
                }
                "voice" -> {
                    if (item.userLogin == getUserLogin(binding.clCurrentUserAudio.context)) {

                        binding.clCurrentUserText.visibility = View.GONE
                        binding.clSendUserText.visibility = View.GONE

                        binding.clCurrentUserAudio.visibility = View.VISIBLE
                        binding.clSendUserAudio.visibility = View.GONE

                        binding.clCurrentUserImage.visibility = View.GONE
                        binding.clSendUserImage.visibility = View.GONE

                        binding.clCurrentUserFile.visibility = View.GONE
                        binding.clSendUserFile.visibility = View.GONE

                        var indexOfPoint = item.date.indexOf('T')
                        var date = item.date.substring(0,indexOfPoint)
                        var time = item.date.substring(indexOfPoint+1, item.date.length-7)
                        var allTime = date + " " + time
                        binding.timeOfSmsAudio.text = allTime

                        binding.playCurrentUserBtn.setOnClickListener {
                            encrypt(item.message)
                            onClick(decrypt.toString())
                        }

                    } else {

                        binding.clCurrentUserText.visibility = View.GONE
                        binding.clSendUserText.visibility = View.GONE

                        binding.clCurrentUserAudio.visibility = View.GONE
                        binding.clSendUserAudio.visibility = View.VISIBLE

                        binding.clCurrentUserImage.visibility = View.GONE
                        binding.clSendUserImage.visibility = View.GONE

                        binding.clCurrentUserFile.visibility = View.GONE
                        binding.clSendUserFile.visibility = View.GONE
                        var indexOfPoint = item.date.indexOf('T')
                        var date = item.date.substring(0,indexOfPoint)
                        var time = item.date.substring(indexOfPoint+1, item.date.length-7)
                        var allTime = date + " " + time
                        binding.timeOfSmsTextAudio2.text = allTime

                        binding.playSendUserBtn.setOnClickListener {
                            encrypt(item.message)
                            onClick(decrypt.toString())
                        }
                    }
                }
                "file" -> {
                    if (item.userLogin == getUserLogin(binding.clCurrentUserAudio.context)) {

                        binding.clCurrentUserText.visibility = View.GONE
                        binding.clSendUserText.visibility = View.GONE

                        binding.clCurrentUserAudio.visibility = View.GONE
                        binding.clSendUserAudio.visibility = View.GONE

                        binding.clCurrentUserImage.visibility = View.GONE
                        binding.clSendUserImage.visibility = View.GONE

                        binding.clCurrentUserFile.visibility = View.VISIBLE
                        binding.clSendUserFile.visibility = View.GONE

                        var indexOfPoint = item.filetype.indexOf('.')
                        var fileNameFromServer = item.filetype.substring(0,indexOfPoint)
                        binding.fileCurrent.text = fileNameFromServer
                        var indexOfPoint3 = item.date.indexOf('T')
                        var date = item.date.substring(0,indexOfPoint3)
                        var time = item.date.substring(indexOfPoint3+1, item.date.length-7)
                        var allTime = date + " " + time
                        binding.timeOfSmsFile.text = allTime

                        binding.clCurrentUserFile.setOnClickListener {
                            encrypt(item.message)
                            onFileClick(decrypt.toString(), item.filetype)
                        }

                    } else {

                        binding.clCurrentUserText.visibility = View.GONE
                        binding.clSendUserText.visibility = View.GONE

                        binding.clCurrentUserAudio.visibility = View.GONE
                        binding.clSendUserAudio.visibility = View.GONE

                        binding.clCurrentUserImage.visibility = View.GONE
                        binding.clSendUserImage.visibility = View.GONE

                        binding.clCurrentUserFile.visibility = View.GONE
                        binding.clSendUserFile.visibility = View.VISIBLE

                        var indexOfPoint3 = item.date.indexOf('T')
                        var date = item.date.substring(0,indexOfPoint3)
                        var time = item.date.substring(indexOfPoint3+1, item.date.length-7)
                        var allTime = date + " " + time
                        binding.timeOfSmsFile2.text = allTime

                        var indexOfPoint = item.filetype.indexOf('.')
                        var fileNameFromServer = item.filetype.substring(0,indexOfPoint)
                        binding.file.text = fileNameFromServer

                        binding.clSendUserFile.setOnClickListener {
                            encrypt(item.message)
                            onFileClick(decrypt.toString(), item.filetype)
                        }
                    }
                }
            }
            setOnClickListener {
            }
        }
    }

    fun setdecr(string: String) {
        decrypt = string
    }

    fun setFileNameFromServer(string: String) {
        fileName = string
    }
}

class DiffCallbacks : DiffUtil.ItemCallback<GetMessagesResponseItem>() {

    override fun areItemsTheSame(
        oldItem: GetMessagesResponseItem,
        newItem: GetMessagesResponseItem
    ): Boolean {
        return oldItem.toString() == newItem.toString()
    }

    override fun areContentsTheSame(
        oldItem: GetMessagesResponseItem,
        newItem: GetMessagesResponseItem
    ): Boolean {
        return oldItem == newItem
    }
}
