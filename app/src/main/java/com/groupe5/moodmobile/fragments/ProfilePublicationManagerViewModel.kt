package com.groupe5.moodmobile.fragments

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupe5.moodmobile.dtos.Publication.DtoInputPublication
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserProfile
import com.groupe5.moodmobile.repositories.IProfilePublicationRepository
import com.groupe5.moodmobile.services.ApiClientProfile
import com.groupe5.moodmobile.services.UserService
import com.groupe5.moodmobile.utils.RetrofitFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilePublicationManagerViewModel(private val jwtToken: String) : ViewModel() {
    val mutablePublicationLiveData: MutableLiveData<List<DtoInputPublication>> = MutableLiveData()
    private lateinit var userService: UserService

    //private val publicationRepository = RetrofitFactory.instance.create(IProfilePublicationRepository::class.java)
    init {
        userService = ApiClientProfile.create(jwtToken)
    }
    fun startGetAllPublications() {
        viewModelScope.launch {
            val call1 = userService.getUserIdAndRole()
            call1.enqueue(object : Callback<DtoInputUserIdAndRole> {
                override fun onResponse(call: Call<DtoInputUserIdAndRole>, response: Response<DtoInputUserIdAndRole>) {
                    if (response.isSuccessful) {
                        val userId = response.body()?.userId
                        Log.d("userId", userId.toString())
                        userId?.let {
                            // Use the ID/Login to call the API to get the user's publications
                            val call2 = userService.getUserPublications(it)
                            call2.enqueue(object : Callback<List<DtoInputPublication>> {
                                override fun onResponse(call: Call<List<DtoInputPublication>>, response: Response<List<DtoInputPublication>>) {
                                    if (response.isSuccessful) {
                                        Log.d("test", "test")
                                        val publications = response.body()
                                        mutablePublicationLiveData.postValue(publications)
                                    }
                                    else{
                                        val message = "echec : ${response.message()}"
                                        Log.d("Echec", message)
                                    }
                                }
                                override fun onFailure(call: Call<List<DtoInputPublication>>, t: Throwable) {
                                    Log.d("test", "test")
                                    val message = "echec : ${t.message}"
                                    Log.d("Echec", message)
                                }
                            })
                        }
                    } else {
                        val message = "echec : ${response.message()}"
                        Log.d("Echec", message)
                    }
                }

                override fun onFailure(call: Call<DtoInputUserIdAndRole>, t: Throwable) {
                    val message = "echec : ${t.message}"
                    Log.d("Echec", message)
                }
            })
        }
    }
}