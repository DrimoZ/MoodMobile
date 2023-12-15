package com.groupe5.moodmobile.dtos.Friend

data class DtoInputFriend(val commonFriendCount: Int,
                          val id: String,
                          val idImage: Int,
                          val isFriendWithConnected: Int,
                          val login: String,
                          val name: String)
