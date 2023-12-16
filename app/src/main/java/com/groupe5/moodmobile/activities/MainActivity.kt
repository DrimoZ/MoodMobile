package com.groupe5.moodmobile.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.fragments.SearchFragment
import com.groupe5.moodmobile.databinding.ActivityMainBinding
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserProfile
import com.groupe5.moodmobile.fragments.MessageFragment
import com.groupe5.moodmobile.fragments.NewsFeedFragment
import com.groupe5.moodmobile.fragments.NotificationFragment
import com.groupe5.moodmobile.fragments.SideMenuFragment
import com.groupe5.moodmobile.fragments.UserProfile.OtherUserProfileFragment
import com.groupe5.moodmobile.fragments.UserProfile.ProfileFragment
import com.groupe5.moodmobile.fragments.UserProfile.ProfileFriendManagerFragment
import com.groupe5.moodmobile.fragments.UserProfile.ProfileFriendsFragment
import com.groupe5.moodmobile.fragments.UserProfile.ProfileFriendsRecyclerViewAdapter
import com.groupe5.moodmobile.repositories.IUserRepository
import com.groupe5.moodmobile.services.UserService
import com.groupe5.moodmobile.utils.RetrofitFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), ProfileFriendsRecyclerViewAdapter.OnFriendClickListener {
    lateinit var binding: ActivityMainBinding
    private lateinit var userService: UserService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!isTokenPresent()) {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
            finish()
        }

        setUpListeners()
    }

    override fun onStart() {
        supportFragmentManager
            .beginTransaction()
            .add(
                R.id.fragmentContainerView_mainActivity,
                NewsFeedFragment.newInstance(),
                "NewsFeedFragment"
            )
            .commit()
        super.onStart()
    }

    fun onRefresh(friend: DtoInputFriend) {
        Log.e("",""+friend.isFriendWithConnected)
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainerView_mainActivity,
                OtherUserProfileFragment.newInstance(friend),
                "OtherUserProfileFragment"
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
                            SearchFragment.newInstance(),
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
                    supportFragmentManager
                        .beginTransaction()
                        .add(
                            R.id.fragmentContainerView_mainActivity,
                            SideMenuFragment.newInstance(),
                            "SideMenuFragment"
                        )
                        .commit()
//                    val prefs = getSharedPreferences("mood", MODE_PRIVATE)
//                    val editor = prefs.edit()
//                    editor.putString("jwtToken", null)
//                    editor.apply()
//                    if (!isTokenPresent()) {
//                        val intent = Intent(this, SigninActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//                    supportFragmentManager
//                        .beginTransaction()
//                        .replace(
//                            R.id.fragmentContainerView_mainActivity,
//                            NotificationFragment.newInstance(),
//                            "NotificationFragment"
//                        )
//                        .commit()
                }
            }
            true
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