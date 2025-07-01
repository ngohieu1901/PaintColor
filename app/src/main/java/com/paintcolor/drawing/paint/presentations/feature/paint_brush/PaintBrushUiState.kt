package com.paintcolor.drawing.paint.presentations.feature.paint_brush

import com.paintcolor.drawing.paint.domain.model.PaintBrush

data class PaintBrushUiState(
    val listAllPaintBrush: List<PaintBrush> = emptyList(),
    val listPaintBrushInColoringPicture: List<PaintBrush> = emptyList(),
    val countWatchedVideoReward: Int = 0
) {
    val paintBrushSelected = listAllPaintBrush.find { it.isSelected }
    val sizeBrushSelected = listAllPaintBrush.find { it.isSelected }?.size ?: 50
}