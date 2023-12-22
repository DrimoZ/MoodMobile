package com.groupe5.moodmobile.fragments.More

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.groupe5.moodmobile.activities.MainActivity
import com.groupe5.moodmobile.databinding.FragmentSideMenuBinding

class SideMenuFragment : Fragment() {
    private lateinit var binding: FragmentSideMenuBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSideMenuBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFragmentSideMenuParameters.setOnClickListener {
            (requireActivity() as MainActivity).goToParameters()
        }

        binding.btnFragmentSideMenuNotifications.setOnClickListener {
            (requireActivity() as MainActivity).goToNotifications()
        }

        binding.btnFragmentSideMenuSignOut.setOnClickListener {
            (requireActivity() as MainActivity).signOut()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SideMenuFragment()
    }
}