package com.groupe5.moodmobile.fragments.UserProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.databinding.FragmentProfileFriendManagerBinding
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend

class ProfileFriendsFragment : Fragment() {
    private val friendUI: ArrayList<DtoInputFriend> = arrayListOf()
    val profileFriendRecyclerViewAdapter = ProfileFriendsRecyclerViewAdapter(friendUI)
    lateinit var binding: FragmentProfileFriendManagerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friend_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            view.layoutManager = layoutManager
            view.adapter = profileFriendRecyclerViewAdapter
        }
        return view
    }

    fun initUIWithFriends(friends: List<DtoInputFriend>?) {
        friends?.forEach(friendUI::add)
        profileFriendRecyclerViewAdapter.notifyDataSetChanged()
    }

    fun deleteFriendFromUI(friend: DtoInputFriend) {
        friendUI.remove(friend)
        profileFriendRecyclerViewAdapter.notifyDataSetChanged()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFriendsFragment()
    }
}