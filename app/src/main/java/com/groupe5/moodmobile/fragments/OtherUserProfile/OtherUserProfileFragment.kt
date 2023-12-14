package com.groupe5.moodmobile.fragments.OtherUserProfile

import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.databinding.FragmentOtherUserProfileBinding
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.dtos.Image.DtoInputImage
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserProfile
import com.groupe5.moodmobile.fragments.UserProfile.ProfileFriendManagerFragment
import com.groupe5.moodmobile.fragments.UserProfile.ProfilePublicationManagerFragment
import com.groupe5.moodmobile.repositories.IImageRepository
import com.groupe5.moodmobile.repositories.IUserRepository
import com.groupe5.moodmobile.utils.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class OtherUserProfileFragment(friend: DtoInputFriend) : Fragment() {
    val friend = friend
    private lateinit var binding: FragmentOtherUserProfileBinding
    private lateinit var userRepository: IUserRepository
    private lateinit var imageRepository: IImageRepository
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOtherUserProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startUserData(friend)

        binding.btnFragmentProfilePublications.setOnClickListener {
            replaceFragment(ProfilePublicationManagerFragment.newInstance())
        }

        binding.btnFragmentProfileFriends.setOnClickListener {
            replaceFragment(ProfileFriendManagerFragment.newInstance())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fcb_profilePublicationManager_list, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun startUserData(friend: DtoInputFriend){
        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val jwtToken = prefs.getString("jwtToken", "") ?: ""
        userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)

        val userId = friend.id
        val call = userRepository.getUserProfile(userId)
        call.enqueue(object : Callback<DtoInputUserProfile> {
            override fun onResponse(call: Call<DtoInputUserProfile>, response: Response<DtoInputUserProfile>) {
                if (response.isSuccessful) {
                    val userProfile = response.body()
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
        fun newInstance(friend: DtoInputFriend) = OtherUserProfileFragment(friend)
    }
}