package com.groupe5.moodmobile.fragments.UserProfile

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.databinding.FragmentOtherUserProfileBinding
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserProfile
import com.groupe5.moodmobile.repositories.IFriendRepository
import com.groupe5.moodmobile.repositories.IImageRepository
import com.groupe5.moodmobile.repositories.IUserRepository
import com.groupe5.moodmobile.services.ImageService
import com.groupe5.moodmobile.utils.RetrofitFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtherUserProfileFragment(friend: DtoInputFriend) : Fragment() {
    val friend = friend
    private lateinit var binding: FragmentOtherUserProfileBinding
    private lateinit var userRepository: IUserRepository
    private lateinit var friendRepository: IFriendRepository
    private lateinit var imageRepository: IImageRepository
    private lateinit var imageService: ImageService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOtherUserProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startUserData(friend)
        val friendId = friend.id
        val profilePublicationFragment = ProfilePublicationManagerFragment.newInstance(friendId)
        replaceFragment(profilePublicationFragment)

        binding.btnFragmentOtherUserProfilePublications.setOnClickListener {
            val friendId = friend.id
            val profilePublicationFragment = ProfilePublicationManagerFragment.newInstance(friendId)
            replaceFragment(profilePublicationFragment)
        }

        binding.btnFragmentOtherUserProfileFriends.setOnClickListener {
            val friendId = friend.id
            val profileFriendFragment = ProfileFriendManagerFragment.newInstance(friendId)
            replaceFragment(profileFriendFragment)
        }

        binding.btnFragmentOtherUserProfileAddFriend.setOnClickListener {
            friendRepository.createFriendRequest(friendId)
        }

        binding.btnFragmentOtherUserProfileCancelFriendRequest.setOnClickListener {
            friendRepository.rejectFriendRequest(friendId)
        }

        binding.btnFragmentOtherUserProfileAcceptFriendRequest.setOnClickListener {
            friendRepository.acceptFriendRequest(friendId)
        }

        binding.btnFragmentOtherUserProfileRejectFriendRequest.setOnClickListener {
            friendRepository.rejectFriendRequest(friendId)
        }

        binding.btnFragmentOtherUserProfileDeleteFriend.setOnClickListener {
            friendRepository.deleteFriend(friendId)
            val deleteCall = friendRepository.deleteFriend(friendId)
            deleteCall.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("FriendDeletion", "Friend deleted successfully")
                    } else if (response.code() == 404) {
                        Log.d("FriendDeletion", "Friend not found")
                    }else {
                        Log.d("responseNotSucc","responseNotSucc")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("Failure","Failure")
                }
            })
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fcb_otherUserProfileFriendManager_list, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun refreshFragment() {
        val friendId = friend.id
        val profileFriendFragment = ProfileFriendManagerFragment.newInstance(friendId)
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fcb_otherUserProfileFriendManager_list, profileFriendFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    private fun startUserData(friend: DtoInputFriend){
        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val jwtToken = prefs.getString("jwtToken", "") ?: ""
        userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)
        friendRepository = RetrofitFactory.create(jwtToken, IFriendRepository::class.java)
        imageRepository = RetrofitFactory.create(jwtToken, IImageRepository::class.java)
        imageService = ImageService(requireContext(), imageRepository)

        val userId = friend.id
        val call = userRepository.getUserProfile(userId)
        call.enqueue(object : Callback<DtoInputUserProfile> {
            override fun onResponse(call: Call<DtoInputUserProfile>, response: Response<DtoInputUserProfile>) {
                if (response.isSuccessful) {
                    val userProfile = response.body()
                    val imageId = response.body()?.idImage
                    imageId?.let { id ->
                        CoroutineScope(Dispatchers.Main).launch {
                            val image = imageService.getImageById(id)
                            if (image.startsWith("@drawable/")) {
                                val resourceId = resources.getIdentifier(
                                    image.substringAfterLast('/'),
                                    "drawable",
                                    "com.groupe5.moodmobile"
                                )
                                Log.e("ressourceid", ""+resourceId)
                                Picasso.with(binding.ivFragmentOtherUserProfileUserImage.context).load(resourceId).into(binding.ivFragmentOtherUserProfileUserImage)
                            } else {
                                Picasso.with(binding.ivFragmentOtherUserProfileUserImage.context).load(image).into(binding.ivFragmentOtherUserProfileUserImage)
                            }
                        }
                    }
                    // Update TextViews with profile data
                    binding.tvFragmentOtherUserProfileUserUsername.text = userProfile?.name
                    binding.tvFragmentOtherUserProfileUserNbPublications.text = "Publications: ${userProfile?.publicationCount}"
                    binding.tvFragmentOtherUserProfileUserNbFriends.text = "Friends: ${userProfile?.friendCount}"
                    binding.tvFragmentOtherUserProfileUserDescription.text = userProfile?.description
                    if(friend.isFriendWithConnected == 2){
                        binding.btnFragmentOtherUserProfileDeleteFriend.isEnabled = true
                        binding.btnFragmentOtherUserProfileDeleteFriend.visibility = View.VISIBLE
                    }
                    if(friend.isFriendWithConnected == 0){
                        binding.btnFragmentOtherUserProfileCancelFriendRequest.isEnabled = true
                        binding.btnFragmentOtherUserProfileCancelFriendRequest.visibility = View.VISIBLE
                    }
                    if(friend.isFriendWithConnected == 1){
                        binding.btnFragmentOtherUserProfileAcceptFriendRequest.isEnabled = true
                        binding.btnFragmentOtherUserProfileAcceptFriendRequest.visibility = View.VISIBLE
                        binding.btnFragmentOtherUserProfileRejectFriendRequest.isEnabled = true
                        binding.btnFragmentOtherUserProfileRejectFriendRequest.visibility = View.VISIBLE
                    }
                    if(friend.isFriendWithConnected == -1){
                        binding.btnFragmentOtherUserProfileAddFriend.isEnabled = true
                        binding.btnFragmentOtherUserProfileAddFriend.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<DtoInputUserProfile>, t: Throwable) {
                val message = "Echec DB: ${t.message}"
                Log.e("EchecDb", message, t)
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(friend: DtoInputFriend) = OtherUserProfileFragment(friend)
    }
}