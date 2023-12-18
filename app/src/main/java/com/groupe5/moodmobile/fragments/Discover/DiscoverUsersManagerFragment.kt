package com.groupe5.moodmobile.fragments.Discover

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import android.content.Context
import com.groupe5.moodmobile.activities.MainActivity
import com.groupe5.moodmobile.databinding.FragmentDiscoverUsersManagerBinding
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend

class DiscoverUsersManagerFragment : Fragment() {
    lateinit var binding: FragmentDiscoverUsersManagerBinding

    companion object {
        fun newInstance(searchValue: String): DiscoverUsersManagerFragment {
            val fragment = DiscoverUsersManagerFragment()
            val args = Bundle()
            args.putString("searchvalue", searchValue)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: DiscoverUsersManagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiscoverUsersManagerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val token = prefs.getString("jwtToken", "") ?: ""

        val searchValue = arguments?.getString("searchvalue") ?: ""

        viewModel = DiscoverUsersManagerViewModel(token, searchValue)

        val discoverUsersFragment = childFragmentManager
            .findFragmentById(R.id.fcb_discoverUsersManager_list) as DiscoverUsersFragment

        viewModel.mutableUserLiveData.observe(viewLifecycleOwner) {
            discoverUsersFragment.initUIWithUsers(it)
        }
//        viewModel.mutableUserAddData.observe(viewLifecycleOwner) {
//            discoverUsersFragment.addUIToUsers(it)
//        }
        viewModel.startGetAllUsers()

        viewModel.mutableUserRefreshData.observe(viewLifecycleOwner){
            discoverUsersFragment.deleteOrAcceptUserUI()
        }

        viewModel.mutableCount.observe(viewLifecycleOwner){
            if(it == -1){
                binding.btnDiscoverUsersManagerLoadMoreUsers.visibility = View.INVISIBLE
                binding.btnDiscoverUsersManagerLoadMoreUsers.isEnabled = false
            }else if(it % 10 == 0){
                binding.btnDiscoverUsersManagerLoadMoreUsers.visibility = View.VISIBLE
                binding.btnDiscoverUsersManagerLoadMoreUsers.isEnabled = true
            }else{
                binding.btnDiscoverUsersManagerLoadMoreUsers.visibility = View.INVISIBLE
                binding.btnDiscoverUsersManagerLoadMoreUsers.isEnabled = false
            }
        }

        binding.btnDiscoverUsersManagerLoadMoreUsers.setOnClickListener {
            viewModel.showCount += 10
            viewModel.startGetAllUsers()
        }

        discoverUsersFragment.discoverUsersRecyclerViewAdapter.setOnDeleteClickListener(object :
            DiscoverUsersRecyclerViewAdapter.OnDeleteClickListener {
            override fun onDeleteClick(user: DtoInputFriend) {
                viewModel.deleteFriend(user)
            }
        })
        discoverUsersFragment.discoverUsersRecyclerViewAdapter.setOnAddClickListener(object :
            DiscoverUsersRecyclerViewAdapter.OnAddClickListener {
            override fun onAddClick(user: DtoInputFriend) {
                viewModel.addFriend(user)
            }
        })
        discoverUsersFragment.discoverUsersRecyclerViewAdapter.setOnCancelClickListener(object :
            DiscoverUsersRecyclerViewAdapter.OnCancelClickListener {
            override fun onCancelClick(user: DtoInputFriend) {
                viewModel.cancelFriendRequest(user)
            }
        })
        discoverUsersFragment.discoverUsersRecyclerViewAdapter.setOnAcceptClickListener(object :
            DiscoverUsersRecyclerViewAdapter.OnAcceptClickListener {
            override fun onAcceptClick(user: DtoInputFriend) {
                viewModel.acceptFriendRequest(user)
            }
        })
        discoverUsersFragment.discoverUsersRecyclerViewAdapter.setOnRejectClickListener(object :
            DiscoverUsersRecyclerViewAdapter.OnRejectClickListener {
            override fun onRejectClick(user: DtoInputFriend) {
                viewModel.rejectFriendRequest(user)
            }
        })
        discoverUsersFragment.discoverUsersRecyclerViewAdapter.setOnUserClickListener(object :
            DiscoverUsersRecyclerViewAdapter.OnUserClickListener {
            override fun onUserClick(user: DtoInputFriend) {
                (requireActivity() as MainActivity).onFriendClick(user)
            }
        })
    }
}
