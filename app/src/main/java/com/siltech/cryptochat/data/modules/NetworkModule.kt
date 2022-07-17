package com.siltech.cryptochat.data.modules

import com.siltech.cryptochat.app.AppModule.Companion.context
import com.siltech.cryptochat.app.AppModule.Companion.sslSocketFactory
import com.siltech.cryptochat.getUserToken
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

private val baseUrlDev = "http://194.67.110.76:3000/"

private fun getHttpLogInterceptor() = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val okHttpClients by lazy {
    OkHttpClient.Builder().addInterceptor(getHttpLogInterceptor()).addInterceptor(AuthInterceptor())
        .apply { setTimeOutToOkHttpClient(this).build() }.sslSocketFactory(
            sslSocketFactory,
            trustAllCerts[0] as X509TrustManager
        ).hostnameVerifier { _, _ -> true }
}

private fun getretrofit(): Retrofit =
    Retrofit.Builder()
        .baseUrl(baseUrlDev)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClients.build())
        .build()

val trustAllCerts: Array<TrustManager> = arrayOf(
    object : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?>? {
            return arrayOf()
        }
    }
)

fun getRetrofit() = getretrofit()

private fun setTimeOutToOkHttpClient(okHttpClientBuilder: OkHttpClient.Builder) =
    okHttpClientBuilder.apply {
        readTimeout(30L, TimeUnit.SECONDS)
        connectTimeout(30L, TimeUnit.SECONDS)
        writeTimeout(30L, TimeUnit.SECONDS)
    }
//interceptor to use token

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        getUserToken(context)?.let {
            requestBuilder.addHeader("Content-Type", "application/json")

            requestBuilder.addHeader("Authorization", "Bearer $it")

        }

        return chain.proceed(requestBuilder.build())
    }
}