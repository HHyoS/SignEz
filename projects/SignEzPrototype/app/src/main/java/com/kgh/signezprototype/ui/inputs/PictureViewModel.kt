package com.kgh.signezprototype.ui.inputs

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


class PictureViewModel: ViewModel() {
    var imageUri = mutableStateOf(Uri.EMPTY)
    var mCurrentPhotoPath = mutableStateOf("")
}