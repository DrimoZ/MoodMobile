package com.groupe5.moodmobile.repositories

import com.groupe5.moodmobile.dtos.Friend.DtoInputFriendsResponse
import com.groupe5.moodmobile.dtos.Publication.DtoInputPublicationsResponse
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserAccount
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserProfile
import com.groupe5.moodmobile.dtos.Users.Output.DtoOutputUserAccount
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface IUserRepository {
    @GET("/api/v1/user/{userLogin}")
    fun getUserProfile(@Path("userLogin") userLogin: String): Call<DtoInputUserProfile>
    @GET("/api/v1/user/{userLogin}/account")
    fun getUserAccount(@Path("userLogin") userLogin: String): Call<DtoInputUserAccount>
    @PUT("/api/v1/user")
    fun setUserAccount(@Body userAccount: DtoOutputUserAccount): Call<Void>
    @GET("/api/v1/user")
    fun getUserIdAndRole(): Call<DtoInputUserIdAndRole>
    @GET("/api/v1/user/{userLogin}/publications")
    fun getUserPublications(@Path("userLogin") userLogin: String): Call<DtoInputPublicationsResponse>
    @GET("/api/v1/user/{userLogin}/friends")
    fun getUserFriends(@Path("userLogin") userLogin: String): Call<DtoInputFriendsResponse>
}

