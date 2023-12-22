package com.groupe5.moodmobile.dtos.Publication.Input

import java.util.Date

data class DtoInputPublicationInformation(val publicationId: Int,
                                          val publicationContent: String,
                                          val publicationDate: Date,
                                          val likeCount: Int,
                                          val commentCount: Int,
                                          val isFromConnected: Boolean,
                                          val hasConnectedLiked: Boolean,
                                          val authorName: String,
                                          val authorId: String,
                                          val authorImageId: Int,
                                          val authorRole: Int,
                                          val elements: List<DtoInputPubElement>,
                                          val comments: List<DtoInputPubComment>)
