package com.groupe5.moodmobile.dtos.Users.Input

import java.util.Date

data class DtoInputUserAccount(val accountBirthDate: Date,
                               val accountDescription: String,
                               val userMail: String,
                               val userName: String,
                               val userTitle: String)
