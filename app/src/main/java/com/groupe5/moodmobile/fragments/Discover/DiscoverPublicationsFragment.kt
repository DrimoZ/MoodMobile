package com.groupe5.moodmobile.fragments.Discover

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPublication

class DiscoverPublicationsFragment : Fragment() {
    private val publicationUI: ArrayList<DtoInputPublication> = arrayListOf()
    private lateinit var discoverPublicationRecyclerViewAdapter: DiscoverPublicationsRecyclerViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_discover_publications_list, container, false)

        if (view is RecyclerView) {
            discoverPublicationRecyclerViewAdapter = DiscoverPublicationsRecyclerViewAdapter(requireContext(), publicationUI)
            view.adapter = discoverPublicationRecyclerViewAdapter
        }
        return view
    }

    fun initUIWithPublications(publications: List<DtoInputPublication>?) {
        publications?.let {
            publicationUI.clear()
            publicationUI.addAll(it)
            discoverPublicationRecyclerViewAdapter.notifyDataSetChanged()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DiscoverPublicationsFragment()
    }
}