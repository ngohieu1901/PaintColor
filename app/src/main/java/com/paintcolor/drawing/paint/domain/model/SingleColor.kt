package com.paintcolor.drawing.paint.domain.model

import java.util.UUID

data class SingleColor(
    val id: String = UUID.randomUUID().toString(),
    val colorCode: String,
    val isSelected: Boolean
)