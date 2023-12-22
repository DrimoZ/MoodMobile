package com.groupe5.moodmobile.dtos.Friend

import java.util.Date

data class DtoInputFriendRequest(val friendRequestDate: Date,
                                 val imageId: Int,
                                 val isAccepted: Boolean,
                                 val isConnectedEmitter: Boolean,
                                 val isDone: Boolean,
                                 val isFriendWithConnected: Int,
                                 val userId: String,
                                 val userName: String)