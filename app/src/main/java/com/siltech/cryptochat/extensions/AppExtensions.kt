package com.siltech.cryptochat

import android.content.Context
import com.siltech.cryptochat.data.modules.trustAllCerts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.UnknownHostException
import java.security.SecureRandom
import javax.crypto.SecretKey
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory

class UserManager() {


    companion object {
        const val USER_TOKEN = "token"
        const val USER_Chat_ID = "user_chat_id"
        const val Chat_ID = "chat_id"
        const val USER_ID = "user_id"
        const val USER_LOGIN = "user_login"
        const val SECRET_KEY = "secret_key"
        const val USER_IS_ADMIN = "is_admin"
        const val PIN_CODE = "pin"
        const val CHATADDED = "chatadded"

    }


}

suspend fun internetAvailability(): Boolean {
    return try {
        withContext(Dispatchers.Default) { InetAddress.getByName("mail.ru") }
        true
    } catch (e: UnknownHostException) {
        false
    }
}

fun saveUserToken(context: Context, token: String) {
    val editor = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).edit()
    editor.putString(UserManager.USER_TOKEN, token)
    editor.apply()
}

fun getUserToken(context: Context): String? {
    return context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).getString(UserManager.USER_TOKEN, null)
}

fun saveUserID(context: Context, token: Int) {
    val editor = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).edit()
    editor.putInt(UserManager.USER_ID, token)
    editor.apply()
}

fun getUserID(context: Context): Int? {
    return context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).getInt(UserManager.USER_ID, 0)
}

fun saveUserLOGIN(context: Context, token: String) {
    val editor = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).edit()
    editor.putString(UserManager.USER_LOGIN, token)
    editor.apply()
}

fun getUserLogin(context: Context): String? {
    return context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).getString(UserManager.USER_LOGIN, null)
}


fun saveUserChatId(context: Context, id: Int){
    val editor = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).edit()
    editor.putInt(UserManager.USER_Chat_ID, id)
    editor.apply()
}

fun getUserChatId(context: Context): Int {
    return context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).getInt(UserManager.USER_Chat_ID, 0)
}
fun saveChatId(context: Context, id: Int){
    val editor = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).edit()
    editor.putInt(UserManager.Chat_ID, id)
    editor.apply()
}

fun getChatId(context: Context): Int {
    return context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).getInt(UserManager.Chat_ID, 0)
}

fun saveUserIsAdmin(context: Context, flag: Boolean){
    val editor = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).edit()
    editor.putBoolean(UserManager.USER_IS_ADMIN, flag)
    editor.apply()
}

fun getUserIsAdmin(context: Context): Boolean {
    return context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).getBoolean(UserManager.USER_IS_ADMIN, false)
}

fun saveSecretKey(context: Context, id: String){
    val editor = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).edit()
    editor.putString(UserManager.SECRET_KEY, id.toString())
    editor.apply()
}

fun getSecretKey(context: Context): String? {
    return context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).getString(UserManager.SECRET_KEY, "")
}
fun savePinKey(context: Context, id: String){
    val editor = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).edit()
    editor.putString(UserManager.PIN_CODE, id.toString())
    editor.apply()
}

fun getPinKey(context: Context): String? {
    return context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).getString(UserManager.PIN_CODE, "")
}

fun saveCHATADDED(context: Context, flag: Boolean){
    val editor = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).edit()
    editor.putBoolean(UserManager.CHATADDED, flag)
    editor.apply()
}

fun getCHATADDED(context: Context): Boolean? {
    return context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).getBoolean(UserManager.CHATADDED, false)
}
