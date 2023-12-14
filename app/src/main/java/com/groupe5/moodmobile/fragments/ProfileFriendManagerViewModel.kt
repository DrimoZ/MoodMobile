package com.groupe5.moodmobile.fragments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriendsResponse
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.repositories.IUserRepository
import com.groupe5.moodmobile.utils.RetrofitFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFriendManagerViewModel(private val jwtToken: String) : ViewModel() {
    val mutableFriendLiveData: MutableLiveData<List<DtoInputFriend>> = MutableLiveData()
    private val userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)

    fun startGetAllFriends() {
        viewModelScope.launch {
            try {
                val call1 = userRepository.getUserIdAndRole()
                call1.enqueue(object : Callback<DtoInputUserIdAndRole> {
                    override fun onResponse(call: Call<DtoInputUserIdAndRole>, response: Response<DtoInputUserIdAndRole>) {
                        if (response.isSuccessful) {
                            val userId = response.body()?.userId
                            Log.d("userId", userId.toString())
                            userId?.let {
                                getUserFriends(it)
                            }
                        } else {
                            handleApiError(response)
                        }
                    }

                    override fun onFailure(call: Call<DtoInputUserIdAndRole>, t: Throwable) {
                        handleNetworkError(t)
                    }
                })
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun getUserFriends(userId: String) {
        val call2 = userRepository.getUserFriends(userId)
        call2.enqueue(object : Callback<DtoInputFriendsResponse> {
            override fun onResponse(call: Call<DtoInputFriendsResponse>, response: Response<DtoInputFriendsResponse>) {
                if (response.isSuccessful) {
                    val friendsResponse = response.body()
                    val friends = friendsResponse?.friends
                    Log.d("Friends", friends.toString())
                    mutableFriendLiveData.postValue(friends)
                } else {
                    handleApiError(response)
                }
            }

            override fun onFailure(call: Call<DtoInputFriendsResponse>, t: Throwable) {
                handleNetworkError(t)
            }
        })
    }

    private fun handleApiError(response: Response<*>) {
        val message = "API error: ${response.message()}"
        Log.d("Echec", message)
    }

    private fun handleNetworkError(t: Throwable) {
        val message = "Network error: ${t.message}"
        Log.d("Echec", message)
    }

    private fun handleException(e: Exception) {
        val message = "Exception: ${e.message}"
        Log.d("Echec", message)
    }
}
