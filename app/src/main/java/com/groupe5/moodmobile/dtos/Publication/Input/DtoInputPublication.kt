package com.groupe5.moodmobile.dtos.Publication.Input

import java.util.Date

data class DtoInputPublication(val publicationId:Int,
                               val publicationContent:String,
                               val publicationDate: Date,
                               val likeCount:Int,
                               val commentCount:Int,
                               val elements: List<DtoInputPubElement>)
