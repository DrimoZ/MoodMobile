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

    private fun validateForm(name: String, login: String, mail: String, birthdate: String, password: String, passwordConfirmation: String): Boolean {
        return name.isNotEmpty() &&
                isLoginValid(login) &&
                isEmailValid(mail) &&
                isBirthdateValid(birthdate) &&
                isPasswordValid(password) &&
                arePasswordsEqual(password, passwordConfirmation)
    }
    private fun isLoginValid(login: String): Boolean{
        if(login.isNotEmpty() && login.length >= 8){
            return true
        }else{
            Toast.makeText(
                this@SignupActivity,
                "Login must be at least 8 characters long",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
    }

    private fun isBirthdateValid(birthdate: String): Boolean {
        val birthdateRegex = """\d{4}-\d{2}-\d{2}""".toRegex()
        if(birthdate.isNotEmpty() && birthdateRegex.matches(birthdate)){
            return true
        }else{
            Toast.makeText(
                this@SignupActivity,
                "Birthdate must be yyyy-dd-mm",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
    }
    private fun isEmailValid(email: String): Boolean {
        val emailRegex = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}".toRegex()
        if(email.isNotEmpty() && emailRegex.matches(email)){
            return email.isNotEmpty() && emailRegex.matches(email)
        }else{
            Toast.makeText(
                this@SignupActivity,
                "Mail must be user@example.com",
                Toast.LENGTH_LONG
            ).show()
            return email.isNotEmpty() && emailRegex.matches(email)
        }
    }

    private fun arePasswordsEqual(password: String, passwordConfirmation: String): Boolean{
        if(password == passwordConfirmation){
            return true
        }else{
            Toast.makeText(
                this@SignupActivity,
                "Passwords are differents",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        if (Pattern.compile("^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*()_+=\\[\\]{};:<>|.\\/?,-]).+$")
                .matcher(password).matches()){
            return true
        }else{
            val errorMsg = buildString {
                append("Password must contain:")
                append("\n- At least one uppercase letter")
                append("\n- At least one lowercase letter")
                append("\n- At least one digit (0-9)")
                append("\n- At least one special character (!@#$%^&*()_+[]{};:<>|./?,-)")
            }
            Toast.makeText(
                this@SignupActivity,
                errorMsg,
                Toast.LENGTH_LONG
            ).show()
            return false
        }
    }

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