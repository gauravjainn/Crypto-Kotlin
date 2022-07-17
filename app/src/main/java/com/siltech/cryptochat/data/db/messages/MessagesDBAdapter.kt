package com.siltech.cryptochat.data.db.messages

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
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.ChatWithSMSDatas
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.SmsDatas
import com.siltech.cryptochat.databinding.RvItemsMessagesBinding
import com.siltech.cryptochat.getUserLogin
import com.siltech.cryptochat.model.GetMessagesResponseItem


class MessagesDBAdapter(
    private val encrypt: (String) -> Unit,
    private val onClick: (String) -> Unit,
    private val onImageClick: (String) -> Unit,
    private val onFileClick: (String, String) -> Unit

) : ListAdapter<SmsDatas, MessagesDBAdapter.ItemViewholder>(DiffCallbacks()) {
    var decrypt: String? = null
    var fileName = ""
    private var audio = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        val binding =
            RvItemsMessagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewholder(binding)
    }

    override fun onBindViewHolder(holder: MessagesDBAdapter.ItemViewholder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class ItemViewholder(val binding: RvItemsMessagesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SmsDatas) = with(itemView) {


            if (item.sms != null) {
                encrypt(item.sms)
                Log.d("SMSISHERE",item.sms)

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
                        var date = item.date.substring(0, indexOfPoint)
                        var time = item.date.substring(indexOfPoint + 1, item.date.length - 7)
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
                        var date = item.date.substring(0, indexOfPoint)
                        var time = item.date.substring(indexOfPoint + 1, item.date.length - 7)
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
                        var date = item.date.substring(0, indexOfPoint)
                        var time = item.date.substring(indexOfPoint + 1, item.date.length - 7)
                        var allTime = date + " " + time
                        binding.timeOfSmsImage.text = allTime

                        binding.clCurrentUserImage.setOnClickListener {
                            encrypt(item.sms)
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
                        var date = item.date.substring(0, indexOfPoint)
                        var time = item.date.substring(indexOfPoint + 1, item.date.length - 7)
                        var allTime = date + " " + time
                        binding.timeOfSmsTextImage2.text = allTime

                        Glide.with(binding.clSendUserAudio.context).load(bitmap)
                            .into(binding.imageSendUserSms)

                        binding.clSendUserImage.setOnClickListener {
                            encrypt(item.sms)
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
                        var date = item.date.substring(0, indexOfPoint)
                        var time = item.date.substring(indexOfPoint + 1, item.date.length - 7)
                        var allTime = date + " " + time
                        binding.timeOfSmsAudio.text = allTime
                        binding.playCurrentUserBtn.setOnClickListener {
                            encrypt(item.sms)
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
                        var date = item.date.substring(0, indexOfPoint)
                        var time = item.date.substring(indexOfPoint + 1, item.date.length - 7)
                        var allTime = date + " " + time
                        binding.timeOfSmsTextAudio2.text = allTime
                        binding.playSendUserBtn.setOnClickListener {
                            encrypt(item.sms)
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

                        var indexOfPoint = item.fileType.indexOf('.')
                        var fileNameFromServer = item.fileType.substring(0, indexOfPoint)
                        binding.fileCurrent.text = fileNameFromServer
                        var indexOfPoint3 = item.date.indexOf('T')
                        var date = item.date.substring(0, indexOfPoint3)
                        var time = item.date.substring(indexOfPoint3 + 1, item.date.length - 7)
                        var allTime = date + " " + time
                        binding.timeOfSmsFile.text = allTime
                        binding.clCurrentUserFile.setOnClickListener {
                            encrypt(item.sms)
                            onFileClick(decrypt.toString(), item.fileType)
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
                        var date = item.date.substring(0, indexOfPoint3)
                        var time = item.date.substring(indexOfPoint3 + 1, item.date.length - 7)
                        var allTime = date + " " + time
                        binding.timeOfSmsFile2.text = allTime

                        var indexOfPoint = item.fileType.indexOf('.')
                        var fileNameFromServer = item.fileType.substring(0, indexOfPoint)
                        binding.file.text = fileNameFromServer

                        binding.clSendUserFile.setOnClickListener {
                            encrypt(item.sms)
                            onFileClick(decrypt.toString(), item.fileType)
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

    fun setAudio(flaBoolean: Boolean) {
        audio = flaBoolean
    }
}

class DiffCallbacks : DiffUtil.ItemCallback<SmsDatas>() {

    override fun areItemsTheSame(
        oldItem: SmsDatas,
        newItem: SmsDatas
    ): Boolean {
        return oldItem.toString() == newItem.toString()
    }

    override fun areContentsTheSame(
        oldItem: SmsDatas,
        newItem: SmsDatas
    ): Boolean {
        return oldItem == newItem
    }
}
