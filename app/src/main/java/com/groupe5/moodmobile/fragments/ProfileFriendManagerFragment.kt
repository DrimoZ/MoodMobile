package com.groupe5.moodmobile.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import android.content.Context
import com.groupe5.moodmobile.databinding.FragmentProfileFriendManagerBinding

class ProfileFriendManagerFragment : Fragment() {
    lateinit var binding: FragmentProfileFriendManagerBinding

    companion object {
        fun newInstance() = ProfileFriendManagerFragment()
    }

    private lateinit var viewModel: ProfileFriendManagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileFriendManagerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Get the JWT token from SharedPreferences
        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val token = prefs.getString("jwtToken", "") ?: ""
        viewModel = ProfileFriendManagerViewModel(token)

        val profileFriendsFragment = childFragmentManager
            .findFragmentById(R.id.fcb_profileFriendManager_list) as ProfileFriendsFragment

        viewModel.mutableFriendLiveData.observe(viewLifecycleOwner) {
            Log.i("Friends", it.toString())
            profileFriendsFragment.initUIWithFriends(it)
        }

        viewModel.startGetAllFriends()
    }
}
