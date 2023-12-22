package com.groupe5.moodmobile.dtos.Publication

data class PublicationViewState(
    var id: Int = 0,
    var liked: Boolean = false,
    var likeCount: Int = 0,
    var displayComments: Boolean = false,
    var commentCount: Int = 0
)
