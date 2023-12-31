package com.groupe5.moodmobile.fragments.UserProfile

import IUserRepository
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.dhaval2404.imagepicker.ImagePicker
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.activities.MainActivity
import com.groupe5.moodmobile.classes.SharedViewModel
import com.groupe5.moodmobile.databinding.FragmentProfileBinding
import com.groupe5.moodmobile.fragments.UserProfile.UserFriends.ProfileFriendManagerFragment
import com.groupe5.moodmobile.fragments.UserProfile.UserPublications.ProfilePublicationManagerFragment
import com.groupe5.moodmobile.repositories.IImageRepository
import com.groupe5.moodmobile.services.ImageService
import com.groupe5.moodmobile.utils.RetrofitFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var userRepository: IUserRepository
    private lateinit var imageRepository: IImageRepository
    private lateinit var imageService: ImageService
    private lateinit var sharedViewModel: SharedViewModel

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data

                imageUri?.let {
                    val imageFile = File(it.path)

                    val requestFile: RequestBody =
                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), imageFile)

                    val imagePart: MultipartBody.Part =
                        MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

                    val call = imageRepository.setUserProfileImage(imagePart)

                    call.enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            Toast.makeText(requireContext(), "Photo updated successfully", Toast.LENGTH_SHORT).show()
                            (requireActivity() as MainActivity).onRefreshUserProfile()
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e("ImagePickerLauncher", "Failed to upload image", t)
                        }
                    })
                }
            } else {
                Log.e("ImagePickerLauncher", "Image picking canceled or failed")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.friendData.observe(viewLifecycleOwner, Observer { userProfile ->
            lifecycleScope.launch {
                startUserData()
            }
        })

        lifecycleScope.launch {
            startUserData()
        }

        binding.btnFragmentProfilePublications.setOnClickListener {
            replaceFragment(ProfilePublicationManagerFragment.newInstance(""))
        }

        binding.btnFragmentProfileFriends.setOnClickListener {
            replaceFragment(ProfileFriendManagerFragment.newInstance(""))
        }

        binding.btnFragmentProfileChangeImage.setOnClickListener {
            val image = ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(500, 500)
                .createIntent { intent ->
                    imagePickerLauncher.launch(intent)
                }
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fcb_profilePublicationManager_list, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private suspend fun startUserData() {
        try {
            val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
            val jwtToken = prefs.getString("jwtToken", "") ?: ""
            userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)
            imageRepository = RetrofitFactory.create(jwtToken, IImageRepository::class.java)
            imageService = ImageService(requireContext(), imageRepository)

            val userIdAndRole = userRepository.getUserIdAndRole()

            userIdAndRole.userId?.let { userId ->
                val userProfile = userRepository.getUserProfile(userId)

                val imageId = userProfile.imageId
                imageId?.let { id ->
                    CoroutineScope(Dispatchers.Main).launch {
                        val image = imageService.getImageById(id)
                        if (image.startsWith("@drawable/")) {
                            val resourceId = resources.getIdentifier(
                                image.substringAfterLast('/'),
                                "drawable",
                                "com.groupe5.moodmobile"
                            )
                            Picasso.with(binding.ivFragmentProfileUserImage.context).load(resourceId).into(binding.ivFragmentProfileUserImage)
                        } else {
                            Picasso.with(binding.ivFragmentProfileUserImage.context).load(image).into(binding.ivFragmentProfileUserImage)
                        }
                    }
                }
                binding.tvFragmentProfileUserUsername.text = userProfile.userName
                binding.tvFragmentProfileUserNbPublications.text = "Publications: ${userProfile.publicationCount}"
                binding.tvFragmentProfileUserNbFriends.text = "Friends: ${userProfile.friendCount}"
                binding.tvFragmentProfileUserDescription.text = userProfile.accountDescription
            }
        } catch (e: Exception) {
            val message = "Echec: ${e.message}"
            Log.e("Echec", message, e)
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}
