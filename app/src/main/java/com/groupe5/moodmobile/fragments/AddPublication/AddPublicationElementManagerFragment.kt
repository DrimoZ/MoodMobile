package com.groupe5.moodmobile.fragments.AddPublication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import android.content.Context
import android.net.Uri
import com.groupe5.moodmobile.databinding.FragmentAddPublicationElementManagerBinding

class AddPublicationElementManagerFragment(image: MutableList<Uri>) : Fragment() {
    lateinit var binding: FragmentAddPublicationElementManagerBinding
    val image = image

    companion object {
        fun newInstance(image : MutableList<Uri>) = AddPublicationElementManagerFragment(image)
    }

    private lateinit var viewModel: AddPublicationElementManagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPublicationElementManagerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val token = prefs.getString("jwtToken", "") ?: ""

        viewModel = AddPublicationElementManagerViewModel(token)

        val addPublicationElementFragment = childFragmentManager
            .findFragmentById(R.id.fcb_addPublicationElement_list) as AddPublicationElementFragment?

        viewModel.startGetAllElement(image)
        viewModel.mutableElementLiveData.observe(viewLifecycleOwner) {
            if (addPublicationElementFragment != null) {
                addPublicationElementFragment.initUIWithElements(it)
            }
        }
        if (addPublicationElementFragment != null) {
            addPublicationElementFragment.addPublicationElementRecyclerViewAdapter.setOnDeleteClickListener(object :
                AddPublicationElementRecyclerViewAdapter.OnDeleteClickListener {
                override fun onDeleteClick(image: Uri) {
                    viewModel.deleteElement(image)
                    if (addPublicationElementFragment != null) {
                        addPublicationElementFragment.deleteCommentFromUI(image)
                    }
                }
            })
        }
    }
}
