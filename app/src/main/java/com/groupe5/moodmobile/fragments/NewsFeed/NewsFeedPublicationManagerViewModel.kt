package com.groupe5.moodmobile.fragments.NewsFeed

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPublication
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPublicationInformation
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPublicationsResponse
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.repositories.IPublicationRepository
import com.groupe5.moodmobile.repositories.IUserRepository
import com.groupe5.moodmobile.utils.RetrofitFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFeedPublicationManagerViewModel(private val jwtToken: String) : ViewModel() {
    val mutablePublicationLiveData: MutableLiveData<List<DtoInputPublicationInformation>> = MutableLiveData()
    private val publicationRepository = RetrofitFactory.create(jwtToken, IPublicationRepository::class.java)
    val publicationCount = 30

    fun startGetAllPublications() {
        viewModelScope.launch {
            try {
                val newsFeedPublicationCall = publicationRepository.getNewsFeedPublicationInformation(publicationCount)
                newsFeedPublicationCall.enqueue(object : Callback<List<DtoInputPublicationInformation>> {
                    override fun onResponse(call: Call<List<DtoInputPublicationInformation>>, response: Response<List<DtoInputPublicationInformation>>) {
                        if (response.isSuccessful) {
                            val publicationList = response.body()
                            mutablePublicationLiveData.postValue(publicationList)
                        } else {
                            handleApiError(response)
                        }
                    }
                    override fun onFailure(call: Call<List<DtoInputPublicationInformation>>, t: Throwable) {
                        handleNetworkError(t)
                    }
                })
            } catch (e: Exception) {
                handleException(e)
            }
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
