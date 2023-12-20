package com.groupe5.moodmobile.dtos.Publication.Input

data class DtoInputPublicationsResponse(val isConnectedUser: Boolean,
                                        val isPublicationsPublic: Boolean,
                                        val publications: List<DtoInputPublication>)
