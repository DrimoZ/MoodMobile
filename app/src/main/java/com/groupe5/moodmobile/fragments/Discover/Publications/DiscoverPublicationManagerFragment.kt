package com.groupe5.moodmobile.fragments.Discover.Publications

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.activities.MainActivity
import com.groupe5.moodmobile.databinding.FragmentDiscoverPublicationManagerBinding

class DiscoverPublicationManagerFragment : Fragment() {
    private lateinit var binding: FragmentDiscoverPublicationManagerBinding
    private lateinit var viewModel: DiscoverPublicationManagerViewModel

    companion object {
        private const val SEARCH_VALUE_KEY = "searchvalue"

        fun newInstance(searchValue: String): DiscoverPublicationManagerFragment {
            val fragment = DiscoverPublicationManagerFragment()
            val args = Bundle()
            args.putString(SEARCH_VALUE_KEY, searchValue)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiscoverPublicationManagerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        observeViewModel()
        initUI()
        setListeners()
        viewModel.startGetAllPublications()
    }

    private fun initViewModel() {
        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val token = prefs.getString("jwtToken", "") ?: ""
        val searchValue = arguments?.getString(SEARCH_VALUE_KEY) ?: ""
        viewModel = DiscoverPublicationManagerViewModel(token, searchValue)
    }

    private fun observeViewModel() {
        val discoverPublicationsFragment = childFragmentManager.findFragmentById(R.id.fcb_discoverPublicationManager_list) as DiscoverPublicationsFragment

        viewModel.mutablePublicationLiveData.observe(viewLifecycleOwner) {
            discoverPublicationsFragment.initUIWithPublications(it)
        }

        viewModel.mutableCount.observe(viewLifecycleOwner) {
            when {
                it == -1 -> hideLoadMoreButton()
                it % 4 == 0 -> showLoadMoreButton()
                else -> hideLoadMoreButton()
            }
        }

        discoverPublicationsFragment.discoverPublicationRecyclerViewAdapter.setOnOpenClickListener(object :
            DiscoverPublicationsRecyclerViewAdapter.OnOpenClickListener {
            override fun onOpenClick(idPublication: Int) {
                (requireActivity() as MainActivity).openPublicationInformation(idPublication)
            }
        })
    }

    private fun initUI() {
        // Initialize UI components if needed
    }

    private fun setListeners() {
        binding.btnDiscoverPublicationsManagerLoadMorePublications.setOnClickListener {
            viewModel.showCount += 30
            viewModel.startGetAllPublications()
        }
    }

    private fun showLoadMoreButton() {
        binding.btnDiscoverPublicationsManagerLoadMorePublications.visibility = View.VISIBLE
        binding.btnDiscoverPublicationsManagerLoadMorePublications.isEnabled = true
    }

    private fun hideLoadMoreButton() {
        binding.btnDiscoverPublicationsManagerLoadMorePublications.visibility = View.INVISIBLE
        binding.btnDiscoverPublicationsManagerLoadMorePublications.isEnabled = false
    }
}
