package com.groupe5.moodmobile.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.databinding.FragmentProfileBinding
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserProfile
import com.groupe5.moodmobile.repositories.IUserRepository
import com.groupe5.moodmobile.utils.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var userRepository: IUserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val jwtToken = prefs.getString("jwtToken", "") ?: ""
        userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)

        // Call the API to get the user's ID and role
        val call1 = userRepository.getUserIdAndRole()
        call1.enqueue(object : Callback<DtoInputUserIdAndRole> {
            override fun onResponse(call: Call<DtoInputUserIdAndRole>, response: Response<DtoInputUserIdAndRole>) {
                if (response.isSuccessful) {
                    val userId = response.body()?.userId
                    //Log.d("userId", userId.toString())
                    userId?.let {
                        // Use the ID/Login to call the API to get the user's profile
                        val call2 = userRepository.getUserProfile(it)
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

        binding.btnFragmentProfilePublications.setOnClickListener {
            replaceFragment(ProfilePublicationManagerFragment.newInstance())
        }

        binding.btnFragmentProfileFriends.setOnClickListener {
            replaceFragment(ProfileFriendManagerFragment.newInstance())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fcb_profilePublicationManager_list, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}
