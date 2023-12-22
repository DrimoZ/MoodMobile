package com.groupe5.moodmobile.services

import IUserRepository
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriendsResponse
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserProfile
import com.groupe5.moodmobile.dtos.Users.Output.DtoOutputUserPassword
import com.groupe5.moodmobile.utils.RetrofitFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserService(private val context: Context) {
    private lateinit var userRepository: IUserRepository
    lateinit var prefs: SharedPreferences
    suspend fun getUserId(): String = suspendCoroutine{continuation ->
        prefs = context.getSharedPreferences("mood", Context.MODE_PRIVATE)
        val jwtToken = prefs.getString("jwtToken", "") ?: ""
        userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)
        val call1 = userRepository.getUserIdAndRoleService()
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

    suspend fun getUserProfile(friendId: String): Int = suspendCoroutine {continuation ->
        prefs = context.getSharedPreferences("mood", Context.MODE_PRIVATE)
        val jwtToken = prefs.getString("jwtToken", "") ?: ""
        userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)
        val call1 = userRepository.getUserProfileService(friendId)
        call1.enqueue(object : Callback<DtoInputUserProfile> {
            override fun onResponse(call: Call<DtoInputUserProfile>, response: Response<DtoInputUserProfile>) {
                if (response.isSuccessful) {
                    val isUserFriendWith = response.body()?.isFriendWithConnected
                    isUserFriendWith?.let {
                        continuation.resume(isUserFriendWith)
                    }
                } else {
                    val message = "echec : ${response.message()}"
                    Log.d("Echec", message)
                }
            }

            override fun onFailure(call: Call<DtoInputUserProfile>, t: Throwable) {
                val message = "Echec DB: ${t.message}"
                Log.e("EchecDb", message, t)
            }
        })
    }

    suspend fun getFriendDto(friendId: String): DtoInputFriend = suspendCancellableCoroutine { continuation ->
        prefs = context.getSharedPreferences("mood", Context.MODE_PRIVATE)
        val jwtToken = prefs.getString("jwtToken", "") ?: ""
        userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)

        val call1 = userRepository.getUserProfileService(friendId)
        call1.enqueue(object : Callback<DtoInputUserProfile> {
            override fun onResponse(call: Call<DtoInputUserProfile>, response: Response<DtoInputUserProfile>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        val dto = DtoInputFriend(
                            commonFriendCount = 0,
                            id = friendId,
                            idImage = user.idImage,
                            isFriendWithConnected = user.isFriendWithConnected,
                            login = user.login,
                            name = user.name
                        )
                        continuation.resume(dto)
                    } else {
                        continuation.resumeWithException(NullPointerException("User response body is null"))
                    }
                } else {
                    val message = "echec : ${response.message()}"
                    Log.d("Echec", message)
                    continuation.resumeWithException(Exception(message))
                }
            }

            override fun onFailure(call: Call<DtoInputUserProfile>, t: Throwable) {
                val message = "Echec DB: ${t.message}"
                Log.e("EchecDb", message, t)
                continuation.resumeWithException(Exception(message))
            }
        })
    }
}