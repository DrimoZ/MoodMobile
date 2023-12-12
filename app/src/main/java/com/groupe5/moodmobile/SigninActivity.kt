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
import java.util.regex.Pattern


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
                    //Get the token from the cookie
                    val cookieHeader: String? = response.headers().get("Set-Cookie")
                    if (cookieHeader != null) {
                        val token = extractTokenFromCookie(cookieHeader)
                        Log.d("cookieToken", token)
                        updateTokenInPreferences(token)
                    }

                    startActivity(Intent(this@SigninActivity, MainActivity::class.java))
                } else {
                    val message = "Echec Auth!: ${response.message()}"
                    Log.d("EchecAuth", message)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                val message = "Echec DB: ${t.message}"
                Log.e("EchecDb", message, t)
            }

        })
    }
    private fun extractTokenFromCookie(cookieHeader: String): String {
        val pattern = Pattern.compile("MoodSession=([^;]+)")
        val matcher = pattern.matcher(cookieHeader)
        return if (matcher.find()) {
            matcher.group(1)
        } else {
            ""
        }
    }
    private fun updateTokenInPreferences(newToken: String) {
        val prefs = getSharedPreferences("mood", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("jwtToken", newToken)
        editor.apply()
    }
}