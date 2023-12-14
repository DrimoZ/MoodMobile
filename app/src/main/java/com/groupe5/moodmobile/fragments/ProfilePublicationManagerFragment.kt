package com.groupe5.moodmobile.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.databinding.FragmentProfilePublicationManagerBinding
import com.groupe5.moodmobile.R
import android.content.Context

class ProfilePublicationManagerFragment : Fragment() {
    lateinit var binding: FragmentProfilePublicationManagerBinding

    companion object {
        fun newInstance() = ProfilePublicationManagerFragment()
    }

    private lateinit var viewModel: ProfilePublicationManagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilePublicationManagerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Get the JWT token from SharedPreferences
        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val token = prefs.getString("jwtToken", "") ?: ""

        // Directly create the ViewModel with the token
        //viewModel = ViewModelProvider(this).get(ProfilePublicationManagerViewModel::class.java)
        viewModel = ProfilePublicationManagerViewModel(token)

        val profilePublicationsFragment = childFragmentManager
            .findFragmentById(R.id.fcb_profilePublicationManager_list) as ProfilePublicationsFragment

        viewModel.mutablePublicationLiveData.observe(viewLifecycleOwner) {
            Log.i("Publications", it.toString())
            profilePublicationsFragment.initUIWithPublications(it)
        }

        viewModel.startGetAllPublications()
    }
}
