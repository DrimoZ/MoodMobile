package com.groupe5.moodmobile.fragments.More

import IUserRepository
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.activities.MainActivity
import com.groupe5.moodmobile.databinding.FragmentDeleteAccountBinding
import com.groupe5.moodmobile.databinding.FragmentParametersBinding
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteAccountFragment : Fragment() {
    private lateinit var binding: FragmentDeleteAccountBinding
    private lateinit var userRepository: IUserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeleteAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnDeleteAccountDelete.setOnClickListener {
            deleteAccount()
        }
        binding.btnDeleteAccountCancel.setOnClickListener {
            val frag = (requireActivity() as MainActivity).supportFragmentManager.findFragmentById(R.id.fragmentContainerView_mainActivity_parameters)
            if (frag is ParametersFragment) {
                frag.hideDeleteAccountFragment()
            }
        }

    }

    private fun deleteAccount() {
        childFragmentManager
            .beginTransaction()
            .add(
                R.id.fcv_fragmentParameters_deleteAccount,
                DeleteAccountFragment.newInstance(),
                "DeleteAccountFragment"
            )
            .commit()
        val deleteCall = userRepository.deleteAccount()
        deleteCall.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    (requireActivity() as MainActivity).signOut()
                    Toast.makeText(requireContext(), "Profile deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to delete profile", Toast.LENGTH_SHORT).show()
                    Log.d("UpdateProfile", "Failed to delete profile : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error deleting profile", Toast.LENGTH_SHORT).show()
                Log.e("UpdateProfile", "Error deleting profile : ${t.message}", t)
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = DeleteAccountFragment()
    }
}