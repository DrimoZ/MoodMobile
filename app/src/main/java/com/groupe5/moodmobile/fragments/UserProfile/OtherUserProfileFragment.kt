package com.groupe5.moodmobile.fragments.UserProfile

import IUserRepository
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.activities.MainActivity
import com.groupe5.moodmobile.databinding.FragmentOtherUserProfileBinding
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.fragments.UserProfile.UserFriends.ProfileFriendManagerFragment
import com.groupe5.moodmobile.fragments.UserProfile.UserPublications.ProfilePublicationManagerFragment
import com.groupe5.moodmobile.repositories.IFriendRepository
import com.groupe5.moodmobile.repositories.IImageRepository
import com.groupe5.moodmobile.services.ImageService
import com.groupe5.moodmobile.services.UserService
import com.groupe5.moodmobile.utils.RetrofitFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtherUserProfileFragment(friend: DtoInputFriend) : Fragment() {
    val friend = friend
    private lateinit var binding: FragmentOtherUserProfileBinding
    private lateinit var userRepository: IUserRepository
    private lateinit var userService: UserService
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
        lifecycleScope.launch {
            startUserData(friend)
        }
        val friendId = friend.userId
        val profilePublicationFragment = ProfilePublicationManagerFragment.newInstance(friendId)
        replaceFragment(profilePublicationFragment)

        binding.btnFragmentOtherUserProfilePublications.setOnClickListener {
            val friendId = friend.userId
            val profilePublicationFragment = ProfilePublicationManagerFragment.newInstance(friendId)
            replaceFragment(profilePublicationFragment)
        }

        binding.btnFragmentOtherUserProfileFriends.setOnClickListener {
            val friendId = friend.userId
            val profileFriendFragment = ProfileFriendManagerFragment.newInstance(friendId)
            replaceFragment(profileFriendFragment)
        }

        binding.btnFragmentOtherUserProfileAddFriend.setOnClickListener {
            val addCall = friendRepository.createFriendRequest(friendId)
            addCall.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("FriendRequestSent", "Friend request sent successfully")
                        lifecycleScope.launch {
                            (requireActivity() as MainActivity).onRefreshOtherUser(
                                friendRefresh(
                                    friend
                                )
                            )
                        }
                    } else if (response.code() == 404) {
                        Log.d("FriendRequestNotSent", "Friend not found")
                    }else {
                        val message = "erreur : ${response.message()}"
                        Log.d("responseNotSucc",message)
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("Failure","Failure")
                }
            })
        }

        binding.btnFragmentOtherUserProfileCancelFriendRequest.setOnClickListener {
            val cancelCall = friendRepository.rejectFriendRequest(friendId)
            cancelCall.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("FriendRequestCanceled", "Friend request canceled successfully")
                        lifecycleScope.launch {
                            (requireActivity() as MainActivity).onRefreshOtherUser(
                                friendRefresh(
                                    friend
                                )
                            )
                        }
                    } else if (response.code() == 404) {
                        Log.d("FriendRequestNotCancelled", "Friend not found")
                    }else {
                        val message = "erreur : ${response.message()}"
                        Log.d("responseNotSucc",message)
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("Failure","Failure")
                }
            })
        }

        binding.btnFragmentOtherUserProfileAcceptFriendRequest.setOnClickListener {
            val cancelCall = friendRepository.acceptFriendRequest(friendId)
            cancelCall.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("FriendRequestAccepted", "Friend request accepted successfully")
                        lifecycleScope.launch {
                            (requireActivity() as MainActivity).onRefreshOtherUser(
                                friendRefresh(
                                    friend
                                )
                            )
                        }
                    } else if (response.code() == 404) {
                        Log.d("FriendRequestnotAccepted", "Friend not found")
                    }else {
                        val message = "erreur : ${response.message()}"
                        Log.d("responseNotSucc",message)
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("Failure","Failure")
                }
            })
        }


        binding.btnFragmentOtherUserProfileRejectFriendRequest.setOnClickListener {
            val cancelCall = friendRepository.rejectFriendRequest(friendId)
            cancelCall.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("FriendRequestRejected", "Friend request rejected successfully")
                        lifecycleScope.launch {
                            (requireActivity() as MainActivity).onRefreshOtherUser(
                                friendRefresh(
                                    friend
                                )
                            )
                        }
                    } else if (response.code() == 404) {
                        Log.d("FriendRequestNotRejected", "Friend not found")
                    }else {
                        val message = "erreur : ${response.message()}"
                        Log.d("responseNotSucc",message)
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("Failure","Failure")
                }
            })
        }


        binding.btnFragmentOtherUserProfileDeleteFriend.setOnClickListener {
            val cancelCall = friendRepository.deleteFriend(friendId)
            cancelCall.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("FriendDeletion", "Friend deleted successfully")
                        lifecycleScope.launch {
                            (requireActivity() as MainActivity).onRefreshOtherUser(
                                friendRefresh(
                                    friend
                                )
                            )
                        }
                    } else if (response.code() == 404) {
                        Log.d("FriendRequestNotDeleted", "Friend not found")
                    }else {
                        val message = "erreur : ${response.message()}"
                        Log.d("responseNotSucc",message)
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("Failure","Failure")
                }
            })
        }
    }

    suspend fun friendRefresh(friend: DtoInputFriend): DtoInputFriend {
        userService = UserService(requireContext())
        val friendId = friend.userId
        friend.isFriendWithConnected = userService.getUserProfile(friendId)
        return friend
    }

    fun replaceFragment(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fcb_otherUserProfileFriendManager_list, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun refreshFragment() {
        val friendId = friend.userId
        val profileFriendFragment = ProfileFriendManagerFragment.newInstance(friendId)
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fcb_otherUserProfileFriendManager_list, profileFriendFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    suspend fun startUserData(friend: DtoInputFriend) {
        try {
            val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
            val jwtToken = prefs.getString("jwtToken", "") ?: ""
            userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)
            friendRepository = RetrofitFactory.create(jwtToken, IFriendRepository::class.java)
            imageRepository = RetrofitFactory.create(jwtToken, IImageRepository::class.java)
            imageService = ImageService(requireContext(), imageRepository)

            val userId = friend.userId
            val response = userRepository.getUserProfile(userId)
                val imageId = response.imageId
                imageId?.let { id ->
                    val image = imageService.getImageById(id)
                    if (image.startsWith("@drawable/")) {
                        val resourceId = resources.getIdentifier(
                            image.substringAfterLast('/'),
                            "drawable",
                            "com.groupe5.moodmobile"
                        )
                        Log.e("ressourceid", "" + resourceId)
                        withContext(Dispatchers.Main) {
                            Picasso.with(binding.ivFragmentOtherUserProfileUserImage.context).load(resourceId)
                                .into(binding.ivFragmentOtherUserProfileUserImage)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Picasso.with(binding.ivFragmentOtherUserProfileUserImage.context).load(image)
                                .into(binding.ivFragmentOtherUserProfileUserImage)
                        }
                    }
                }
                // Update TextViews with profile data
                withContext(Dispatchers.Main) {
                    binding.tvFragmentOtherUserProfileUserUsername.text = response.userName
                    binding.tvFragmentOtherUserProfileUserNbPublications.text =
                        "Publications: ${response.publicationCount}"
                    binding.tvFragmentOtherUserProfileUserNbFriends.text =
                        "Friends: ${response.friendCount}"
                    binding.tvFragmentOtherUserProfileUserDescription.text = response.accountDescription
                    if (friend.isFriendWithConnected == 2) {
                        binding.btnFragmentOtherUserProfileDeleteFriend.isEnabled = true
                        binding.btnFragmentOtherUserProfileDeleteFriend.visibility = View.VISIBLE
                    }
                    if (friend.isFriendWithConnected == 0) {
                        binding.btnFragmentOtherUserProfileCancelFriendRequest.isEnabled = true
                        binding.btnFragmentOtherUserProfileCancelFriendRequest.visibility = View.VISIBLE
                    }
                    if (friend.isFriendWithConnected == 1) {
                        binding.btnFragmentOtherUserProfileAcceptFriendRequest.isEnabled = true
                        binding.btnFragmentOtherUserProfileAcceptFriendRequest.visibility = View.VISIBLE
                        binding.btnFragmentOtherUserProfileRejectFriendRequest.isEnabled = true
                        binding.btnFragmentOtherUserProfileRejectFriendRequest.visibility = View.VISIBLE
                    }
                    if (friend.isFriendWithConnected == -1) {
                        binding.btnFragmentOtherUserProfileAddFriend.isEnabled = true
                        binding.btnFragmentOtherUserProfileAddFriend.visibility = View.VISIBLE
                    }
                }
        } catch (e: Exception) {
            Log.e("","error : "+e)
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(friend: DtoInputFriend) = OtherUserProfileFragment(friend)
    }
}