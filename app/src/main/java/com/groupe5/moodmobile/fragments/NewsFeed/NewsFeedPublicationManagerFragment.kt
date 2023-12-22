package com.groupe5.moodmobile.fragments.NewsFeed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import android.content.Context
import com.groupe5.moodmobile.databinding.FragmentNewsFeedPublicationManagerBinding

class NewsFeedPublicationManagerFragment : Fragment() {
    lateinit var binding: FragmentNewsFeedPublicationManagerBinding

    companion object {
        fun newInstance() = NewsFeedPublicationManagerFragment()
    }

    private lateinit var viewModel: NewsFeedPublicationManagerViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsFeedPublicationManagerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val token = prefs.getString("jwtToken", "") ?: ""

        viewModel = NewsFeedPublicationManagerViewModel(token)

        val newsFeedPublicationFragment = childFragmentManager
            .findFragmentById(R.id.fcb_newsFeedPublication_list) as NewsFeedPublicationFragment

        viewModel.mutablePublicationLiveData.observe(viewLifecycleOwner) {
            newsFeedPublicationFragment.initUIWithPublications(it)
        }
        viewModel.startGetAllPublications()
    }
}
