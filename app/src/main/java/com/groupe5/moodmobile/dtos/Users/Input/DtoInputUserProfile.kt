package com.groupe5.moodmobile.dtos.Users.Input

data class DtoInputUserProfile(val accountDescription: String,
                               val friendCount: Int,
                               val imageId: Int,
                               val isConnectedUser: Boolean,
                               val isFriendWithConnected: Int,
                               val userName: String,
                               val publicationCount: Int,
                               val userRole: Int )
