package com.groupe5.moodmobile.fragments.UserProfile

import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.classes.SharedViewModel
import com.groupe5.moodmobile.databinding.FragmentProfileBinding
import com.groupe5.moodmobile.dtos.Image.DtoInputImage
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserProfile
import com.groupe5.moodmobile.repositories.IImageRepository
import com.groupe5.moodmobile.repositories.IUserRepository
import com.groupe5.moodmobile.services.ImageService
import com.groupe5.moodmobile.utils.RetrofitFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.UUID


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var userRepository: IUserRepository
    private lateinit var imageRepository: IImageRepository
    private lateinit var imageService: ImageService
    private lateinit var sharedViewModel: SharedViewModel

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
            startUserData()
        })

        startUserData()

        binding.btnFragmentProfilePublications.setOnClickListener {
            replaceFragment(ProfilePublicationManagerFragment.newInstance(""))
        }

        binding.btnFragmentProfileFriends.setOnClickListener {
            replaceFragment(ProfileFriendManagerFragment.newInstance(""))
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fcb_profilePublicationManager_list, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun startUserData(){
        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val jwtToken = prefs.getString("jwtToken", "") ?: ""
        userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)
        imageRepository = RetrofitFactory.create(jwtToken, IImageRepository::class.java)
        imageService = ImageService(requireContext(), imageRepository)

        // Call the API to get the user's ID and role
        val call1 = userRepository.getUserIdAndRole()
        call1.enqueue(object : Callback<DtoInputUserIdAndRole> {
            override fun onResponse(call: Call<DtoInputUserIdAndRole>, response: Response<DtoInputUserIdAndRole>) {
                if (response.isSuccessful) {
                    val userId = response.body()?.userId
                    userId?.let {
                        val call2 = userRepository.getUserProfile(it)
                        call2.enqueue(object : Callback<DtoInputUserProfile> {
                            override fun onResponse(call: Call<DtoInputUserProfile>, response: Response<DtoInputUserProfile>) {
                                if (response.isSuccessful) {
                                    val userProfile = response.body()
                                    val imageId = response.body()?.idImage
                                    imageId?.let { id ->
                                        CoroutineScope(Dispatchers.Main).launch {
                                            val image = imageService.getImageById(id)
                                            if (image.startsWith("@drawable/")) {
                                                val resourceId = resources.getIdentifier(
                                                    image.substringAfterLast('/'),
                                                    "drawable",
                                                    image
                                                )
                                                Picasso.with(binding.ivFragmentProfileUserImage.context).load(resourceId).into(binding.ivFragmentProfileUserImage)
                                            } else {
                                                Picasso.with(binding.ivFragmentProfileUserImage.context).load(image).into(binding.ivFragmentProfileUserImage)
                                            }
                                        }
                                    }
                                    // Update TextViews with profile data
                                    binding.tvFragmentProfileUserUsername.text = userProfile?.name
                                    binding.tvFragmentProfileUserNbPublications.text = "Publications: ${userProfile?.publicationCount}"
                                    binding.tvFragmentProfileUserNbFriends.text = "Friends: ${userProfile?.friendCount}"
                                    binding.tvFragmentProfileUserDescription.text = userProfile?.description
                                }
                            }

                            override fun onFailure(call: Call<DtoInputUserProfile>, t: Throwable) {
                                val message = "Echec DB: ${t.message}"
                                Log.e("EchecDb", message, t)
                            }
                        })
                    }
                } else {
                    val message = "echec : ${response.message()}"
                    Log.d("Echec", message)
                }
            }

            override fun onFailure(call: Call<DtoInputUserIdAndRole>, t: Throwable) {
                val message = "Echec DB: ${t.message}"
                Log.e("EchecDb", message, t)
            }
        })

    }

    fun imageToURL(dto: DtoInputImage): String {
        val imageData = dto.data
        val decodedBytes: ByteArray = Base64.decode(imageData, Base64.DEFAULT)

        // Créez un fichier dans le répertoire des fichiers temporaires de l'application
        val directory = File(requireContext().filesDir, "images")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        // Générez un nom de fichier aléatoire
        val randomFileName = UUID.randomUUID().toString() + ".jpg"
        val imageFile = File(directory, randomFileName)

        // Écrivez les octets dans le fichier image
        try {
            FileOutputStream(imageFile).use { stream ->
                stream.write(decodedBytes)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Obtenez l'URL du fichier
        val imageUrl: String = imageFile.toURI().toURL().toString()

        // Affichez l'URL de l'image
        println("URL de l'image: $imageUrl")

        return imageUrl
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}
