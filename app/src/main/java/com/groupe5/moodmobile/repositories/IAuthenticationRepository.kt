package com.groupe5.moodmobile.repositories

import com.groupe5.moodmobile.dtos.Users.Output.DtoOutputUserSignin
import com.groupe5.moodmobile.dtos.Users.Output.DtoOutputUserSignup
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface IAuthenticationRepository {
    @POST("/api/v1/auth/signIn")
    fun signInUser(@Body dto: DtoOutputUserSignin): Call<Void>
    @POST("/api/v1/auth/signUp")
    fun signUpUser(@Body dto: DtoOutputUserSignup): Call<Void>
}