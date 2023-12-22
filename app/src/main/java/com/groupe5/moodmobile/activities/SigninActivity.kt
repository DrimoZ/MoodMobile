package com.groupe5.moodmobile.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.groupe5.moodmobile.databinding.ActivitySigninBinding
import com.groupe5.moodmobile.dtos.Users.Output.DtoOutputUserSignin
import com.groupe5.moodmobile.repositories.IAuthenticationRepository
import com.groupe5.moodmobile.utils.RetrofitFactory
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SigninActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding
    private lateinit var jwtToken: String
    private lateinit var authenticationRepository: IAuthenticationRepository
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("mood", Context.MODE_PRIVATE)
        jwtToken = prefs.getString("jwtToken", "") ?: ""
        authenticationRepository = RetrofitFactory.create(jwtToken, IAuthenticationRepository::class.java)

        binding.btnSignInSignIn.setOnClickListener {
            submitForm(
                binding.etSigninLogin.text.toString(),
                binding.etSigninPassword.text.toString(),
                true
            )
        }

        binding.tvSigninLink.setOnClickListener {
            navigateToSignupActivity()
        }
    }

    private fun submitForm(login: String, password: String, stayLoggedIn: Boolean) {
        lifecycleScope.launch {
            try {
                authenticationRepository.signInUser(DtoOutputUserSignin(login, password, stayLoggedIn)).let { response ->
                    if (response.isSuccessful) {
                        response.headers().get("Set-Cookie")?.let { cookieHeader ->
                            extractTokenFromCookie(cookieHeader)?.let { token ->
                                updateTokenInPreferences(token)
                            }
                        }
                        navigateToMainActivity()
                    } else {
                        Toast.makeText(this@SigninActivity, "Username or Password incorrect", Toast.LENGTH_SHORT).show()
                        binding.etSigninLogin.text.clear()
                        binding.etSigninPassword.text.clear()
                        Log.d("EchecAuth", "Echec Auth!: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("EchecDb", "Echec DB: ${e.message}", e)
            }
        }
    }

    private fun extractTokenFromCookie(cookieHeader: String): String? =
        Pattern.compile("MoodSession=([^;]+)").matcher(cookieHeader).takeIf { it.find() }?.group(1)

    private fun updateTokenInPreferences(newToken: String) {
        getSharedPreferences("mood", MODE_PRIVATE).edit().apply {
            putString("jwtToken", newToken)
            apply()
        }
    }

    private fun navigateToSignupActivity() {
        startActivity(Intent(this@SigninActivity, SignupActivity::class.java))
        finish()
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this@SigninActivity, MainActivity::class.java))
    }
}