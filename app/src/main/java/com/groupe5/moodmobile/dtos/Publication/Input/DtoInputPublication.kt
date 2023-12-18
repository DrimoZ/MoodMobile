package com.groupe5.moodmobile.dtos.Publication.Input

import java.util.Date

data class DtoInputPublication(val id:Int,
                               val likeCount:Int,
                               val commentCount:Int,
                               val content:String,
                               val date: Date,
                               val elements: List<DtoInputPubElement>)
