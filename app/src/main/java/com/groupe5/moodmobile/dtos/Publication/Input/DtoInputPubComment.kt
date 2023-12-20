package com.groupe5.moodmobile.dtos.Publication.Input

import java.util.Date

data class DtoInputPubComment(val id: Int,
                              val date: Date,
                              val content: String,
                              val idAuthorImage: Int,
                              val nameAuthor: String,
                              val idAuthor: String)
