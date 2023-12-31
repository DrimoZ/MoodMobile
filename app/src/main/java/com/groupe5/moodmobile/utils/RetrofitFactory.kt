package com.groupe5.moodmobile.utils

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {
    private val BASE_URL:String= "http://10.0.2.2:5555"

    fun <T> create(token: String, repositoryClass: Class<T>): T {

        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()

        val retrofit : Retrofit

        if(token != null){
            val okHttpClient = OkHttpClientFactory.create(token)
            retrofit = Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        }else{
            retrofit = Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

        return retrofit.create(repositoryClass)
    }
}