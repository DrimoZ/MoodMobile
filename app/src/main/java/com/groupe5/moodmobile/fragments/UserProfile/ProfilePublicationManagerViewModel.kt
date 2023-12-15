package com.groupe5.moodmobile.fragments.UserProfile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupe5.moodmobile.dtos.Publication.DtoInputPublication
import com.groupe5.moodmobile.dtos.Publication.DtoInputPublicationsResponse
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.repositories.IUserRepository
import com.groupe5.moodmobile.utils.RetrofitFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilePublicationManagerViewModel(private val jwtToken: String) : ViewModel() {
    val mutablePublicationLiveData: MutableLiveData<List<DtoInputPublication>> = MutableLiveData()
    private val userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)

    fun startGetAllPublications(friendId: String? = null) {
        viewModelScope.launch {
            try {
                // Step 1: Get user ID and role
                val call1 = userRepository.getUserIdAndRole()
                call1.enqueue(object : Callback<DtoInputUserIdAndRole> {
                    override fun onResponse(call: Call<DtoInputUserIdAndRole>, response: Response<DtoInputUserIdAndRole>) {
                        if (response.isSuccessful) {
                            Log.e("friendID", "firend id : " + friendId)
                            val userId = friendId ?: response.body()?.userId
                            Log.d("userId", userId.toString())
                            userId?.let {
                                // Step 2: Use the ID/Login to call the API to get the user's publications
                                getUserPublications(it)
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

    private fun getUserPublications(userId: String) {
        val call2 = userRepository.getUserPublications(userId)
        call2.enqueue(object : Callback<DtoInputPublicationsResponse> {
            override fun onResponse(call: Call<DtoInputPublicationsResponse>, response: Response<DtoInputPublicationsResponse>) {
                if (response.isSuccessful) {
                    val publicationsResponse = response.body()
                    val publications = publicationsResponse?.publications
                    Log.d("Publications", publications.toString())
                    mutablePublicationLiveData.postValue(publications)
                } else {
                    handleApiError(response)
                }
            }

            override fun onFailure(call: Call<DtoInputPublicationsResponse>, t: Throwable) {
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
