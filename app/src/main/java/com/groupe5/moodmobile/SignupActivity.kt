package com.groupe5.moodmobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.groupe5.moodmobile.databinding.ActivitySigninBinding
import com.groupe5.moodmobile.databinding.ActivitySignupBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    private val authenticationService = ApiClient.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignUpSignUp.setOnClickListener {
            val name = binding.etSignupFullName.text.toString()
            val login = binding.etSignupUsername.text.toString()
            val mail = binding.etSignupEmail.text.toString()
            val birthdate = binding.etSignupBirthdate.text.toString()
            val password = binding.etSignupPassword.text.toString()
            val passwordConfirmation = binding.etSignupPasswordConfirmation.text.toString()
            if (validateForm(name, login, mail, birthdate, password, passwordConfirmation)) {
                val message = "Submit!"
                Log.d("Submit", message)
                submitForm(name, login, mail, birthdate, password, passwordConfirmation)
            } else {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            }
        }
        binding.tvSignupLink.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun submitForm(name: String, login: String, mail: String, birthdate: String, password: String, passwordConfirmation: String) {
        val dto = DtoOutputUserSignup(name, login, mail, birthdate, password)
        val call = authenticationService.signUpUser(dto)

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

                    startActivity(Intent(this@SignupActivity, MainActivity::class.java))
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("EchecAuth", "Error body: $errorBody")
                    if (response.code() == 409) {
                        Log.d("EchecAuth", "Conflict: Username or mail already used")
                        runOnUiThread {
                            Toast.makeText(
                                this@SignupActivity,
                                "Username and email already exists.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                if (t is HttpException) {
                    val responseCode = t.code()
                    if (responseCode == 409) {
                        Log.d("EchecAuth", "Conflict: Username or mail already used")
                    }
                }
                // Echec bd pas accessible
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
    private fun validateForm(
        name: String,
        login: String,
        mail: String,
        birthdate: String,
        password: String,
        passwordConfirmation: String
    ): Boolean {
        return name.isNotEmpty() &&
                login.isNotEmpty() && login.length >= 8 &&
                mail.isNotEmpty() &&
                birthdate.isNotEmpty() &&
                isPasswordValid(password) &&
                isPasswordValid(passwordConfirmation)
    }
    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = Pattern.compile("^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*()_+=\\[\\]{};:<>|.\\/?,-]).+$")
        val matcher = passwordPattern.matcher(password)
        return matcher.matches()
    }
    private fun updateTokenInPreferences(newToken: String) {
        val prefs = getSharedPreferences("mood", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("jwtToken", newToken)
        editor.apply()
    }
}