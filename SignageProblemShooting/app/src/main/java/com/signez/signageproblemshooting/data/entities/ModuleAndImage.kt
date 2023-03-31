package com.signez.signageproblemshooting.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ErrorModuleWithImage(
    @Embedded val errorModule: ErrorModule,
    @Relation(
        parentColumn = "id",
        entityColumn = "error_module_id"
    )
    val errorImage: ErrorImage?
)