package com.groupe5.moodmobile.fragments.More.Notifications

import IUserRepository
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriendRequest
import com.groupe5.moodmobile.repositories.IFriendRepository
import com.groupe5.moodmobile.utils.RetrofitFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendRequestsManagerViewModel(private val jwtToken: String) : ViewModel() {
    val mutableFriendRequestLiveData: MutableLiveData<List<DtoInputFriendRequest>> = MutableLiveData()
    val mutableFriendRequestRefreshData: MutableLiveData<Void> = MutableLiveData()
    private val userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)
    private val friendRepository = RetrofitFactory.create(jwtToken, IFriendRepository::class.java)

    suspend fun startGetAllUsers() {
        try {
            val response = userRepository.getUserNotifications()
            if (response != null) {
                mutableFriendRequestLiveData.postValue(response)
            } else {
                Log.e("","error")
            }
        } catch (t: Throwable) {
            handleNetworkError(t)
        }
    }
    fun acceptFriendRequest(friend: DtoInputFriendRequest) {
        val friendId = friend.userId
        Log.d("friendid",friendId)
        viewModelScope.launch {
            val acceptCall = friendRepository.acceptFriendRequest(friendId)
            acceptCall.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("FriendRequestAccepted", "Friend request accepted successfully")
                        mutableFriendRequestRefreshData.postValue(null)
                    } else if (response.code() == 404) {
                        Log.d("FriendRequestNotCanceled", "Friend not found")
                    }else {
                        handleApiError(response)
                        val message = "erreur : ${response.message()}"
                        Log.d("responseNotSucc",message)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    handleNetworkError(t)
                    Log.d("Failure","Failure")
                }
            })
        }
    }

    fun rejectFriendRequest(friend: DtoInputFriendRequest) {
        val friendId = friend.userId
        Log.d("friendid",friendId)
        viewModelScope.launch {
            val rejectCall = friendRepository.rejectFriendRequest(friendId)
            rejectCall.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("FriendRequestRejected", "Friend request rejected successfully")
                    } else if (response.code() == 404) {
                        Log.d("FriendRequestNotCanceled", "Friend not found")
                    }else {
                        handleApiError(response)
                        val message = "erreur : ${response.message()}"
                        Log.d("responseNotSucc",message)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    handleNetworkError(t)
                    Log.d("Failure","Failure")
                }
            })
        }
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
