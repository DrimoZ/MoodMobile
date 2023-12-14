package com.groupe5.moodmobile.dtos.Friend

import com.groupe5.moodmobile.dtos.Publication.DtoInputPublication

data class DtoInputFriendsResponse(val isConnectedUser: Boolean,
                                   val isFriendPublic: Boolean,
                                   val friends: List<DtoInputFriend>)
