package com.groupe5.moodmobile.dtos.Users.Output

import java.util.Date

data class DtoOutputUserAccount(val userId: String,
                                val userName: String,
                                val userTitle: String,
                                val userMail: String,
                                val accountBirthDate: Date,
                                val accountDescription: String)
