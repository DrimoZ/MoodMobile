package com.groupe5.moodmobile.dtos.Friend

data class DtoInputFriend(val commonFriendCount: Int,
                          val id: String,
                          val isFriendWithConnected: Int,
                          val login: String,
                          val name: String)
