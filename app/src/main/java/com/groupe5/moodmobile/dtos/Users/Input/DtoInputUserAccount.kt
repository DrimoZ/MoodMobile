package com.groupe5.moodmobile.dtos.Users.Input

import java.util.Date

data class DtoInputUserAccount(val birthDate: Date,
                               val description: String,
                               val login: String,
                               val mail: String,
                               val name: String,
                               val phoneNumber: String,
                               val title: String)
