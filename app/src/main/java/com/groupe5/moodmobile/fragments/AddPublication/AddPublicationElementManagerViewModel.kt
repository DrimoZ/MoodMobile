package com.groupe5.moodmobile.fragments.AddPublication

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Response

class AddPublicationElementManagerViewModel(private val jwtToken: String) : ViewModel() {
    val mutableElementLiveData: MutableLiveData<MutableList<Uri>> = MutableLiveData()
    val mutableElementDeleteData: MutableLiveData<Uri> = MutableLiveData()

    fun startGetAllElement(image: MutableList<Uri>) {
        mutableElementLiveData.postValue(image)
    }
    fun deleteElement(image: Uri) {
        mutableElementDeleteData.postValue(image)
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
