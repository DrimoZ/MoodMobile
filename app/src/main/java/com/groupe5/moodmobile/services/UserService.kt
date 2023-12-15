package com.groupe5.moodmobile.services

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriendsResponse
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.fragments.UserProfile.OtherUserProfileFragment
import com.groupe5.moodmobile.fragments.UserProfile.ProfileFragment
import com.groupe5.moodmobile.repositories.IUserRepository
import com.groupe5.moodmobile.utils.RetrofitFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserService(private val context: Context) {
    private lateinit var userRepository: IUserRepository
    lateinit var prefs: SharedPreferences
    suspend fun getUserId(): String = suspendCoroutine{continuation ->
        prefs = context.getSharedPreferences("mood", Context.MODE_PRIVATE)
        val jwtToken = prefs.getString("jwtToken", "") ?: ""
        userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)
        val call1 = userRepository.getUserIdAndRole()
        call1.enqueue(object : Callback<DtoInputUserIdAndRole> {
            override fun onResponse(call: Call<DtoInputUserIdAndRole>, response: Response<DtoInputUserIdAndRole>) {
                if (response.isSuccessful) {
                    val userId = response.body()?.userId
                    userId?.let {
                        continuation.resume(userId)
                    }
                } else {
                    val message = "echec : ${response.message()}"
                    Log.d("Echec", message)
                }
            }

            override fun onFailure(call: Call<DtoInputUserIdAndRole>, t: Throwable) {
                val message = "Echec DB: ${t.message}"
                Log.e("EchecDb", message, t)
            }
        })
    }

    suspend fun getFriendList(): List<String> = suspendCoroutine{continuation ->
        prefs = context.getSharedPreferences("mood", Context.MODE_PRIVATE)
        val jwtToken = prefs.getString("jwtToken", "") ?: ""
        userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)
        CoroutineScope(Dispatchers.Main).launch {
            val call1 = userRepository.getUserFriends(getUserId())
            call1.enqueue(object : Callback<DtoInputFriendsResponse> {
                override fun onResponse(call: Call<DtoInputFriendsResponse>, response: Response<DtoInputFriendsResponse>) {
                    if (response.isSuccessful) {
                        val friendList = response.body()?.friends
                        friendList?.let {
                            val friendIds = friendList.map { friend -> friend.id }
                            continuation.resume(friendIds)
                        }
                    } else {
                        val message = "echec : ${response.message()}"
                        Log.d("Echec", message)
                    }
                }

                override fun onFailure(call: Call<DtoInputFriendsResponse>, t: Throwable) {
                    val message = "Echec DB: ${t.message}"
                    Log.e("EchecDb", message, t)
                }
            })
        }
    }
}