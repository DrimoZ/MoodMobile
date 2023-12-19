package com.groupe5.moodmobile.fragments.Publication.Element

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPubElement
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPublicationInformation
import retrofit2.Response

class PublicationInformationElementManagerViewModel(private val jwtToken: String) : ViewModel() {
    val mutableElementLiveData: MutableLiveData<List<DtoInputPubElement>> = MutableLiveData()

    fun startGetAllElement(dto: DtoInputPublicationInformation) {
        val elements = dto.elements
        mutableElementLiveData.postValue(elements)
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
