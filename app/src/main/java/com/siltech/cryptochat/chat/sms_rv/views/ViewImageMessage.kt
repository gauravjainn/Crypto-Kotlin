package com.siltech.cryptochat.chat.sms_rv.views

class ViewImageMessage(override val id: String, override var sms: String) : MessageInt{

    override fun getTypeView(): Int {
        return MessageInt.MESSAGE_IMAGE
    }
}