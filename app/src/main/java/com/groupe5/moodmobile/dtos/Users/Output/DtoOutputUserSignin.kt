package com.groupe5.moodmobile.dtos.Users.Output

data class DtoOutputUserSignin(val userLogin: String,
                               val userPassword: String,
                               val stayLoggedIn: Boolean)
