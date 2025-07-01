package com.paintcolor.drawing.paint.domain.model

data class ToolsDrawing (
    val iconResource: Int,
    val nameResource: Int,
    val isSelected: Boolean = false,
    val isViewedReward: Boolean = false,
)