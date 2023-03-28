package com.signez.signageproblemshooting.ui.inputs

import android.net.Uri
import androidx.compose.runtime.MutableState

interface MediaViewModel {
    var mCurrentPhotoPath: MutableState<String>
    var imageUri:MutableState<Uri>
    var type:Int
}