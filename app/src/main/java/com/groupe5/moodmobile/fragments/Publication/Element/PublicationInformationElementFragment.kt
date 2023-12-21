package com.groupe5.moodmobile.fragments.Publication.Element

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPubElement

class PublicationInformationElementFragment : Fragment() {
    private val elementUI: ArrayList<DtoInputPubElement> = arrayListOf()
    lateinit var publicationInformationElementRecyclerViewAdapter: PublicationInformationElementRecyclerViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.publication_information_element_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            publicationInformationElementRecyclerViewAdapter = PublicationInformationElementRecyclerViewAdapter(requireContext(), elementUI)
            view.adapter = publicationInformationElementRecyclerViewAdapter
        }
        return view
    }
    fun initUIWithElements(publications: List<DtoInputPubElement>?) {
        publications?.let {
            elementUI.clear()
            elementUI.addAll(it)
            publicationInformationElementRecyclerViewAdapter.notifyDataSetChanged()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PublicationInformationElementFragment()
    }
}