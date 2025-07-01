package com.paintcolor.drawing.paint.domain.repository

import android.content.Intent
import android.graphics.Bitmap
import com.paintcolor.drawing.paint.domain.model.ColoringMyCreation
import com.paintcolor.drawing.paint.domain.model.SketchMyCreation
import java.io.File

interface FileRepository {
    suspend fun saveImage(folder: String, bitmap: Bitmap, existingPath: String?): Result<String>
    suspend fun deleteFile(pathFile: String): Result<Boolean>
    suspend fun getSavedFilesColoring(): Result<List<ColoringMyCreation>>
    suspend fun getSavedFilesDrawing(): Result<List<SketchMyCreation>>
    suspend fun copyFileToDirectoryDownload(filePath: String): Result<String>
    fun shareFile(filePath: String): Result<Intent>
    fun getOutputFile(): File
}