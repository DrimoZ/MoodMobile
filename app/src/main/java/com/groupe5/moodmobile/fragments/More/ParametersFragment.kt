package com.groupe5.moodmobile.fragments.More

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.groupe5.moodmobile.databinding.FragmentParametersBinding
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserAccount
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserProfile
import com.groupe5.moodmobile.dtos.Users.Output.DtoOutputUserAccount
import com.groupe5.moodmobile.repositories.IUserRepository
import com.groupe5.moodmobile.utils.RetrofitFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            updateUserAccount()
        }
    }

    private fun updateUserAccount() {
        val updatedUsername = binding.etFragmentParametersUsername.text.toString()
        val updatedEmailAddress = binding.etFragmentParametersEmailAddress.text.toString()
        val updatedBirthdate = binding.etFragmentParametersBirthdate.text.toString()
        val updatedTitle = binding.etFragmentParametersTitle.text.toString()
        val updatedDescription = binding.etFragmentParametersDescription.text.toString()
        val updatedLogin = binding.etFragmentParametersLogin.text.toString()
        val updatedPhoneNumber = binding.etFragmentParametersPhoneNumber.text.toString()

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
                    Toast.makeText(requireContext(), "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Échec de la mise à jour du profil", Toast.LENGTH_SHORT).show()
                    Log.d("UpdateProfile", "Échec de la mise à jour du profil : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur lors de la mise à jour du profil", Toast.LENGTH_SHORT).show()
                Log.e("UpdateProfile", "Erreur lors de la mise à jour du profil : ${t.message}", t)
            }
        })
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
                        val commonCall = userRepository.getUserAccount(it)
                        commonCall.enqueue(object : Callback<DtoInputUserAccount> {
                            override fun onResponse(call: Call<DtoInputUserAccount>, response: Response<DtoInputUserAccount>) {
                                if (response.isSuccessful) {
                                    val userAccount = response.body()
                                    Log.e("",""+userAccount)
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

                                        binding.etFragmentParametersLogin.setText(userAccount.login)
                                        binding.etFragmentParametersPhoneNumber.setText(userAccount.phoneNumber)
                                    }
                                }
                            }
                            override fun onFailure(call: Call<DtoInputUserAccount>, t: Throwable) {
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


    companion object {
        @JvmStatic
        fun newInstance() = ParametersFragment()
    }
}