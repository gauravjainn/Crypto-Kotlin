package com.siltech.cryptochat.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(mcxt: Context) {

    companion object {
        val PREF_NAME = " CRYPTOCHAT"
        val PREF_GENERAL = "PREF_GENERAL"
        val KEY_USERLOGINID = "userloginid"
        val KEY_USERLOGINNAME = "userloginname"
        val KEY_LOGINTOKEN = "logintoken"
        val KEY_CALLUSERSID = "calluserid"

    }

    var generalEditor: SharedPreferences.Editor
    var generalPref: SharedPreferences

    private var PRIVATE_MODE = 0

    init {
        generalPref = mcxt.getSharedPreferences(PREF_GENERAL, PRIVATE_MODE)
        generalEditor = generalPref.edit()
    }

    val userLoggedInID: String?
        get() = generalPref.getString(KEY_USERLOGINID,"")

    fun setUserLoginId(loginUserID: String) {
        generalEditor.putString(KEY_USERLOGINID, loginUserID)
        generalEditor.commit()
    }

    val userLoggedInName: String?
        get() = generalPref.getString(KEY_USERLOGINNAME,"")

    fun setUserLoginName(loginUserID: String) {
        generalEditor.putString(KEY_USERLOGINNAME, loginUserID)
        generalEditor.commit()
    }

    fun setUserLoginToken(loginToken: String) {
        generalEditor.putString(KEY_LOGINTOKEN, loginToken)
        generalEditor.commit()
    }

    val getUserLogingToken: String?
        get() = generalPref.getString(KEY_LOGINTOKEN,"")



    var callUserIds: String?
        get() = generalPref.getString(KEY_CALLUSERSID, "")
        set(usersIds) {
            generalEditor.putString(KEY_CALLUSERSID, usersIds!!)
            generalEditor.commit()
        }
}