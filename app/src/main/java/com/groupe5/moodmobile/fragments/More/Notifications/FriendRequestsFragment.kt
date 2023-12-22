package com.groupe5.moodmobile.fragments.More.Notifications

import android.os.Bundle
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
import com.groupe5.moodmobile.databinding.FragmentFriendResquestManagerBinding
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriendRequest
import com.groupe5.moodmobile.fragments.Discover.DiscoverFragment
import com.groupe5.moodmobile.fragments.Discover.Users.DiscoverUsersRecyclerViewAdapter
import com.groupe5.moodmobile.placeholder.PlaceholderContent
class FriendRequestsFragment : Fragment() {
    private val friendRequestUI: ArrayList<DtoInputFriendRequest> = arrayListOf()
    lateinit var friendRequestsRecyclerViewAdapter: FriendRequestsRecyclerViewAdapter
    lateinit var binding: FragmentFriendResquestManagerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.friend_requests_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            friendRequestsRecyclerViewAdapter = FriendRequestsRecyclerViewAdapter(requireContext(), friendRequestUI)
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            view.layoutManager = layoutManager
            view.adapter = friendRequestsRecyclerViewAdapter
        }
        return view
    }
    fun initUIWithUsers(users: List<DtoInputFriendRequest>?) {
        users?.forEach(friendRequestUI::add)
        friendRequestsRecyclerViewAdapter.notifyDataSetChanged()
    }

    fun deleteOrAcceptUserUI() {
        (requireActivity() as MainActivity).goToNotifications()
    }

    companion object {
        @JvmStatic
        fun newInstance() = FriendRequestsFragment()
    }
}