package com.signez.signageproblemshooting.datum_temp

data class ErrorModule(
    val id: Long,
    val resultId: Long,
    val score: Double,
    val x: Int,
    val y: Int
)