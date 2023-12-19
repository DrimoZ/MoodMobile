package com.groupe5.moodmobile.fragments.Publication.Comments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import android.content.Context
import android.util.Log
import com.groupe5.moodmobile.databinding.FragmentPublicationInformationCommentManagerBinding
import com.groupe5.moodmobile.databinding.FragmentPublicationInformationContentManagerBinding
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPublicationInformation

class PublicationInformationCommentManagerFragment(id: Int) : Fragment() {
    lateinit var binding: FragmentPublicationInformationCommentManagerBinding
    val idPublication = id

    companion object {
        fun newInstance(id : Int) = PublicationInformationCommentManagerFragment(id)
    }

    private lateinit var viewModel: PublicationInformationCommentManagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPublicationInformationCommentManagerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val token = prefs.getString("jwtToken", "") ?: ""

        viewModel = PublicationInformationCommentManagerViewModel(token)

        val publicationInformationCommentFragment = childFragmentManager
            .findFragmentById(R.id.fcb_publicationInformationComments_list) as PublicationInformationCommentFragment

        /*viewModel.mutableCommentLiveData.observe(viewLifecycleOwner) {
            publicationInformationCommentFragment.initUIWithComments(it)
        }*/
        viewModel.mutableCommentLiveData.observe(viewLifecycleOwner) { comments ->
            if (comments.size != 0) {
                binding.tvPublicationInformationCommentsNoComment.visibility = View.INVISIBLE
                publicationInformationCommentFragment.initUIWithComments(comments)
            } else {
                binding.tvPublicationInformationCommentsNoComment.visibility = View.VISIBLE
            }
        }
        viewModel.startGetAllComment(idPublication)
    }
}
