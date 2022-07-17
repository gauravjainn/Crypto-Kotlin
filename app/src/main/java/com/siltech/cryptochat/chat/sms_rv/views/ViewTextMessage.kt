package com.siltech.cryptochat.chat.sms_rv.views

class ViewTextMessage(override val id: String, override var sms: String) : MessageInt{

    override fun getTypeView(): Int {
        return MessageInt.MESSAGE_TEXT
    }
}