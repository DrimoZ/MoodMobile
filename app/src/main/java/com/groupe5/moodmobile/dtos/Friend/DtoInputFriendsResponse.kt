package com.groupe5.moodmobile.dtos.Friend

data class DtoInputFriendsResponse(val isConnectedUser: Boolean,
                                   val isFriendPublic: Boolean,
                                   val friends: List<DtoInputFriend>)
