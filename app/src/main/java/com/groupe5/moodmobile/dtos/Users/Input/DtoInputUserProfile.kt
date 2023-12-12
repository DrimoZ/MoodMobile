package com.groupe5.moodmobile.dtos.Users.Input

import java.lang.invoke.TypeDescriptor

data class DtoInputUserProfile(val login: String, val name: String, val title: String, val description: String, val friendCount: Int, val publicationCount: Int)
