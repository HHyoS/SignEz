package com.kgh.signezprototype.ui.inputs

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


class VideoViewModel: ViewModel() {
    var videoUri = mutableStateOf(Uri.EMPTY)
    var mCurrentVideoPath = mutableStateOf("")
}