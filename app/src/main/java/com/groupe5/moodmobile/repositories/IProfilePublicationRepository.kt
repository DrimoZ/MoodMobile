package com.groupe5.moodmobile.repositories

import com.groupe5.moodmobile.dtos.Publication.DtoInputPublication
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserProfile
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface IProfilePublicationRepository {
    @GET("/api/v1/user/{userLogin}/publications")
    fun getAll(@Path("userLogin") userLogin: String): Call<DtoInputPublication>
}

