package com.groupe5.moodmobile.fragments.UserProfile.UserFriends

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.activities.MainActivity
import com.groupe5.moodmobile.databinding.FragmentProfileFriendManagerBinding
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.fragments.UserProfile.OtherUserProfileFragment

class ProfileFriendsFragment : Fragment() {
    private val friendUI: ArrayList<DtoInputFriend> = arrayListOf()
    lateinit var profileFriendRecyclerViewAdapter: ProfileFriendsRecyclerViewAdapter
    lateinit var binding: FragmentProfileFriendManagerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friend_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            profileFriendRecyclerViewAdapter = ProfileFriendsRecyclerViewAdapter(requireContext(), friendUI)
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

    fun deleteFriendFromUI(friend: DtoInputFriend, isOtherUser: Boolean) {
        if(!isOtherUser){
            friendUI.remove(friend)
            profileFriendRecyclerViewAdapter.notifyDataSetChanged()
        }
        else{
            val activity = requireActivity()
            if (activity is MainActivity) {
                val currentFragment = activity.supportFragmentManager.findFragmentById(R.id.fragmentContainerView_mainActivity)
                if (currentFragment is OtherUserProfileFragment) {
                    currentFragment.refreshFragment()
                }
            }
        }

    }

    fun acceptFriendToUI(friend: DtoInputFriend, isOtherUser: Boolean) {
        if(!isOtherUser){
            friendUI.add(friend)
            profileFriendRecyclerViewAdapter.notifyDataSetChanged()
        }
        else{
            val activity = requireActivity()
            if (activity is MainActivity) {
                val currentFragment = activity.supportFragmentManager.findFragmentById(R.id.fragmentContainerView_mainActivity)
                if (currentFragment is OtherUserProfileFragment) {
                    currentFragment.refreshFragment()
                }
            }
        }

    }
    fun RefreshFriendUI(friendId: String) {
        val activity = requireActivity()
        if (activity is MainActivity) {
            val currentFragment = activity.supportFragmentManager.findFragmentById(R.id.fragmentContainerView_mainActivity)
            if (currentFragment is OtherUserProfileFragment) {
                currentFragment.refreshFragment()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFriendsFragment()
    }
}