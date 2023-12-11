package com.groupe5.moodmobile

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class DtoOutputUserSignin(val login: String, val password: String, val stayLoggedIn: Boolean)
data class DtoOutputUserSignup(val name: String, val login: String, val mail: String, val birthdate: String, val password: String)

interface AuthenticationService {

    @POST("/api/v1/user/signIn")
    fun signInUser(@Body dto: DtoOutputUserSignin): Call<Void>
    @POST("/api/v1/user/signUp")
    fun signUpUser(@Body dto: DtoOutputUserSignup): Call<Void>

    // Ajoutez les autres methodes : signUpUser,...
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
