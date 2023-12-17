package com.groupe5.moodmobile.repositories

import com.groupe5.moodmobile.dtos.Image.DtoInputImage
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface IImageRepository {
    @GET("api/v1/image/{id}")
    fun getImageData(@Path("id") id: Int): Call<DtoInputImage>
    @Multipart
    @POST("api/v1/image/userProfile")
    fun setUserProfileImage(@Part image: MultipartBody.Part): Call<Void>
}