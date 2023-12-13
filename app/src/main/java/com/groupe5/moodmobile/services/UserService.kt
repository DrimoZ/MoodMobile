package com.groupe5.moodmobile.services

import android.util.Log
import com.google.gson.GsonBuilder
import com.groupe5.moodmobile.dtos.Publication.DtoInputPublication
import com.groupe5.moodmobile.dtos.Publication.DtoInputPublicationsResponse
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserProfile
import com.groupe5.moodmobile.utils.OkHttpClientFactory
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
    @GET("/api/v1/user/{userLogin}/publications")
    fun getUserPublications(@Path("userLogin") userLogin: String): Call<DtoInputPublicationsResponse>
}
object ApiClientProfile {
    private const val BASE_URL = "http://10.0.2.2:5555"

    fun create(token: String): UserService {

        val okHttpClient = OkHttpClientFactory.create(token)

        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(UserService::class.java)
    }
}



