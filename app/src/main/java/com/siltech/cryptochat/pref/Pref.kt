package com.siltech.cryptochat.pref

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.siltech.cryptochat.app.AppModule

object Pref{
    private val spBigPref: SharedPreferences by lazy {
        AppModule.getInstance().getSharedPreferences("com.siltech.cryptochat", Context.MODE_PRIVATE)
    }

    private fun loadInBigPref(key: String, default: String? = null): String? =
        spBigPref.getString(key, default)

    private fun saveInBigPref(key: String, value: String?) {
        val edit = spBigPref.edit()
        if (value != null) {
            edit.putString(key, value)
        } else {
            edit.remove(key)
        }
        edit.apply()
    }

    private val sp: SharedPreferences by lazy {
        AppModule.getInstance().getSharedPreferences("com.siltech.cryptochat", Context.MODE_PRIVATE)
    }

    private fun load(key: String, default: String? = null): String? = sp.getString(key, default)

    private fun save(key: String, value: String?) {
        val edit = sp.edit()
        if (value != null) {
            edit.putString(key, value)
        } else {
            edit.remove(key)
        }
        edit.apply()
    }

    private const val FIRST_START_APP = "api:first_start_app"
    private const val FINGER_PRINT = "api:finger_print"
    private const val LOCALE_PASSWORD = "api:locale_password"


    var fingerPrint: String
        get() {
            return load(FINGER_PRINT) ?: ""
        }
        set(value) {
            save(FINGER_PRINT, value)
        }

    val hasFingerPrint: Boolean
        get() {
            return !TextUtils.isEmpty(fingerPrint)
        }


    var localePassword: String
        get() {
            return load(LOCALE_PASSWORD) ?: ""
        }
        set(value) {
            save(LOCALE_PASSWORD, value)
        }

    var isFirstStartApp: Boolean
        get() {
            return TextUtils.isEmpty(load(FIRST_START_APP))
        }
        set(value) {
            save(FIRST_START_APP, value.toString())
        }

}
