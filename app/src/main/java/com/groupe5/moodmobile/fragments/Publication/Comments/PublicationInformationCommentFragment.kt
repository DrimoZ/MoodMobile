package com.groupe5.moodmobile.fragments.Publication.Comments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPubComment
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPubElement
import com.groupe5.moodmobile.fragments.Publication.Element.PublicationInformationElementRecyclerViewAdapter
import com.groupe5.moodmobile.placeholder.PlaceholderContent
class PublicationInformationCommentFragment : Fragment() {
    private val commentUI: ArrayList<DtoInputPubComment> = arrayListOf()
    lateinit var publicationInformationCommentRecyclerViewAdapter: PublicationInformationCommentRecyclerViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_publication_information_comment_list,
            container,
            false
        )

        // Set the adapter
        if (view is RecyclerView) {
            publicationInformationCommentRecyclerViewAdapter = PublicationInformationCommentRecyclerViewAdapter(requireContext(), commentUI)
            view.adapter = publicationInformationCommentRecyclerViewAdapter
        }
        return view
    }

    fun initUIWithComments(publications: List<DtoInputPubComment>?) {
        publications?.let {
            commentUI.clear()
            commentUI.addAll(it)
            publicationInformationCommentRecyclerViewAdapter.notifyDataSetChanged()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PublicationInformationCommentFragment()
    }
}