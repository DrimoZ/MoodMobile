package com.groupe5.moodmobile.repositories

import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPubComment
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPubLike
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPublicationInformation
import com.groupe5.moodmobile.dtos.Publication.Output.DtoOutputPubComment
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface IPublicationRepository {
    @GET("api/v1/publication/{id}")
    fun getPublicationInformation(@Path("id") id: Int): Call<DtoInputPublicationInformation>
    @GET("api/v1/publication/friends")
    fun getNewsFeedPublicationInformation(@Query("publicationCount") count: Int): Call<List<DtoInputPublicationInformation>>
    @GET("api/v1/publication/{id}/comments")
    fun getPublicationComments(@Path("id") id: Int): Call<List<DtoInputPubComment>>
    @POST("api/v1/publication/like")
    fun setPublicationLike(@Body dto: DtoInputPubLike): Call<Void>
    @POST("api/v1/publication/comment")
    fun setPublicationComment(@Body dto: DtoOutputPubComment): Call<Void>
    @DELETE("api/v1/publication/comment/{id}")
    fun deletePublicationComment(@Path("id") id: Int): Call<Void>
}