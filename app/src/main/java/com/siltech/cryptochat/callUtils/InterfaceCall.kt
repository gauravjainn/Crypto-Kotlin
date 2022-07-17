package com.siltech.cryptochat.callUtils

import android.webkit.JavascriptInterface
import com.siltech.cryptochat.chat.NewCallActivity

class InterfaceCall(private var callActivity: NewCallActivity) {

    @JavascriptInterface
    fun onPeerConnected() = callActivity.onPeerConnected()

    @JavascriptInterface
    fun onPeerError() = callActivity.onPeerError()

}
