package com.paintcolor.drawing.paint.domain.model

import java.util.UUID

data class ColorCustom (
    val id: String = UUID.randomUUID().toString(),
    val colorCode: String = "",
    val isItemSelected: Boolean = false,
    val isItemAdd: Boolean = false,
)