package com.paintcolor.drawing.paint.domain.repository

import com.paintcolor.drawing.paint.data.result.DataResult
import com.paintcolor.drawing.paint.domain.model.PaintBrush

interface PaintBrushRepository {
    suspend fun getAllListPaintBrush(): DataResult<List<PaintBrush>>
}