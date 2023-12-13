package com.groupe5.moodmobile.fragments

import android.R
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.groupe5.moodmobile.activities.SigninActivity
import com.groupe5.moodmobile.databinding.FragmentProfileBinding
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserProfile
import com.groupe5.moodmobile.repositories.IAuthenticationRepository
import com.groupe5.moodmobile.services.ApiClientProfile
import com.groupe5.moodmobile.services.UserService
import com.groupe5.moodmobile.utils.RetrofitFactory
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var userService: UserService
    private val authenticationRepository = RetrofitFactory.instance.create(IAuthenticationRepository::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Get the JWT token from SharedPreferences
        val prefs = requireActivity().getSharedPreferences("mood", MODE_PRIVATE)
        val token = prefs.getString("jwtToken", "") ?: ""
        //Log.d("Token", token)

        // Initialize userService with the JWT token
        userService = ApiClientProfile.create(token)

        // Call the API to get the user's ID and role
        val call1 = userService.getUserIdAndRole()
        call1.enqueue(object : Callback<DtoInputUserIdAndRole> {
            override fun onResponse(call: Call<DtoInputUserIdAndRole>, response: Response<DtoInputUserIdAndRole>) {
                if (response.isSuccessful) {
                    val userId = response.body()?.userId
                    //Log.d("userId", userId.toString())
                    userId?.let {
                        // Use the ID/Login to call the API to get the user's profile
                        val call2 = userService.getUserProfile(it)
                        call2.enqueue(object : Callback<DtoInputUserProfile> {
                            override fun onResponse(call: Call<DtoInputUserProfile>, response: Response<DtoInputUserProfile>) {
                                if (response.isSuccessful) {
                                    val userProfile = response.body()
                                    // Update TextViews with profile data
                                    binding.tvFragmentProfileUserUsername.text = userProfile?.name
                                    binding.tvFragmentProfileUserNbPublications.text = "Publications: ${userProfile?.publicationCount}"
                                    binding.tvFragmentProfileUserNbFriends.text = "Friends: ${userProfile?.friendCount}"
                                    binding.tvFragmentProfileUserDescription.text = userProfile?.description
                                }
                            }

                            override fun onFailure(call: Call<DtoInputUserProfile>, t: Throwable) {
                                val message = "Echec DB: ${t.message}"
                                Log.e("EchecDb", message, t)
                            }
                        })
                    }
                } else {
                    val message = "echec : ${response.message()}"
                    Log.d("Echec", message)
                }
            }

            override fun onFailure(call: Call<DtoInputUserIdAndRole>, t: Throwable) {
                val message = "Echec DB: ${t.message}"
                Log.e("EchecDb", message, t)
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}
