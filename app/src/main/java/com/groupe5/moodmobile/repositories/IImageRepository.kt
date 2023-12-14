package com.groupe5.moodmobile.repositories

import com.groupe5.moodmobile.dtos.Image.DtoInputImage
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface IImageRepository {
    @GET("api/v1/image/{id}")
    fun getImageData(@Path("id") id: Int): Call<DtoInputImage>
}