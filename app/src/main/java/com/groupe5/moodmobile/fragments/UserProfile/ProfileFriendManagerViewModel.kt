package com.groupe5.moodmobile.fragments.UserProfile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriendsResponse
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.repositories.IFriendRepository
import com.groupe5.moodmobile.repositories.IUserRepository
import com.groupe5.moodmobile.utils.RetrofitFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFriendManagerViewModel(private val jwtToken: String) : ViewModel() {
    val mutableFriendLiveData: MutableLiveData<List<DtoInputFriend>> = MutableLiveData()
    val mutableFriendDeleteData: MutableLiveData<DtoInputFriend> = MutableLiveData()
    val mutableFriendAcceptData: MutableLiveData<DtoInputFriend> = MutableLiveData()
    val mutableFriendRefreshData: MutableLiveData<Void> = MutableLiveData()
    private val userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)
    private val friendRepository = RetrofitFactory.create(jwtToken, IFriendRepository::class.java)

    fun startGetAllFriends(friendId: String? = null) {
        viewModelScope.launch {
            try {
                val call1 = userRepository.getUserIdAndRole()
                call1.enqueue(object : Callback<DtoInputUserIdAndRole> {
                    override fun onResponse(call: Call<DtoInputUserIdAndRole>, response: Response<DtoInputUserIdAndRole>) {
                        if (response.isSuccessful) {
                            val userId = friendId ?: response.body()?.userId
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

    fun deleteFriend(friend: DtoInputFriend) {
        val friendId = friend.id
        Log.d("friendid",friendId)
        viewModelScope.launch {
            val deleteCall = friendRepository.deleteFriend(friendId)
            deleteCall.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("FriendDeletion", "Friend deleted successfully")
                        mutableFriendDeleteData.postValue(friend)
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
        val friendId = friend.id
        Log.d("friendid",friendId)
        viewModelScope.launch {
            val addCall = friendRepository.createFriendRequest(friendId)
            addCall.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("FriendRequestSent", "Friend request sent successfully")
                        mutableFriendRefreshData.postValue(null)
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
    }

    fun cancelFriendRequest(friend: DtoInputFriend) {
        val friendId = friend.id
        Log.d("friendid",friendId)
        viewModelScope.launch {
            val cancelCall = friendRepository.rejectFriendRequest(friendId)
            cancelCall.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("FriendRequestCanceled", "Friend request canceled successfully")
                        mutableFriendRefreshData.postValue(null)
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
        val friendId = friend.id
        Log.d("friendid",friendId)
        viewModelScope.launch {
            val acceptCall = friendRepository.acceptFriendRequest(friendId)
            acceptCall.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("FriendRequestAccepted", "Friend request accepted successfully")
                        mutableFriendAcceptData.postValue(friend)
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
        val friendId = friend.id
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
