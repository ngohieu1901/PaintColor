package com.paintcolor.drawing.paint.presentations.feature.coloring_picture

import android.graphics.Bitmap
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.coloring_source.Image
import com.paintcolor.drawing.paint.domain.model.SingleColor

data class ColoringPictureUiState(
    val cachedBitmap: Bitmap? = null,
    val undoStack: List<Image> = emptyList(),
    val redoStack: List<Image> = emptyList(),
    val isCanvasInitialized: Boolean = false
)