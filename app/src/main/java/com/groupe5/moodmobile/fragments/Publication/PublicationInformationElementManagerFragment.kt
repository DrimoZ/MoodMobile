package com.groupe5.moodmobile.fragments.Publication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import android.content.Context
import com.groupe5.moodmobile.databinding.FragmentPublicationInformationContentManagerBinding
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPublicationInformation

class PublicationInformationElementManagerFragment(dto: DtoInputPublicationInformation) : Fragment() {
    lateinit var binding: FragmentPublicationInformationContentManagerBinding
    val dto = dto

    companion object {
        fun newInstance(dto : DtoInputPublicationInformation) = PublicationInformationElementManagerFragment(dto)
    }

    private lateinit var viewModel: PublicationInformationElementManagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPublicationInformationContentManagerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val token = prefs.getString("jwtToken", "") ?: ""

        viewModel = PublicationInformationElementManagerViewModel(token)

        val publicationInformationContentFragment = childFragmentManager
            .findFragmentById(R.id.fcb_publicationInformationElement_list) as PublicationInformationElementFragment

        viewModel.mutableElementLiveData.observe(viewLifecycleOwner) {
            publicationInformationContentFragment.initUIWithContents(it)
        }
        viewModel.startGetAllElement(dto)
    }
}
