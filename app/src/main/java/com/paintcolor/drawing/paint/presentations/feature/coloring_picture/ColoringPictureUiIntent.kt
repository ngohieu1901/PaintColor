package com.paintcolor.drawing.paint.presentations.feature.coloring_picture

import android.graphics.Bitmap
import com.paintcolor.drawing.paint.coloring_source.Image
import com.paintcolor.drawing.paint.domain.model.SingleColor

sealed interface ColoringPictureUiIntent {
    data class CacheCanvasState(
        val bitmap: Bitmap ?= null,
        val undoStack: List<Image>,
        val redoStack: List<Image>
    ) : ColoringPictureUiIntent
    data object MarkCanvasInitialized : ColoringPictureUiIntent
    data class SaveImage(val bitmap: Bitmap,val oldFilePath: String?) : ColoringPictureUiIntent
}