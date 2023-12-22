package com.groupe5.moodmobile.repositories

import com.groupe5.moodmobile.dtos.Users.Output.DtoOutputUserSignin
import com.groupe5.moodmobile.dtos.Users.Output.DtoOutputUserSignup
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IAuthenticationRepository {
    @POST("/api/v1/auth/signIn")
    suspend fun signInUser(@Body dto: DtoOutputUserSignin): Response<Void>

    @POST("/api/v1/auth/signUp")
    suspend fun signUpUser(@Body dto: DtoOutputUserSignup): Response<Void>
}