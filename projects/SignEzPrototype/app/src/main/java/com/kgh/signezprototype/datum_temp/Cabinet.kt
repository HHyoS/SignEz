package com.kgh.signezprototype.datum_temp

data class Cabinet(
    var id: Long?,
    var name: String,
    var cabinetWidth: Double,
    var cabinetHeight: Double,
    var moduleRowCount: Int,
    var moduleColCount: Int,
    var repImage: ByteArray
)

