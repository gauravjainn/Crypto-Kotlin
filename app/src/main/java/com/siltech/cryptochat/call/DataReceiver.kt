package com.siltech.cryptochat.call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.siltech.cryptochat.call.CallActivity
import com.siltech.cryptochat.call.Call_finder
import com.siltech.cryptochat.call.util.Const

/**
 * Created by Evgeny Eliseyev on 21/02/2018.
 * Receives statuses about registration and send them to activity.
 * Sends statuses about call between service and activity for both sides.
 */

class DataReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) {
            return
        }

        val hasErrorCode = intent.extras?.containsKey(Const.KEY_ERROR_CODE) ?: false
        val hasStatus = intent.extras?.containsKey(Const.KEY_STATUS) ?: false
        val isRegistration = hasStatus && hasErrorCode
        val isCall = hasStatus && !hasErrorCode

        if (context is Call_finder && isRegistration) {
            val status = intent.getSerializableExtra(Const.KEY_STATUS) as Const.SipRegistration
            val errorCode = intent.getIntExtra(Const.KEY_ERROR_CODE, 0)
            context.receiveRegisterState(status, errorCode)
        } else if (isCall) {
            val status = intent.getSerializableExtra(Const.KEY_STATUS) as Const.SipCall

            if (context is CallActivity) {
                context.updateCallStatus(status)
            } else if (context is CallService) {
                context.doAction(status)
            }
        }
    }
}