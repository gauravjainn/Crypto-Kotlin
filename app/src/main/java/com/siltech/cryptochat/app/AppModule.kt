package com.siltech.cryptochat.app

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp
import com.siltech.cryptochat.data.modules.dbModule
import com.siltech.cryptochat.data.modules.networkModule
import com.siltech.cryptochat.data.modules.repositoryModule
import com.siltech.cryptochat.data.modules.smsViewModel
import com.siltech.cryptochat.data.modules.*
import com.siltech.cryptochat.network.Api
import com.siltech.cryptochat.utils.SessionManager
import com.siltech.cryptochat.utils.SocketManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory

class AppModule : MultiDexApplication() {

    companion object {
        private var instance: AppModule? = null
        lateinit var sslContext: SSLContext
        lateinit var context: Context
        lateinit var sslSocketFactory: SSLSocketFactory
        fun getInstance(): AppModule = instance!!
    }

    lateinit var api: Api
    lateinit var session: SessionManager
    override fun onCreate() {
        sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        sslSocketFactory = sslContext.socketFactory

        super.onCreate()

        context = this
        connectToSocket()
        startKoin {
            androidLogger()
            androidContext(this@AppModule)
            modules(
                listOf(
                    networkModule,
                    repositoryModule,
                    smsViewModel,
                    dbModule,
                    dbModuleChat
                )
            )
        }

    }

    fun connectToSocket() {
        session = SessionManager(context)
        val publicKey =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiMDQzZDU4NjU3NDE2MzNiNyJ9.eQBCfKO38fAT2oZ9m5L6Ty2UtBshSu-ggWCoqCopBKg"
        val socketUrl = "https://cryptochatapi.herokuapp.com"
        if (!session.userLoggedInID.isNullOrEmpty()) {
            SocketManager.instance!!.connectSocket(
                session.userLoggedInID.toString(),
                publicKey,
                socketUrl
            )
        }
    }
}
