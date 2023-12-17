package com.groupe5.moodmobile.repositories

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface IFriendRepository {
    @POST("/api/v1/friend/request/{friendId}")
    fun createFriendRequest(@Path("friendId") friendId: String): Call<Void>
    @POST("/api/v1/friend/request/accept/{friendId}")
    fun acceptFriendRequest(@Path("friendId") friendId: String): Call<Void>
    @POST("/api/v1/friend/request/reject/{friendId}")
    fun rejectFriendRequest(@Path("friendId") friendId: String): Call<Void>
    @DELETE("/api/v1/friend/{friendId}")
    fun deleteFriend(@Path("friendId") friendId: String): Call<Void>
}