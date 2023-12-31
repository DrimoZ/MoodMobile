package com.groupe5.moodmobile.fragments.UserProfile.UserPublications

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPublication

class ProfilePublicationsFragment : Fragment() {
    private val publicationUI: ArrayList<DtoInputPublication> = arrayListOf()
    lateinit var profilePublicationRecyclerViewAdapter: ProfilePublicationsRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.profile_publication_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            profilePublicationRecyclerViewAdapter = ProfilePublicationsRecyclerViewAdapter(requireContext(), publicationUI)
            view.adapter = profilePublicationRecyclerViewAdapter
        }
        return view
    }

    fun initUIWithPublications(publications: List<DtoInputPublication>?) {
        publications?.let {
            publicationUI.clear()
            publicationUI.addAll(it)
            profilePublicationRecyclerViewAdapter.notifyDataSetChanged()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfilePublicationsFragment()
    }
}
