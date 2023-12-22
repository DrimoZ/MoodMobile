package com.groupe5.moodmobile.fragments.More

import IUserRepository
import android.content.Context
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
import com.groupe5.moodmobile.services.UserService
import com.groupe5.moodmobile.utils.RetrofitFactory
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
            lifecycleScope.launch {
                deleteAccount()
            }
        }
        binding.btnDeleteAccountCancel.setOnClickListener {
            (requireActivity() as MainActivity).toggleDeleteAccountFragment(false)
        }

    }

    private suspend fun deleteAccount() {
        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val jwtToken = prefs.getString("jwtToken", "") ?: ""
        userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)
        try {
            val response = userRepository.deleteAccount()
            Toast.makeText(requireContext(), "Failed to delete profile", Toast.LENGTH_SHORT).show()
            Log.d("UpdateProfile", "Failed to delete profile : ${response}")
        } catch (e: Exception) {
            (requireActivity() as MainActivity).signOut()
            Toast.makeText(requireContext(), "Profile deleted successfully", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DeleteAccountFragment()
    }
}