package com.siltech.cryptochat.utils.RetrofitIntances

import com.siltech.cryptochat.data.modules.okHttpClients
import com.siltech.cryptochat.network.Api
import com.siltech.cryptochat.utils.baseUrlDev
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitGetPeerInstance {
    companion object {
        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(logging).build()
            Retrofit.Builder().baseUrl(baseUrlDev)
                .addConverterFactory(GsonConverterFactory.create()).client(okHttpClients.build())
                .build()
        }

        val api by lazy {
            retrofit.create(Api::class.java)
        }
    }
}