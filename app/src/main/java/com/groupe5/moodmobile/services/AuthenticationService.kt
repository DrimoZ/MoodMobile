package com.groupe5.moodmobile.services

import com.groupe5.moodmobile.dtos.Users.Output.DtoOutputUserSignin
import com.groupe5.moodmobile.dtos.Users.Output.DtoOutputUserSignup
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationService {

    @POST("/api/v1/auth/signIn")
    fun signInUser(@Body dto: DtoOutputUserSignin): Call<Void>
    @POST("/api/v1/auth/signUp")
    fun signUpUser(@Body dto: DtoOutputUserSignup): Call<Void>
}

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:5555"

    fun create(): AuthenticationService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(AuthenticationService::class.java)
    }
}
