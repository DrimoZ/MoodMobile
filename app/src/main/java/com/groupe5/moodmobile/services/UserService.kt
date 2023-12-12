package com.groupe5.moodmobile.services

import android.util.Log
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserProfile
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface UserService {
    @GET("/api/v1/user/{userLogin}")
    fun getUserProfile(@Path("userLogin") userLogin: String): Call<DtoInputUserProfile>
    @GET("/api/v1/user")
    fun getUserIdAndRole(): Call<DtoInputUserIdAndRole>
}
object ApiClientProfile {
    private const val BASE_URL = "http://10.0.2.2:5555"

    fun create(token: String): UserService {
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
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                val cookieHeader = "MoodSession=$token"
                requestBuilder.header("Cookie", cookieHeader)
                val request = requestBuilder.build()
                Log.d("RequestHeaders", request.headers.toString())
                chain.proceed(request)
            }
            .cookieJar(cookieJar)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(UserService::class.java)
    }
}



