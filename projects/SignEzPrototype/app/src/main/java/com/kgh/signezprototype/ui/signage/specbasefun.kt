package com.kgh.signezprototype.ui.signage

import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
    val options = BitmapFactory.Options()
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
}