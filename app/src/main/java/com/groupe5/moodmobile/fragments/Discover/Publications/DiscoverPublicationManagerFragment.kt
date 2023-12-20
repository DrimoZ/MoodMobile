package com.groupe5.moodmobile.fragments.Discover.Publications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import android.content.Context
import com.groupe5.moodmobile.activities.MainActivity
import com.groupe5.moodmobile.databinding.FragmentDiscoverPublicationManagerBinding

class DiscoverPublicationManagerFragment : Fragment() {
    lateinit var binding: FragmentDiscoverPublicationManagerBinding

    companion object {
        fun newInstance(searchValue: String): DiscoverPublicationManagerFragment {
            val fragment = DiscoverPublicationManagerFragment()
            val args = Bundle()
            args.putString("searchvalue", searchValue)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: DiscoverPublicationManagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiscoverPublicationManagerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val token = prefs.getString("jwtToken", "") ?: ""

        val searchValue = arguments?.getString("searchvalue") ?: ""

        viewModel = DiscoverPublicationManagerViewModel(token, searchValue)

        val discoverPublicationsFragment = childFragmentManager
            .findFragmentById(R.id.fcb_discoverPublicationManager_list) as DiscoverPublicationsFragment

        viewModel.mutablePublicationLiveData.observe(viewLifecycleOwner) {
            discoverPublicationsFragment.initUIWithPublications(it)
        }
        viewModel.mutableCount.observe(viewLifecycleOwner){
            if(it == -1){
                binding.btnDiscoverPublicationsManagerLoadMorePublications.visibility = View.INVISIBLE
                binding.btnDiscoverPublicationsManagerLoadMorePublications.isEnabled = false
            }else if(it % 4 == 0){
                binding.btnDiscoverPublicationsManagerLoadMorePublications.visibility = View.VISIBLE
                binding.btnDiscoverPublicationsManagerLoadMorePublications.isEnabled = true
            }else{
                binding.btnDiscoverPublicationsManagerLoadMorePublications.visibility = View.INVISIBLE
                binding.btnDiscoverPublicationsManagerLoadMorePublications.isEnabled = false
            }
        }
        discoverPublicationsFragment.discoverPublicationRecyclerViewAdapter.setOnOpenClickListener(object :
            DiscoverPublicationsRecyclerViewAdapter.OnOpenClickListener {
            override fun onOpenClick(idPublication: Int) {
                (requireActivity() as MainActivity).openPublicationInformation(idPublication)
            }
        })

        binding.btnDiscoverPublicationsManagerLoadMorePublications.setOnClickListener {
            viewModel.showCount += 30
            viewModel.startGetAllPublications()
        }
        viewModel.startGetAllPublications()
    }
}
