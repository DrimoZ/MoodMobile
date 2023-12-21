package com.groupe5.moodmobile.fragments.UserProfile.UserPublications

import IUserRepository
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPublication
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPublicationsResponse
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.utils.RetrofitFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilePublicationManagerViewModel(private val jwtToken: String) : ViewModel() {
    val mutablePublicationLiveData: MutableLiveData<List<DtoInputPublication>> = MutableLiveData()
    val isPublicationsPublicLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)

    suspend fun startGetAllPublications(friendId: String? = null) {
        try {
        val response = userRepository.getUserIdAndRole()
            val userId = friendId ?: response.userId
            Log.d("userId", userId.toString())
            userId?.let {
                Log.e("", userId)
                getUserPublications(it)
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private suspend fun getUserPublications(userId: String) {
        try {
        val response = userRepository.getUserPublications(userId)
            if (response != null) {
                if (response.isPublicationsPublic) {
                    val publications = response.publications
                    Log.d("Publications", publications.toString())
                    mutablePublicationLiveData.postValue(publications)
                } else {
                    isPublicationsPublicLiveData.postValue(false)
                }
            }
        } catch (t: Throwable) {
            handleNetworkError(t)
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
