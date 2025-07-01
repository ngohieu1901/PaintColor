package com.paintcolor.drawing.paint.domain.use_cases

import com.paintcolor.drawing.paint.domain.repository.PaintBrushRepository

class GetAllPaintBrushUseCase(
    private val paintBrushRepository: PaintBrushRepository
) {
    suspend operator fun invoke() = paintBrushRepository.getAllListPaintBrush()
}