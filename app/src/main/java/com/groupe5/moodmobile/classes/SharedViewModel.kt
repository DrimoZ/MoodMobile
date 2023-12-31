package com.groupe5.moodmobile.classes

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend

class SharedViewModel : ViewModel() {
    val friendData: MutableLiveData<DtoInputFriend> = MutableLiveData()
    val numberCommentAfterDelete: MutableLiveData<Int> = MutableLiveData()
    val mutableElementLiveData: MutableLiveData<MutableList<Uri>> = MutableLiveData()
}