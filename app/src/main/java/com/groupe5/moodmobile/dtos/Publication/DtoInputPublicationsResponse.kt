package com.groupe5.moodmobile.dtos.Publication

data class DtoInputPublicationsResponse(val isConnectedUser: Boolean,
                                        val isPublicationsPublic: Boolean,
                                        val publications: List<DtoInputPublication>)
