package com.siltech.cryptochat.data.modules

import com.siltech.cryptochat.network.Api
import org.koin.dsl.module
import retrofit2.Retrofit

val networkModule = module {
    val retrofit: Retrofit = getRetrofit()
    val API_SMS: Api = retrofit.create(Api::class.java)
    single { API_SMS }
}
