package com.paintcolor.drawing.paint.domain.repository

import android.graphics.Bitmap

interface SketchImageRepository {
    suspend fun loadOriginalBitmap(
        imagePath: String
    ): Bitmap

    suspend fun flipHorizontal(bitmap: Bitmap): Bitmap

    suspend fun convertToSketch(bitmap: Bitmap): Bitmap
}