package com.paintcolor.drawing.paint.data.repositories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.Segmenter
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import com.paintcolor.drawing.paint.domain.repository.SketchImageRepository
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageThresholdEdgeDetectionFilter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SketchImageRepositoryImpl(
    private val context: Context,
    private val defaultDispatcher: CoroutineDispatcher
) : SketchImageRepository {

    private val segmenter: Segmenter by lazy {
        val opts = SelfieSegmenterOptions.Builder()
            .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
            .build()
        Segmentation.getClient(opts)
    }

    override suspend fun loadOriginalBitmap(
        imagePath: String
    ): Bitmap = withContext(defaultDispatcher) {
        val raw = getBitmap(imagePath)
            ?.let { modifyOrientation(it, imagePath) }
            ?: throw IllegalStateException("Failed load bitmap from $imagePath")
        raw
    }

    override suspend fun flipHorizontal(bitmap: Bitmap): Bitmap = withContext(defaultDispatcher) {
        val matrix = Matrix().apply { preScale(-1f, 1f) }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    override suspend fun convertToSketch(bitmap: Bitmap): Bitmap = withContext(defaultDispatcher) {
        val gpu = GPUImage(context).apply { setImage(bitmap) }
        gpu.setFilter(GPUImageThresholdEdgeDetectionFilter())
        val result = gpu.bitmapWithFilterApplied
            ?: throw IllegalStateException("Failed convert to sketch")

        val out = result.copy(Bitmap.Config.ARGB_8888, true)
        val w = out.width
        val h = out.height
        for (y in 0 until h) {
            for (x in 0 until w) {
                if (out.getPixel(x, y) == -1) {
                    out.setPixel(x, y, 0)
                }
            }
        }
        out
    }

    private fun getBitmap(filePath: String): Bitmap? =
        BitmapFactory.decodeFile(filePath, BitmapFactory.Options())

    private suspend fun modifyOrientation(
        bitmap: Bitmap,
        imagePath: String
    ): Bitmap = withContext(defaultDispatcher) {
        try {
            val ei = ExifInterface(imagePath)
            when (ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )) {
                ExifInterface.ORIENTATION_ROTATE_90 ->
                    rotate(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 ->
                    rotate(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 ->
                    rotate(bitmap, 270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL ->
                    flip(bitmap, horizontal = true, vertical = false)
                ExifInterface.ORIENTATION_FLIP_VERTICAL ->
                    flip(bitmap, horizontal = false, vertical = true)
                else ->
                    bitmap
            }
        } catch (_: Exception) {
            bitmap
        }
    }

    private suspend fun rotate(
        bitmap: Bitmap,
        degrees: Float
    ): Bitmap = withContext(defaultDispatcher) {
        val matrix = Matrix().apply { postRotate(degrees) }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private suspend fun flip(
        bitmap: Bitmap,
        horizontal: Boolean,
        vertical: Boolean
    ): Bitmap = withContext(defaultDispatcher) {
        val sx = if (horizontal) -1f else 1f
        val sy = if (vertical) -1f else 1f
        val matrix = Matrix().apply { preScale(sx, sy) }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
