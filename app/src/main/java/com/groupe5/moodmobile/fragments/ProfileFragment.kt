package com.groupe5.moodmobile.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.groupe5.moodmobile.databinding.FragmentProfileBinding
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserProfile
import com.groupe5.moodmobile.services.ApiClientProfile
import com.groupe5.moodmobile.services.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var userService: UserService

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
        Log.d("Token", token)

        // Initialize userService with the JWT token
        userService = ApiClientProfile.create(token)

        // Call the API to get the user's ID and role
        val call1 = userService.getUserIdAndRole()
        call1.enqueue(object : Callback<DtoInputUserIdAndRole> {
            override fun onResponse(call: Call<DtoInputUserIdAndRole>, response: Response<DtoInputUserIdAndRole>) {
                if (response.isSuccessful) {
                    Log.d("test2", "test2")
                    val userId = response.body()?.userId
                    Log.d("userId", userId.toString())
                    userId?.let {
                        // Use the ID to call the API to get the user's profile
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
                                // Handle request failure here

                                Log.d("test4", "test4")
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
                // Handle request failure here

                Log.d("test3", "test3")
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
