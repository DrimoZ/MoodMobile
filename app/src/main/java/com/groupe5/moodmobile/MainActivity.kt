package com.groupe5.moodmobile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.groupe5.moodmobile.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
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
                R.id.navbar_notification -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.fragmentContainerView_mainActivity,
                            NotificationFragment.newInstance(),
                            "NotificationFragment"
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
            }
            true
        }
    }
}