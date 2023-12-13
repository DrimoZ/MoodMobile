package com.groupe5.moodmobile.repositories

import com.groupe5.moodmobile.dtos.Publication.DtoInputPublicationsResponse
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserProfile
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface IUserRepository {
    @GET("/api/v1/user/{userLogin}")
    fun getUserProfile(@Path("userLogin") userLogin: String): Call<DtoInputUserProfile>
    @GET("/api/v1/user")
    fun getUserIdAndRole(): Call<DtoInputUserIdAndRole>
    @GET("/api/v1/user/{userLogin}/publications")
    fun getUserPublications(@Path("userLogin") userLogin: String): Call<DtoInputPublicationsResponse>
}

