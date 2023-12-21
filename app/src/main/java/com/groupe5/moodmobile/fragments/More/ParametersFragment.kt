package com.groupe5.moodmobile.fragments.More

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.groupe5.moodmobile.activities.MainActivity
import com.groupe5.moodmobile.databinding.FragmentParametersBinding
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserAccount
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserPrivacy
import com.groupe5.moodmobile.dtos.Users.Output.DtoOutputUserAccount
import com.groupe5.moodmobile.dtos.Users.Output.DtoOutputUserPassword
import com.groupe5.moodmobile.dtos.Users.Output.DtoOutputUserPrivacy
import com.groupe5.moodmobile.repositories.IUserRepository
import com.groupe5.moodmobile.utils.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ParametersFragment : Fragment() {
    private lateinit var binding: FragmentParametersBinding
    private lateinit var userRepository: IUserRepository
    private var idUser = "null"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParametersBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startUserData()

        binding.btnFragmentParametersEditProfileCommonInformation.setOnClickListener {
            updateUserAccount()
        }
        binding.btnFragmentParametersEditProfilePersonalInformation.setOnClickListener {
            updateUserPassword()
        }
        binding.switchFragmentParametersMakeAccountPrivate.setOnClickListener {
            updateUserPrivacy("isPublic", false)
        }
        binding.switchFragmentParametersMakePostsPrivate.setOnClickListener {
            updateUserPrivacy("isPublicationPublic", false)
        }
        binding.switchFragmentParametersMakeFriendsListPrivate.setOnClickListener {
            updateUserPrivacy("isFriendPublic", false)
        }
        binding.btnFragmentParametersDeleteAccount.setOnClickListener {
            deleteAccount()
        }
    }

    private fun deleteAccount() {
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

    private fun updateUserPrivacy(switch: String, all: Boolean) {
        var pathPrivacy = "isPublic"
        var valuePrivacy = true
        when (switch) {
            "isPublic" -> {
                pathPrivacy = "isPublic"
                valuePrivacy = binding.switchFragmentParametersMakeAccountPrivate.isChecked.not()
                if(!all){
                    binding.switchFragmentParametersMakeFriendsListPrivate.isChecked = valuePrivacy.not()
                    updateUserPrivacy("isFriendPublic", false)
                    binding.switchFragmentParametersMakePostsPrivate.isChecked = valuePrivacy.not()
                    updateUserPrivacy("isPublicationPublic", false)
                }

            }
            "isPublicationPublic" -> {
                pathPrivacy = "isPublicationPublic"
                valuePrivacy = binding.switchFragmentParametersMakePostsPrivate.isChecked.not()
                if(binding.switchFragmentParametersMakeAccountPrivate.isChecked || binding.switchFragmentParametersMakePostsPrivate.isChecked && binding.switchFragmentParametersMakeFriendsListPrivate.isChecked){
                    binding.switchFragmentParametersMakeAccountPrivate.isChecked = valuePrivacy.not()
                    updateUserPrivacy("isPublic", true)
                }

            }
            "isFriendPublic" -> {
                pathPrivacy = "isFriendPublic"
                valuePrivacy = binding.switchFragmentParametersMakeFriendsListPrivate.isChecked.not()
                if(binding.switchFragmentParametersMakeAccountPrivate.isChecked || binding.switchFragmentParametersMakePostsPrivate.isChecked && binding.switchFragmentParametersMakeFriendsListPrivate.isChecked){
                    binding.switchFragmentParametersMakeAccountPrivate.isChecked = valuePrivacy.not()
                    updateUserPrivacy("isPublic", true)
                }
            }
        }
        val updatedUserPrivacy = DtoOutputUserPrivacy(
            path = pathPrivacy,
            value = valuePrivacy
        )
        val updateCall = userRepository.setUserPrivacy(updatedUserPrivacy)
        updateCall.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                    Log.d("UpdateProfile", "Failed to update profile : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error updating profile", Toast.LENGTH_SHORT).show()
                Log.e("UpdateProfile", "Error updating profile : ${t.message}", t)
            }
        })

    }

    private fun updateUserAccount() {
        val updatedUsername = binding.etFragmentParametersUsername.text.toString()
        val updatedEmailAddress = binding.etFragmentParametersEmailAddress.text.toString()
        val updatedBirthdate = binding.etFragmentParametersBirthdate.text.toString()
        val updatedTitle = binding.etFragmentParametersTitle.text.toString()
        val updatedDescription = binding.etFragmentParametersDescription.text.toString()

        val birthDate = parseDateString(updatedBirthdate) ?: Date()
        val updatedUserAccount = DtoOutputUserAccount(
            id = idUser,
            name = updatedUsername,
            title = updatedTitle,
            mail = updatedEmailAddress,
            birthDate = birthDate,
            description = updatedDescription
        )

        val updateCall = userRepository.setUserAccount(updatedUserAccount)
        updateCall.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                    Log.d("UpdateProfile", "Failed to update profile : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error updating profile", Toast.LENGTH_SHORT).show()
                Log.e("UpdateProfile", "Error updating profile : ${t.message}", t)
            }
        })
    }

    private fun updateUserPassword() {
        val oldPassword = binding.etFragmentParametersOldPassword.text.toString()
        val newPassword = binding.etFragmentParametersNewPassword.text.toString()
        val newPasswordConfirmation = binding.etFragmentParametersNewPasswordConfirmation.text.toString()

        if (newPassword != newPasswordConfirmation) {
            Toast.makeText(requireContext(), "New passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isPasswordValid(newPassword)) {
            Toast.makeText(requireContext(), "Invalid password format", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedUserPassword = DtoOutputUserPassword(
            oldPassword = oldPassword,
            newPassword = newPassword
        )

        val updateCall = userRepository.setUserPassword(updatedUserPassword)
        updateCall.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to update password", Toast.LENGTH_SHORT).show()
                    Log.d("UpdateProfile", "Failed to update password : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error updating password", Toast.LENGTH_SHORT).show()
                Log.e("UpdateProfile", "Error updating password : ${t.message}", t)
            }
        })
    }

    private fun isPasswordValid(password: String): Boolean {
        val minLength = 8
        val maxLength = 48

        val hasUpperCase = Regex("(?=.*[A-Z])").containsMatchIn(password)
        val hasLowerCase = Regex("(?=.*[a-z])").containsMatchIn(password)
        val hasDigit = Regex("(?=.*[0-9])").containsMatchIn(password)
        val hasSpecialChar = Regex("(?=.*[!@#$%^&*()_+=\\[\\]{};:<>|./?,\\-])").containsMatchIn(password)

        return password.length in minLength..maxLength &&
                hasUpperCase &&
                hasLowerCase &&
                hasDigit &&
                hasSpecialChar
    }

    private fun parseDateString(dateString: String): Date? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return try {
            inputFormat.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    private fun startUserData() {
        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val jwtToken = prefs.getString("jwtToken", "") ?: ""
        userRepository = RetrofitFactory.create(jwtToken, IUserRepository::class.java)
        val idCall = userRepository.getUserIdAndRole()
        idCall.enqueue(object : Callback<DtoInputUserIdAndRole> {
            override fun onResponse(call: Call<DtoInputUserIdAndRole>, response: Response<DtoInputUserIdAndRole>) {
                if (response.isSuccessful) {
                    val userId = response.body()?.userId
                    userId?.let {
                        idUser = userId
                        getUserAccount()
                        getUserPrivacy()
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

    fun getUserAccount(){
        val commonCall = userRepository.getUserAccount(idUser)
        commonCall.enqueue(object : Callback<DtoInputUserAccount> {
            override fun onResponse(call: Call<DtoInputUserAccount>, response: Response<DtoInputUserAccount>) {
                if (response.isSuccessful) {
                    val userAccount = response.body()
                    if (userAccount != null) {
                        val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
                        val outputFormat = SimpleDateFormat("yyyy-MM-dd")
                        val date = inputFormat.parse(userAccount.birthDate.toString())
                        val birthdate = outputFormat.format(date)
                        binding.etFragmentParametersUsername.setText(userAccount.name)
                        binding.etFragmentParametersEmailAddress.setText(userAccount.mail)
                        binding.etFragmentParametersBirthdate.setText(birthdate)
                        binding.etFragmentParametersTitle.setText(userAccount.title)
                        binding.etFragmentParametersDescription.setText(userAccount.description)
                    }
                }
            }
            override fun onFailure(call: Call<DtoInputUserAccount>, t: Throwable) {
                val message = "Echec DB: ${t.message}"
                Log.e("EchecDb", message, t)
            }
        })
    }
    fun getUserPrivacy(){
        val privacyCall = userRepository.getUserPrivacy()
        privacyCall.enqueue(object : Callback<DtoInputUserPrivacy> {
            override fun onResponse(call: Call<DtoInputUserPrivacy>, response: Response<DtoInputUserPrivacy>) {
                if (response.isSuccessful) {
                    val userPrivacy = response.body()
                    if (userPrivacy != null) {
                        binding.switchFragmentParametersMakeAccountPrivate.isChecked = userPrivacy.isPublic.not()
                        binding.switchFragmentParametersMakeFriendsListPrivate.isChecked = userPrivacy.isFriendPublic.not()
                        binding.switchFragmentParametersMakePostsPrivate.isChecked = userPrivacy.isPublicationPublic.not()
                    }
                }
            }
            override fun onFailure(call: Call<DtoInputUserPrivacy>, t: Throwable) {
                val message = "Echec DB: ${t.message}"
                Log.e("EchecDb", message, t)
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ParametersFragment()
    }
}