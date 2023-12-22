package com.groupe5.moodmobile.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.databinding.ActivityMainBinding
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.fragments.Discover.DiscoverFragment
import com.groupe5.moodmobile.fragments.AddPublication.AddPublicationFragment
import com.groupe5.moodmobile.fragments.More.DeleteAccountFragment
import com.groupe5.moodmobile.fragments.More.Notifications.FriendRequestsFragment
import com.groupe5.moodmobile.fragments.More.Notifications.FriendRequestsManagerFragment
import com.groupe5.moodmobile.fragments.More.ParametersFragment
import com.groupe5.moodmobile.fragments.NewsFeed.NewsFeedPublicationManagerFragment
import com.groupe5.moodmobile.fragments.Publication.PublicationInformationFragment
import com.groupe5.moodmobile.fragments.UserProfile.OtherUserProfileFragment
import com.groupe5.moodmobile.fragments.UserProfile.ProfileFragment
import com.groupe5.moodmobile.fragments.UserProfile.UserFriends.ProfileFriendsRecyclerViewAdapter
import com.groupe5.moodmobile.services.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), ProfileFriendsRecyclerViewAdapter.OnFriendClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userService: UserService
    private var isParameters = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!isTokenPresent()) {
            startActivity(Intent(this, SigninActivity::class.java))
            finish()
        }

        showNewsFeedFragment()
        setUpListeners()
    }

    private fun showNewsFeedFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView_mainActivity, NewsFeedPublicationManagerFragment.newInstance(), "NewsFeedPublicationManagerFragment")
            .commit()
    }
    fun toggleDeleteAccountFragment(userId: String, show : Boolean) {
        if(show){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView_mainActivity, DeleteAccountFragment.newInstance(userId), "DeleteAccountFragment")
                .commit()
        }else{
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView_mainActivity, ParametersFragment.newInstance(), "ParametersFragment")
                .commit()
        }
    }

    private fun isTokenPresent(): Boolean = getSharedPreferences("mood", MODE_PRIVATE).getString("jwtToken", null) != null

    private fun setUpListeners() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navbar_newsFeed -> showNewsFeedFragment()
                R.id.navbar_discover -> replaceFragment(DiscoverFragment.newInstance())
                R.id.navbar_newPublication -> replaceFragment(AddPublicationFragment.newInstance())
                R.id.navbar_profile -> replaceFragment(ProfileFragment.newInstance())
                R.id.navbar_more -> toggleParametersFragmentVisibility()
            }
            true
        }
    }

    private fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        closePublicationInformation()
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView_mainActivity, fragment).commit()
    }

    fun toggleParametersFragmentVisibility() {
        if (isParameters) {
            hideSideBarFragment()
        } else {
            showSideBarFragment()
        }
    }

    private fun showSideBarFragment() {
        binding.fragmentContainerViewMainActivityParameters.visibility = View.VISIBLE
        binding.fragmentContainerViewMainActivityParameters.isEnabled = true
        isParameters = true
    }

    private fun hideSideBarFragment() {
        binding.fragmentContainerViewMainActivityParameters.visibility = View.INVISIBLE
        binding.fragmentContainerViewMainActivityParameters.isEnabled = false
        isParameters = false
    }

    fun closePublicationInformation() {
        val fragment = supportFragmentManager.findFragmentByTag("PublicationInformationFragment")
        fragment?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
        binding.nestedScrollView2.visibility = View.INVISIBLE
        binding.nestedScrollView2.isEnabled = false
        binding.nestedScrollView2.isClickable = false
    }

    fun openPublicationInformation(idPublication: Int) {
        binding.nestedScrollView2.visibility = View.VISIBLE
        binding.nestedScrollView2.isEnabled = true
        binding.nestedScrollView2.isClickable = true
        supportFragmentManager
            .beginTransaction()
            .add(
                R.id.fragmentContainerView_mainActivitySpecialPublication,
                PublicationInformationFragment.newInstance(idPublication),
                "PublicationInformationFragment"
            )
            .commit()
    }

    fun goToParameters() {
        hideSideBarFragment()
        replaceFragment(ParametersFragment.newInstance())
    }

    fun goToNotifications() {
        hideSideBarFragment()
        replaceFragment(FriendRequestsManagerFragment.newInstance())
    }

    fun signOut() {
        val prefs = getSharedPreferences("mood", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("jwtToken", null)
        editor.apply()

        if (!isTokenPresent()) {
            startActivity(Intent(this, SigninActivity::class.java))
            finish()
        }
    }

    fun onRefreshOtherUser(friend: DtoInputFriend) {
        replaceFragment(OtherUserProfileFragment.newInstance(friend))
    }

    fun onRefreshUserProfile() {
        replaceFragment(ProfileFragment.newInstance())
    }

    override fun onFriendClick(friend: DtoInputFriend) {
        userService = UserService(this)
        CoroutineScope(Dispatchers.Main).launch {
            val userId = userService.getUserId()
            if (friend.userId == userId) {
                replaceFragment(ProfileFragment.newInstance())
            } else {
                replaceFragment(OtherUserProfileFragment.newInstance(friend))
            }
        }
    }
}
