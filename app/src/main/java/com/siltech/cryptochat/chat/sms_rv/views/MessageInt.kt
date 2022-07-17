package com.siltech.cryptochat.chat.sms_rv.views

interface MessageInt {
    val id: String
    var sms : String

    companion object{
        val MESSAGE_TEXT: Int
        get() = 0

        val MESSAGE_IMAGE: Int
        get() = 1

        val MESSAGE_AUDIO: Int
        get() = 2

    }
fun getTypeView(): Int
}