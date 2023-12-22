package com.groupe5.moodmobile.utils

import android.util.Log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object OkHttpClientFactory {
    fun create(token: String): OkHttpClient {
        val cookieJar = object : CookieJar {
            private val cookies = mutableListOf<Cookie>()

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                this.cookies.addAll(cookies)
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return cookies
            }
        }

        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                val cookieHeader = "MoodSession=$token"
                requestBuilder.header("Cookie", cookieHeader)
                val request = requestBuilder.build()
                //Log.d("RequestHeaders", request.headers.toString())
                chain.proceed(request)
            }
            .cookieJar(cookieJar)
            .build()
        return okHttpClient
    }
}