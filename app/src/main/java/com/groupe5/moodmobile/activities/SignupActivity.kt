package com.groupe5.moodmobile.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.groupe5.moodmobile.databinding.ActivitySignupBinding
import com.groupe5.moodmobile.dtos.Users.Output.DtoOutputUserSignup
import com.groupe5.moodmobile.repositories.IAuthenticationRepository
import com.groupe5.moodmobile.utils.RetrofitFactory
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var authenticationRepository: IAuthenticationRepository
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("mood", Context.MODE_PRIVATE)
        authenticationRepository = RetrofitFactory.create(prefs.getString("jwtToken", "") ?: "", IAuthenticationRepository::class.java)

        binding.btnSignUpSignUp.setOnClickListener {
            with(binding) {
                val name = etSignupFullName.text.toString()
                val login = etSignupUsername.text.toString()
                val mail = etSignupEmail.text.toString()
                val birthdate = etSignupBirthdate.text.toString()
                val password = etSignupPassword.text.toString()
                val passwordConfirmation = etSignupPasswordConfirmation.text.toString()

                if (validateForm(name, login, mail, birthdate, password, passwordConfirmation)) {
                    submitForm(name, login, mail, birthdate, password, passwordConfirmation)
                } else {
                    Toast.makeText(this@SignupActivity, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvSignupLink.setOnClickListener {
            navigateToSigninActivity()
        }
    }

    private fun submitForm(name: String, login: String, mail: String, birthdate: String, password: String, passwordConfirmation: String) {
        lifecycleScope.launch {
            try {
                authenticationRepository.signUpUser(DtoOutputUserSignup(name, login, mail, birthdate, password)).let { response ->
                    if (response.isSuccessful) {
                        response.headers().get("Set-Cookie")?.let { cookieHeader ->
                            extractTokenFromCookie(cookieHeader)?.let { token ->
                                updateTokenInPreferences(token)
                            }
                        }
                        navigateToMainActivity()
                    } else {
                        handleAuthFailure(response.code(), response.errorBody()?.string())
                    }
                }
            } catch (e: HttpException) {
                handleAuthFailure(e.code(), e.message())
            } catch (e: Exception) {
                handleDbFailure(e.message)
            }
        }
    }

    private fun extractTokenFromCookie(cookieHeader: String): String? =
        Pattern.compile("MoodSession=([^;]+)").matcher(cookieHeader).takeIf { it.find() }?.group(1)

    private fun validateForm(name: String, login: String, mail: String, birthdate: String, password: String, passwordConfirmation: String): Boolean =
        name.isNotEmpty() &&
                login.isNotEmpty() && login.length >= 8 &&
                mail.isNotEmpty() &&
                birthdate.isNotEmpty() &&
                isPasswordValid(password) &&
                isPasswordValid(passwordConfirmation)

    private fun isPasswordValid(password: String): Boolean =
        Pattern.compile("^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*()_+=\\[\\]{};:<>|.\\/?,-]).+$")
            .matcher(password).matches()

    private fun updateTokenInPreferences(newToken: String) {
        getSharedPreferences("mood", MODE_PRIVATE).edit().apply {
            putString("jwtToken", newToken)
            apply()
        }
    }

    private fun handleAuthFailure(responseCode: Int, errorBody: String?) {
        Log.d("EchecAuth", "Error body: $errorBody")
        if (responseCode == 409) {
            Log.d("EchecAuth", "Conflict: Username or mail already used")
            runOnUiThread {
                Toast.makeText(
                    this@SignupActivity,
                    "Username and email already exist.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleDbFailure(message: String?) {
        Log.e("EchecDb", "Echec DB: $message")
    }

    private fun navigateToSigninActivity() {
        startActivity(Intent(this@SignupActivity, SigninActivity::class.java))
        finish()
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this@SignupActivity, MainActivity::class.java))
    }
}