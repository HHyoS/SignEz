package com.kgh.signezprototype.datum_temp

import android.graphics.Bitmap

data class Image(
    var id: Long,
    var error_module_id: Long,
    var evidence_image: Bitmap
)