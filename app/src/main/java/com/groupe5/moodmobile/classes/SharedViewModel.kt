package com.groupe5.moodmobile.classes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPubComment

class SharedViewModel : ViewModel() {
    val friendData: MutableLiveData<DtoInputFriend> = MutableLiveData()
    val numberCommentAfterDelete: MutableLiveData<Int> = MutableLiveData()
}