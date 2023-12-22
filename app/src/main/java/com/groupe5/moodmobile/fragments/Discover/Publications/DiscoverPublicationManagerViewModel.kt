package com.groupe5.moodmobile.fragments.Discover.Publications

import IUserRepository
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPublication
import com.groupe5.moodmobile.utils.RetrofitFactory
import kotlinx.coroutines.launch

class DiscoverPublicationManagerViewModel(private val jwtToken: String, private val searchValue: String) : ViewModel() {
    val mutablePublicationLiveData: MutableLiveData<List<DtoInputPublication>> = MutableLiveData()
    val mutableCount: MutableLiveData<Int> = MutableLiveData()
    private val userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)
    var showCount = 30
    private var searchBarValue = searchValue

    fun startGetAllPublications() {
        viewModelScope.launch {
            try {
                val response = userRepository.getDiscoverPublications(showCount, searchBarValue)
                val publications = response.body()
                if (publications != null) {
                    val num = publications.size
                    if (num == showCount) {
                        mutableCount.postValue(num)
                        val startIndex = if (num >= 10) {
                            (showCount - 10) % num
                        } else {
                            0
                        }
                        val slicedPublications = publications.slice(startIndex until startIndex + 30)
                        mutablePublicationLiveData.postValue(slicedPublications)
                    } else {
                        mutableCount.postValue(-1)
                        val startIndex = 0
                        val slicedPublications = publications.slice(startIndex until startIndex + num)
                        mutablePublicationLiveData.postValue(slicedPublications)
                    }
                }
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }


    private fun handleApiError(response: retrofit2.Response<*>) {
        val message = "API error: ${response.message()}"
        Log.d("Echec", message)
    }

    private fun handleException(e: Exception) {
        val message = "Exception: ${e.message}"
        Log.d("Echec", message)
    }
}
