package com.groupe5.moodmobile.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.fragments.Discover.DiscoverFragment
import com.groupe5.moodmobile.databinding.ActivityMainBinding
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.fragments.MessageFragment
import com.groupe5.moodmobile.fragments.More.ParametersFragment
import com.groupe5.moodmobile.fragments.NewsFeedFragment
import com.groupe5.moodmobile.fragments.UserProfile.OtherUserProfileFragment
import com.groupe5.moodmobile.fragments.UserProfile.ProfileFragment
import com.groupe5.moodmobile.fragments.UserProfile.ProfileFriendsRecyclerViewAdapter
import com.groupe5.moodmobile.services.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), ProfileFriendsRecyclerViewAdapter.OnFriendClickListener {
    lateinit var binding: ActivityMainBinding
    private lateinit var userService: UserService
    private var isParameters = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!isTokenPresent()) {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
            finish()
        }

        supportFragmentManager
            .beginTransaction()
            .add(
                R.id.fragmentContainerView_mainActivity,
                NewsFeedFragment.newInstance(),
                "NewsFeedFragment"
            )
            .commit()

        setUpListeners()
    }

    fun onRefreshOtherUser(friend: DtoInputFriend) {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainerView_mainActivity,
                OtherUserProfileFragment.newInstance(friend),
                "OtherUserProfileFragment"
            )
            .commit()
    }

    fun onRefreshUserProfile() {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainerView_mainActivity,
                ProfileFragment.newInstance(),
                "ProfileFragment"
            )
            .commit()
    }

    private fun isTokenPresent(): Boolean {
        val prefs = getSharedPreferences("mood", MODE_PRIVATE)
        val token = prefs.getString("jwtToken", null)
        return token != null
    }

    private fun setUpListeners() {

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navbar_newsFeed -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.fragmentContainerView_mainActivity,
                            NewsFeedFragment.newInstance(),
                            "NewsFeedFragment"
                        )
                        .commit()
                }
                R.id.navbar_messages -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.fragmentContainerView_mainActivity,
                            MessageFragment.newInstance(),
                            "MessageFragment"
                        )
                        .commit()
                }
                R.id.navbar_search -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.fragmentContainerView_mainActivity,
                            DiscoverFragment.newInstance(),
                            "SearchFragment"
                        )
                        .commit()
                }
                R.id.navbar_profile -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.fragmentContainerView_mainActivity,
                            ProfileFragment.newInstance(),
                            "ProfileFragment"
                        )
                        .commit()
                }
                R.id.navbar_more -> {
                    if(isParameters){
                        binding.fragmentContainerViewMainActivityParameters.visibility = View.INVISIBLE
                        binding.fragmentContainerViewMainActivityParameters.isEnabled = false
                        isParameters = false
                    }else{
                        binding.fragmentContainerViewMainActivityParameters.visibility = View.VISIBLE
                        binding.fragmentContainerViewMainActivityParameters.isEnabled = true
                        isParameters = true
                    }
                }
            }
            true
        }
    }

    fun goToParameters(){
        binding.fragmentContainerViewMainActivityParameters.visibility = View.INVISIBLE
        binding.fragmentContainerViewMainActivityParameters.isEnabled = false
        isParameters = false
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainerView_mainActivity,
                ParametersFragment.newInstance(),
                "ParametersFragment"
            )
            .commit()
    }

    fun signOut(){
        val prefs = getSharedPreferences("mood", MODE_PRIVATE)
                    val editor = prefs.edit()
                    editor.putString("jwtToken", null)
                    editor.apply()
                    if (!isTokenPresent()) {
                        val intent = Intent(this, SigninActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
    }
    override fun onFriendClick(friend: DtoInputFriend) {
        userService = UserService(this)
        CoroutineScope(Dispatchers.Main).launch {
            val userId = userService.getUserId()
            val friendId = friend.id
            if(friendId == userId){
                supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.fragmentContainerView_mainActivity,
                        ProfileFragment.newInstance(),
                        "ProfileFragment"
                    )
                    .commit()
            }else{
                supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.fragmentContainerView_mainActivity,
                        OtherUserProfileFragment.newInstance(friend),
                        "OtherUserProfileFragment"
                    )
                    .commit()
            }
        }
    }
}