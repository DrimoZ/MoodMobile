package com.groupe5.moodmobile

import android.R
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.groupe5.moodmobile.databinding.ActivitySigninBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SigninActivity : AppCompatActivity() {
    lateinit var binding: ActivitySigninBinding
    private val authenticationService = ApiClient.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignInSignIn.setOnClickListener {
            val login = binding.etSigninLogin.text.toString()
            val password = binding.etSigninPassword.text.toString()
            val stayLoggedIn = true
            val message = "Submit!"
            Log.d("Submit", message)
            submitForm(login, password, stayLoggedIn)
        }
        binding.tvSigninLink.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun submitForm(login: String, password: String, stayLoggedIn: Boolean) {
        val dto = DtoOutputUserSignin(login, password, stayLoggedIn)
        val call = authenticationService.signInUser(dto)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {

                    val sessionTokenService = SessionTokenService("clesecrete")
//                    val message = "DebToken!"
//                    Log.d("DebToken", message)
                    val userId = login.toString()
                    val role = "user"
                    val isSessionOnly = true

                    val sessionToken = sessionTokenService.createSessionToken(userId, role, isSessionOnly)
                    updateTokenInPreferences(sessionToken)

                    startActivity(Intent(this@SigninActivity, MainActivity::class.java))
                } else {
//                    val message = "Echec Auth!"
//                    Log.d("EchecAuth", message)
                    // Échec de l'authentification
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Echec bd pas accessible
                val message = "Echec DB: ${t.message}"
                Log.e("EchecDb", message, t)
            }

        })
    }
    private fun updateTokenInPreferences(newToken: String) {
        val prefs = getSharedPreferences("mood", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("jwtToken", newToken)
        editor.apply()
    }
}