package com.groupe5.moodmobile.dtos.Publication.Input

import java.util.Date

data class DtoInputPubComment(val commentId: Int,
                              val commentDate: Date,
                              val commentContent: String,
                              val authorImageId: Int,
                              val authorName: String,
                              val authorId: String,
                              val authorRole: Int)
