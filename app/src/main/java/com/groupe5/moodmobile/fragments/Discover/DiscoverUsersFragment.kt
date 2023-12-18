package com.groupe5.moodmobile.fragments.Discover

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.activities.MainActivity
import com.groupe5.moodmobile.databinding.FragmentDiscoverUsersManagerBinding
import com.groupe5.moodmobile.databinding.FragmentProfileFriendManagerBinding
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.fragments.UserProfile.OtherUserProfileFragment
import com.groupe5.moodmobile.fragments.UserProfile.ProfileFriendsRecyclerViewAdapter
import com.groupe5.moodmobile.placeholder.PlaceholderContent
class DiscoverUsersFragment : Fragment() {
    private val userUI: ArrayList<DtoInputFriend> = arrayListOf()
    lateinit var discoverUsersRecyclerViewAdapter: DiscoverUsersRecyclerViewAdapter
    lateinit var binding: FragmentDiscoverUsersManagerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_discover_users_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            discoverUsersRecyclerViewAdapter = DiscoverUsersRecyclerViewAdapter(requireContext(), userUI)
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            view.layoutManager = layoutManager
            view.adapter = discoverUsersRecyclerViewAdapter
        }
        return view
    }

    fun initUIWithUsers(users: List<DtoInputFriend>?) {
        users?.forEach(userUI::add)
        discoverUsersRecyclerViewAdapter.notifyDataSetChanged()
    }

    fun deleteOrAcceptUserUI() {
        val activity = requireActivity()
        if (activity is MainActivity) {
            val currentFragment = activity.supportFragmentManager.findFragmentById(R.id.fragmentContainerView_mainActivity)
            if (currentFragment is DiscoverFragment) {
                currentFragment.refreshFragment()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DiscoverUsersFragment()
    }
}