package com.groupe5.moodmobile.fragments.Publication.Comments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.groupe5.moodmobile.classes.SharedViewModel
import com.groupe5.moodmobile.databinding.FragmentPublicationInformationCommentManagerBinding
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPubComment
import com.groupe5.moodmobile.fragments.Publication.PublicationInformationFragment

class PublicationInformationCommentManagerFragment(id: Int) : Fragment() {
    lateinit var binding: FragmentPublicationInformationCommentManagerBinding
    private lateinit var sharedViewModel: SharedViewModel
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


        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        val publicationInformationCommentFragment = childFragmentManager
            .findFragmentById(R.id.fcb_publicationInformationComments_list) as PublicationInformationCommentFragment

        viewModel.mutableCommentLiveData.observe(viewLifecycleOwner) { comments ->
            if (comments.size != 0) {
                //binding.tvPublicationInformationCommentsNoComment.visibility = View.INVISIBLE
                publicationInformationCommentFragment.initUIWithComments(comments)
            } else {
                //binding.tvPublicationInformationCommentsNoComment.visibility = View.VISIBLE
            }
        }
        publicationInformationCommentFragment.publicationInformationCommentRecyclerViewAdapter.setOnDeleteClickListener(object :
            PublicationInformationCommentRecyclerViewAdapter.OnDeleteClickListener {
            override fun onDeleteClick(dto: DtoInputPubComment) {
                viewModel.deleteFriend(dto)
                publicationInformationCommentFragment.deleteCommentFromUI(dto)
                val currentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerView_mainActivitySpecialPublication)

                if (currentFragment is PublicationInformationFragment) {
                    currentFragment.removePublicationComment()
                }else{
                    sharedViewModel.numberCommentAfterDelete.value = idPublication
                }
            }
        })
        viewModel.startGetAllComment(idPublication)
    }
}
