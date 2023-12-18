package com.groupe5.moodmobile.fragments.Discover

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.databinding.FragmentDiscoverBinding
import com.groupe5.moodmobile.databinding.FragmentOtherUserProfileBinding
import com.groupe5.moodmobile.fragments.UserProfile.ProfileFriendManagerFragment
import com.groupe5.moodmobile.fragments.UserProfile.ProfilePublicationManagerFragment

class DiscoverFragment : Fragment() {
    private lateinit var binding: FragmentDiscoverBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDiscoverBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnFragmentDiscoverPublications.setOnClickListener {
            val searchValue = binding.siFragmentDiscoverSearchbar.text.toString()
            val profilePublicationFragment = DiscoverPublicationManagerFragment.newInstance(searchValue)
            replaceFragment(profilePublicationFragment)
        }

        binding.btnFragmentDiscoverProfiles.setOnClickListener {
            val searchValue = binding.siFragmentDiscoverSearchbar.text.toString()
            val profileFriendFragment = DiscoverUsersManagerFragment.newInstance(searchValue)
            replaceFragment(profileFriendFragment)
        }

        binding.siFragmentDiscoverSearchbar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val currentFragment = childFragmentManager.findFragmentById(R.id.fcv_fragmentDiscover_container)
                val searchQuery = s.toString()
                if (currentFragment is DiscoverPublicationManagerFragment) {
                    val profilePublicationFragment = DiscoverPublicationManagerFragment.newInstance(searchQuery)
                    replaceFragment(profilePublicationFragment)
                } else if (currentFragment is DiscoverUsersManagerFragment) {
                    val profilePublicationFragment = DiscoverUsersManagerFragment.newInstance(searchQuery)
                    replaceFragment(profilePublicationFragment)
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fcv_fragmentDiscover_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    fun refreshFragment() {
        val discoverUserFragment = DiscoverUsersManagerFragment.newInstance("")
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fcv_fragmentDiscover_container, discoverUserFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = DiscoverFragment()
    }
}