package com.signez.signageproblemshooting.ui.inputs

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


class VideoViewModel: ViewModel() {
    var videoUri = mutableStateOf(Uri.EMPTY)
    var mCurrentVideoPath = mutableStateOf("")
    var type = 0; // 0 = 선택 x, 1 = 갤러리에서 골랐을 때, 2 = 앱에서 찍었을 때
}