package com.groupe5.moodmobile.fragments.UserProfile.UserPublications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.databinding.FragmentProfilePublicationManagerBinding
import com.groupe5.moodmobile.R
import android.content.Context
import com.groupe5.moodmobile.activities.MainActivity

class ProfilePublicationManagerFragment : Fragment() {
    lateinit var binding: FragmentProfilePublicationManagerBinding

    companion object {
        fun newInstance(friendId: String): ProfilePublicationManagerFragment {
            val fragment = ProfilePublicationManagerFragment()
            val args = Bundle()
            args.putString("friendId", friendId)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: ProfilePublicationManagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilePublicationManagerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val friendId = arguments?.getString("friendId")
        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val token = prefs.getString("jwtToken", "") ?: ""

        viewModel = ProfilePublicationManagerViewModel(token)

        val profilePublicationsFragment = childFragmentManager
            .findFragmentById(R.id.fcb_profilePublicationManager_list) as ProfilePublicationsFragment

        viewModel.mutablePublicationLiveData.observe(viewLifecycleOwner) {
            profilePublicationsFragment.initUIWithPublications(it)
        }
        viewModel.isPublicationsPublicLiveData.observe(viewLifecycleOwner) { isPublic ->
            if (!isPublic) {
                binding.tvFragmentProfilePublicationManagerPrivatePublications.visibility = View.VISIBLE
            }
        }
        viewModel.startGetAllPublications(if (friendId.isNullOrEmpty()) null else friendId)

        profilePublicationsFragment.profilePublicationRecyclerViewAdapter.setOnOpenClickListener(object :
            ProfilePublicationsRecyclerViewAdapter.OnOpenClickListener {
            override fun onOpenClick(idPublication: Int) {
               (requireActivity() as MainActivity).openPublicationInformation(idPublication)
            }
        })
    }
}
