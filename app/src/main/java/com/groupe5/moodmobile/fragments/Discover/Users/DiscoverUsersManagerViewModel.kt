package com.groupe5.moodmobile.fragments.Discover.Users

import IUserRepository
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.repositories.IFriendRepository
import com.groupe5.moodmobile.utils.RetrofitFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class DiscoverUsersManagerViewModel(private val jwtToken: String, private val searchValue: String) : ViewModel() {
    val mutableUserLiveData: MutableLiveData<List<DtoInputFriend>> = MutableLiveData()
    val mutableUserRefreshData: MutableLiveData<Void> = MutableLiveData()
    val mutableCount: MutableLiveData<Int> = MutableLiveData()
    private val userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)
    private val friendRepository = RetrofitFactory.create(jwtToken, IFriendRepository::class.java)
    var showCount = 10
    private var searchBarValue = searchValue

    suspend fun startGetAllUsers() {
        try {
            val response = userRepository.getDiscoverUsers(showCount, searchBarValue)
                val num = response.size
                if (num != null) {
                    mutableCount.postValue(num)
                    val slicedUsers = response.slice(0 until num)
                    mutableUserLiveData.postValue(null)
                    mutableUserLiveData.postValue(slicedUsers)
                }
        } catch (e: Exception) {
            handleException(e)
        }
    }


    fun deleteFriend(friend: DtoInputFriend) {
        val friendId = friend.userId
        Log.d("friendid",friendId)
        viewModelScope.launch {
            val deleteCall = friendRepository.deleteFriend(friendId)
            deleteCall.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("FriendDeletion", "Friend deleted successfully")
                        mutableUserRefreshData.postValue(null)
                    } else if (response.code() == 404) {
                        Log.d("FriendDeletion", "Friend not found")
                    }else {
                        handleApiError(response)
                        Log.d("responseNotSucc","responseNotSucc")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    handleNetworkError(t)
                    Log.d("Failure","Failure")
                }
            })
        }
    }

    fun addFriend(friend: DtoInputFriend) {
        val friendId = friend.userId
        Log.d("friendid", friendId)
        val addCall = friendRepository.createFriendRequest(friendId)
        addCall.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    friendRepository.createFriendRequest(friendId)
                    Log.d("FriendRequestSent", "Friend request sent successfully")
                    mutableUserRefreshData.postValue(null)
                } else if (response.code() == 404) {
                    Log.d("FriendRequestNotSent", "Friend not found")
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

    fun cancelFriendRequest(friend: DtoInputFriend) {
        val friendId = friend.userId
        Log.d("friendid",friendId)
        viewModelScope.launch {
            val cancelCall = friendRepository.rejectFriendRequest(friendId)
            cancelCall.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("FriendRequestCanceled", "Friend request canceled successfully")
                        mutableUserRefreshData.postValue(null)
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

    fun acceptFriendRequest(friend: DtoInputFriend) {
        val friendId = friend.userId
        Log.d("friendid",friendId)
        viewModelScope.launch {
            val acceptCall = friendRepository.acceptFriendRequest(friendId)
            acceptCall.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("FriendRequestAccepted", "Friend request accepted successfully")
                        mutableUserRefreshData.postValue(null)
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

    fun rejectFriendRequest(friend: DtoInputFriend) {
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
