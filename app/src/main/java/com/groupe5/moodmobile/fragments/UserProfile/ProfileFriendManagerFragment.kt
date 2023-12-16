package com.groupe5.moodmobile.fragments.UserProfile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.groupe5.moodmobile.activities.MainActivity
import com.groupe5.moodmobile.classes.SharedViewModel
import com.groupe5.moodmobile.databinding.FragmentProfileFriendManagerBinding
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend

class ProfileFriendManagerFragment : Fragment() {
    lateinit var binding: FragmentProfileFriendManagerBinding
    private lateinit var sharedViewModel: SharedViewModel

    companion object {
        fun newInstance(friendId: String): ProfileFriendManagerFragment {
            val fragment = ProfileFriendManagerFragment()
            val args = Bundle()
            args.putString("friendId", friendId)
            fragment.arguments = args
            return fragment
        }
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

        val friendId = arguments?.getString("friendId")
        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val token = prefs.getString("jwtToken", "") ?: ""
        viewModel = ProfileFriendManagerViewModel(token)

        val profileFriendsFragment = childFragmentManager
            .findFragmentById(R.id.fcb_profileFriendManager_list) as ProfileFriendsFragment

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        //...
        viewModel.mutableFriendDeleteData.observe(viewLifecycleOwner) { friend ->
            sharedViewModel.friendData.value = friend
        }

        viewModel.mutableFriendDeleteData.observe(viewLifecycleOwner){
            if (friendId != null) {
                profileFriendsFragment.deleteFriendFromUI(it, true)
            } else {
                profileFriendsFragment.deleteFriendFromUI(it, false)
            }

        }
        viewModel.mutableFriendRefreshData.observe(viewLifecycleOwner){
            if (friendId != null) {
                profileFriendsFragment.RefreshFriendUI(friendId)
            }
        }
        viewModel.mutableFriendAcceptData.observe(viewLifecycleOwner){
            if (friendId != null) {
                profileFriendsFragment.acceptFriendToUI(it, true)
            } else {
                profileFriendsFragment.acceptFriendToUI(it, false)
            }
        }
        viewModel.mutableFriendLiveData.observe(viewLifecycleOwner) {
            profileFriendsFragment.initUIWithFriends(it)
        }

        viewModel.startGetAllFriends(if (friendId.isNullOrEmpty()) null else friendId)

        profileFriendsFragment.profileFriendRecyclerViewAdapter.setOnDeleteClickListener(object :
            ProfileFriendsRecyclerViewAdapter.OnDeleteClickListener {
            override fun onDeleteClick(friend: DtoInputFriend) {
                viewModel.deleteFriend(friend)
            }
        })
        profileFriendsFragment.profileFriendRecyclerViewAdapter.setOnAddClickListener(object :
            ProfileFriendsRecyclerViewAdapter.OnAddClickListener {
            override fun onAddClick(friend: DtoInputFriend) {
                viewModel.addFriend(friend)
            }
        })
        profileFriendsFragment.profileFriendRecyclerViewAdapter.setOnCancelClickListener(object :
            ProfileFriendsRecyclerViewAdapter.OnCancelClickListener {
            override fun onCancelClick(friend: DtoInputFriend) {
                viewModel.cancelFriendRequest(friend)
            }
        })
        profileFriendsFragment.profileFriendRecyclerViewAdapter.setOnAcceptClickListener(object :
            ProfileFriendsRecyclerViewAdapter.OnAcceptClickListener {
            override fun onAcceptClick(friend: DtoInputFriend) {
                viewModel.acceptFriendRequest(friend)
            }
        })
        profileFriendsFragment.profileFriendRecyclerViewAdapter.setOnRejectClickListener(object :
            ProfileFriendsRecyclerViewAdapter.OnRejectClickListener {
            override fun onRejectClick(friend: DtoInputFriend) {
                viewModel.rejectFriendRequest(friend)
            }
        })
        profileFriendsFragment.profileFriendRecyclerViewAdapter.setOnFriendClickListener(object :
            ProfileFriendsRecyclerViewAdapter.OnFriendClickListener {
            override fun onFriendClick(friend: DtoInputFriend) {
                (requireActivity() as MainActivity).onFriendClick(friend)
            }
        })
    }
}
