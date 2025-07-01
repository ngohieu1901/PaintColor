package com.paintcolor.drawing.paint.presentations.feature.drawing.ui_state

import com.paintcolor.drawing.paint.domain.model.ToolsDrawing

data class ToolsUiState(
    val listTools: List<ToolsDrawing> = emptyList(),
    val countWatchedVideoReward: Int = 0
)