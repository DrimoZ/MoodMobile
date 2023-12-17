package com.groupe5.moodmobile.dtos.Users.Input

import com.groupe5.moodmobile.dtos.Image.DtoInputImage
import java.lang.invoke.TypeDescriptor

data class DtoInputUserProfile(val login: String, val name: String, val isFriendWithConnected: Int, val title: String, val description: String, val friendCount: Int, val publicationCount: Int, val idImage: Int)
