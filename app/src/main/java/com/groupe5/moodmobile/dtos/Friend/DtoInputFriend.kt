package com.groupe5.moodmobile.dtos.Friend

data class DtoInputFriend(val commonFriendCount: Int,
                          val userId: String,
                          val imageId: Int,
                          var isFriendWithConnected: Int,
                          val userLogin: String,
                          val userName: String)
