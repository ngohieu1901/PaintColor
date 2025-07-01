package com.paintcolor.drawing.paint.data.repositories

import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.coloring_source.StyleTools
import com.paintcolor.drawing.paint.data.result.DataResult
import com.paintcolor.drawing.paint.domain.model.PaintBrush
import com.paintcolor.drawing.paint.domain.repository.PaintBrushRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class PaintBrushRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher
): PaintBrushRepository {
    override suspend fun getAllListPaintBrush(): DataResult<List<PaintBrush>> = withContext(ioDispatcher) {
        try {
            val listPaintBrush = mutableListOf<PaintBrush>().apply {
                add(PaintBrush(nameResource = R.string.pencil, imageResource = R.drawable.ic_pencil, imageLargeResource = R.drawable.ic_pencil_large, size = 50, styleTools = StyleTools.PENCIL, isSelected = true, isViewedReward = true))
                add(PaintBrush(nameResource = R.string.fill, imageResource = R.drawable.ic_fill, imageLargeResource = R.drawable.ic_fill_large, size = 50, styleTools = StyleTools.FILL, isSelected = false))
                add(PaintBrush(nameResource = R.string.brush, imageResource = R.drawable.ic_brush, imageLargeResource = R.drawable.ic_brush_large, size = 50, styleTools = StyleTools.BRUSH, isSelected = false))
                add(PaintBrush(nameResource = R.string.eraser, imageResource = R.drawable.ic_eraser, imageLargeResource = R.drawable.ic_eraser_large, size = 50, styleTools = StyleTools.ERASER, isSelected = false))
                add(PaintBrush(nameResource = R.string.big_brush, imageResource = R.drawable.ic_big_brush, imageLargeResource = R.drawable.ic_big_brush_large, size = 50, styleTools = StyleTools.BIG_BRUSH, isSelected = false))
                add(PaintBrush(nameResource = R.string.spray, imageResource = R.drawable.ic_spray, imageLargeResource = R.drawable.ic_spray_large, size = 50, styleTools = StyleTools.SPRAY, isSelected = false))
                add(PaintBrush(nameResource = R.string.marker, imageResource = R.drawable.ic_marker, imageLargeResource = R.drawable.ic_marker_large, size = 50, styleTools = StyleTools.MARKER, isSelected = false))
                add(PaintBrush(nameResource = R.string.tech_pen, imageResource = R.drawable.ic_tech_pen, imageLargeResource = R.drawable.ic_tech_pen_large, size = 50, styleTools = StyleTools.TECH_PEN, isSelected = false))
            }
            DataResult.Success(listPaintBrush)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}