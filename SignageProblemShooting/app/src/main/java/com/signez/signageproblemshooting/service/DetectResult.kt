package com.signez.signageproblemshooting.service

import android.graphics.Rect


data class DetectResult(
    val classIndex: Int,
    val score: Float,
    val rect: Rect
    )