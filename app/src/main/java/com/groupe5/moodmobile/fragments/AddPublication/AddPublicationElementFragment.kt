package com.groupe5.moodmobile.fragments.AddPublication

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.classes.SharedViewModel
import com.groupe5.moodmobile.dtos.Image.DtoInputImage
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPubComment
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPubElement

class AddPublicationElementFragment : Fragment() {
    private val elementUI: ArrayList<Uri> = arrayListOf()
    lateinit var addPublicationElementRecyclerViewAdapter: AddPublicationElementRecyclerViewAdapter
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.publication_information_element_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            addPublicationElementRecyclerViewAdapter =
                AddPublicationElementRecyclerViewAdapter(requireContext(), elementUI)
            view.adapter = addPublicationElementRecyclerViewAdapter
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    fun initUIWithElements(images: MutableList<Uri>) {
        images?.let {
            elementUI.clear()
            elementUI.addAll(it)
            addPublicationElementRecyclerViewAdapter.notifyDataSetChanged()
        }
    }

    fun deleteCommentFromUI(image: Uri) {
        elementUI.remove(image)
        addPublicationElementRecyclerViewAdapter.notifyDataSetChanged()
        val list = sharedViewModel.mutableElementLiveData.value
        if (list != null) {
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                val it = iterator.next()
                if (image == it) {
                    iterator.remove()
                }
            }
            sharedViewModel.mutableElementLiveData.value = list
        }
    }
    companion object {
        @JvmStatic
        fun newInstance() = AddPublicationElementFragment()
    }
}