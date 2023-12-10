package com.groupe5.moodmobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.groupe5.moodmobile.databinding.ActivitySigninBinding
import org.json.JSONObject
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

        binding.btnSignIn.setOnClickListener {
            val login = binding.etLogin.text.toString()
            val password = binding.etPassword.text.toString()
            val stayLoggedIn = binding.cbStayConnected.isChecked
            val message = "Submit!"
            Log.d("Submit", message)
            submitForm(login, password, stayLoggedIn)
        }
    }

    private fun submitForm(login: String, password: String, stayLoggedIn: Boolean) {
        val dto = DtoOutputUserSignin(login, password, stayLoggedIn)
        val call = authenticationService.signInUser(dto)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {

                    val sessionTokenService = SessionTokenService("clesecrete")
                    val message = "DebToken!"
                    Log.d("DebToken", message)
                    val userId = login.toString()
                    val role = "user"
                    val isSessionOnly = true

                    val sessionToken = sessionTokenService.createSessionToken(userId, role, isSessionOnly)
                    updateTokenInPreferences(sessionToken)

                    startActivity(Intent(this@SigninActivity, MainActivity::class.java))
                } else {
                    val message = "Echec Auth!"
                    Log.d("EchecAuth", message)
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