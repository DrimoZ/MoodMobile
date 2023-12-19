package com.groupe5.moodmobile.fragments.Discover.Publications

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPublication
import com.groupe5.moodmobile.repositories.IUserRepository
import com.groupe5.moodmobile.utils.RetrofitFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiscoverPublicationManagerViewModel(private val jwtToken: String, private val searchValue: String) : ViewModel() {
    val mutablePublicationLiveData: MutableLiveData<List<DtoInputPublication>> = MutableLiveData()
    val mutableCount: MutableLiveData<Int> = MutableLiveData()
    private val userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)
    var showCount = 30
    private var searchBarValue = searchValue

    fun startGetAllPublications() {
        viewModelScope.launch {
            try {
                val publicationsCall = userRepository.getDiscoverPublications(showCount, searchBarValue)
                publicationsCall.enqueue(object : Callback<List<DtoInputPublication>> {
                    override fun onResponse(call: Call<List<DtoInputPublication>>, response: Response<List<DtoInputPublication>>) {
                        if (response.isSuccessful) {
                            val num = response.body()?.size
                            if (num != null) {
                                if(num == showCount){
                                    mutableCount.postValue(num)
                                    val startIndex = if (num != null && num >= 10) {
                                        (showCount - 10) % num
                                    } else {
                                        0
                                    }
                                    val slicedPublications = response.body()?.slice(startIndex until startIndex + 30)
                                    mutablePublicationLiveData.postValue(slicedPublications)
                                }else{
                                    mutableCount.postValue(-1)
                                    val startIndex = 0
                                    val slicedPublications = response.body()?.slice(startIndex until startIndex + num)
                                    mutablePublicationLiveData.postValue(slicedPublications)
                                }
                            }
                        } else {
                            handleApiError(response)
                        }
                    }

                    override fun onFailure(call: Call<List<DtoInputPublication>>, t: Throwable) {
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
