package com.groupe5.moodmobile.fragments.NewsFeed

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.activities.MainActivity
import com.groupe5.moodmobile.classes.SharedViewModel
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPublicationInformation
class NewsFeedPublicationFragment : Fragment() {
    private val publicationUI: ArrayList<DtoInputPublicationInformation> = arrayListOf()
    lateinit var newsFeedPublicationRecyclerViewAdapter: NewsFeedPublicationRecyclerViewAdapter
    lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news_feed_publication_list, container, false)

        // Initialize SharedViewModel
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Set the adapter
        if (view is RecyclerView) {
            newsFeedPublicationRecyclerViewAdapter =
                NewsFeedPublicationRecyclerViewAdapter(
                    requireContext(),
                    requireActivity() as MainActivity,
                    childFragmentManager,
                    publicationUI,
                    sharedViewModel,
                    viewLifecycleOwner
                )
            view.adapter = newsFeedPublicationRecyclerViewAdapter
        }

        return view
    }

    fun initUIWithPublications(publications: List<DtoInputPublicationInformation>?) {
        publications?.let {
            publicationUI.clear()
            publicationUI.addAll(it)
            newsFeedPublicationRecyclerViewAdapter.notifyDataSetChanged()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = NewsFeedPublicationFragment()
    }
}