package com.groupe5.moodmobile.fragments.Publication.Comments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPubComment
import com.groupe5.moodmobile.repositories.IPublicationRepository
import com.groupe5.moodmobile.utils.RetrofitFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PublicationInformationCommentManagerViewModel(private val jwtToken: String) : ViewModel() {
    val mutableCommentLiveData: MutableLiveData<List<DtoInputPubComment>> = MutableLiveData()
    val mutableCommentDeleteData: MutableLiveData<DtoInputPubComment> = MutableLiveData()
    private val publicationRepository = RetrofitFactory.create(jwtToken, IPublicationRepository::class.java)

    fun startGetAllComment(idPublication: Int) {
        viewModelScope.launch {
            try {
                val Commentcall = publicationRepository.getPublicationComments(idPublication)
                Commentcall.enqueue(object : Callback<List<DtoInputPubComment>> {
                    override fun onResponse(call: Call<List<DtoInputPubComment>>, response: Response<List<DtoInputPubComment>>) {
                        if (response.isSuccessful) {
                            val comments = response.body()
                            if (comments != null) {
                                mutableCommentLiveData.postValue(comments)
                            }
                        } else {
                            handleApiError(response)
                        }
                    }

                    override fun onFailure(call: Call<List<DtoInputPubComment>>, t: Throwable) {
                        handleNetworkError(t)
                    }
                })
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    fun deleteFriend(dto: DtoInputPubComment) {
        val id = dto.id
        viewModelScope.launch {
            val deleteCall = publicationRepository.deletePublicationComment(id)
            deleteCall.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        mutableCommentDeleteData.postValue(dto)
                    } else if (response.code() == 404) {
                        Log.d("CommentDeletion", "Comment not found")
                    } else {
                        handleApiError(response)
                        Log.d("responseNotSucc", "responseNotSucc")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    handleNetworkError(t)
                    Log.d("Failure", "Failure")
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
