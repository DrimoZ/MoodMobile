package com.groupe5.moodmobile.dtos.Users.Output

import java.util.Date

data class DtoOutputUserAccount(val id: String,
                                val name: String,
                                val title: String,
                                val mail: String,
                                val birthDate: Date,
                                val description: String)
