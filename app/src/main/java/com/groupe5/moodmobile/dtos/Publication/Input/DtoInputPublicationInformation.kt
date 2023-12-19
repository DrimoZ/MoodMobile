package com.groupe5.moodmobile.dtos.Publication.Input

import java.util.Date

data class DtoInputPublicationInformation(val id: Int,
                                          val content: String,
                                          val date: Date,
                                          val likeCount: Int,
                                          val commentCount: Int,
                                          val isFromConnected: Boolean,
                                          val hasConnectedLiked: Boolean,
                                          val nameAuthor: String,
                                          val idAuthor: String,
                                          val idAuthorImage: Int,
                                          val elements: List<DtoInputPubElement>,
                                          val comments: List<DtoInputPubComment>)
