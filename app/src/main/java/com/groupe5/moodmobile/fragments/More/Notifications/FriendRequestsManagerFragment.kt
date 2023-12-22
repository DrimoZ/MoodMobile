package com.groupe5.moodmobile.fragments.More.Notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.groupe5.moodmobile.activities.MainActivity
import com.groupe5.moodmobile.databinding.FragmentDiscoverUsersManagerBinding
import com.groupe5.moodmobile.databinding.FragmentFriendResquestManagerBinding
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriendRequest
import com.groupe5.moodmobile.services.UserService
import kotlinx.coroutines.launch

class FriendRequestsManagerFragment : Fragment() {
    lateinit var binding: FragmentFriendResquestManagerBinding
    lateinit var userService: UserService

    companion object {
        fun newInstance() = FriendRequestsManagerFragment()
    }

    private lateinit var viewModel: FriendRequestsManagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendResquestManagerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val token = prefs.getString("jwtToken", "") ?: ""
        userService = UserService(requireContext())

        viewModel = FriendRequestsManagerViewModel(token)

        val friendRequestFragment = childFragmentManager
            .findFragmentById(R.id.fcb_friendRequest_list) as FriendRequestsFragment

        viewModel.mutableFriendRequestLiveData.observe(viewLifecycleOwner) {
            friendRequestFragment.initUIWithUsers(it)
        }
        lifecycleScope.launch {
            viewModel.startGetAllUsers()
        }

        viewModel.mutableFriendRequestRefreshData.observe(viewLifecycleOwner){
            friendRequestFragment.deleteOrAcceptUserUI()
        }
        friendRequestFragment.friendRequestsRecyclerViewAdapter.setOnAcceptClickListener(object :
            FriendRequestsRecyclerViewAdapter.OnAcceptClickListener {
            override fun onAcceptClick(user: DtoInputFriendRequest) {
                viewModel.acceptFriendRequest(user)
            }
        })
        friendRequestFragment.friendRequestsRecyclerViewAdapter.setOnRejectClickListener(object :
            FriendRequestsRecyclerViewAdapter.OnRejectClickListener {
            override fun onRejectClick(user: DtoInputFriendRequest) {
                viewModel.rejectFriendRequest(user)
            }
        })
        friendRequestFragment.friendRequestsRecyclerViewAdapter.setOnUserClickListener(object :
            FriendRequestsRecyclerViewAdapter.OnUserClickListener {
            override fun onUserClick(user: DtoInputFriendRequest) {
                lifecycleScope.launch {
                    (requireActivity() as MainActivity).onFriendClick(userService.getFriendDto(user.userId))
                }
            }
        })
    }
}
