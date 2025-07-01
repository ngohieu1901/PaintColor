package com.paintcolor.drawing.paint.data.repositories

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.paintcolor.drawing.paint.constants.Constants
import com.paintcolor.drawing.paint.domain.model.ColoringMyCreation
import com.paintcolor.drawing.paint.domain.model.SketchMyCreation
import com.paintcolor.drawing.paint.domain.repository.FileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale

class FileRepositoryImpl(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher
) : FileRepository {
    override suspend fun saveImage(folder: String, bitmap: Bitmap, existingPath: String?): Result<String> {
        return withContext(ioDispatcher) {
            try {
                val imageFolder = File(context.filesDir, folder).apply {
                    if (!exists()) mkdirs()
                }

                val imageFile = existingPath
                    ?.let { File(it) }
                    ?: File(imageFolder, "${folder}_${System.currentTimeMillis()}.png")

                FileOutputStream(imageFile).use { out ->
                    if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                        throw IOException("Bitmap.compress() failed")
                    }
                }
                Result.success(imageFile.absolutePath)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun deleteFile(pathFile: String): Result<Boolean> =
        withContext(ioDispatcher) {
            try {
                val file = File(pathFile)
                if (!file.exists()) {
                    return@withContext Result.failure(FileNotFoundException("File not exits: $pathFile"))
                }
                val deleted = file.delete()
                if (deleted) {
                    Result.success(true)
                } else {
                    Result.failure(IOException("Failed to delete file : $pathFile"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getSavedFilesColoring(): Result<List<ColoringMyCreation>> =
        withContext(ioDispatcher) {
            try {
                val directory = File(context.filesDir, Constants.FolderName.COLORING_DATA)
                if (!directory.exists() || !directory.isDirectory) {
                    return@withContext Result.success(emptyList())
                }

                val creations = directory.listFiles()
                    ?.filter { it.isFile }
                    ?.map { file ->
                        ColoringMyCreation(
                            imagePath = file.absolutePath,
                            lastModified = file.lastModified()
                        )
                    }
                    ?.sortedByDescending { it.lastModified }
                    ?: emptyList()

                Result.success(creations)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getSavedFilesDrawing(): Result<List<SketchMyCreation>> =
        withContext(ioDispatcher) {
            try {
                val directory = File(context.filesDir, Constants.FolderName.DRAWING_DATA)
                if (!directory.exists() || !directory.isDirectory) {
                    return@withContext Result.success(emptyList())
                }

                val creations = directory.listFiles()
                    ?.filter { it.isFile }
                    ?.map { file ->
                        val extension = file.extension.lowercase()
                        val mimeType = MimeTypeMap.getSingleton()
                            .getMimeTypeFromExtension(extension)
                        val isVideo = mimeType?.startsWith("video/") == true

                        // 1. Lấy raw duration (ms)
                        val durationMs = if (isVideo) {
                            val retriever = MediaMetadataRetriever()
                            try {
                                retriever.setDataSource(file.absolutePath)
                                retriever
                                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                                    ?.toLongOrNull()
                                    ?: 0L
                            } finally {
                                retriever.release()
                            }
                        } else {
                            0L
                        }

                        val totalSeconds = durationMs / 1000
                        val minutes      = totalSeconds / 60
                        val seconds      = totalSeconds % 60
                        val durationStr  = String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            minutes,
                            seconds
                        )

                        SketchMyCreation(
                            filePath     = file.absolutePath,
                            lastModified = file.lastModified(),
                            isVideo      = isVideo,
                            duration     = durationStr
                        )
                    }
                    ?.sortedByDescending { it.lastModified }
                    ?: emptyList()

                Result.success(creations)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun copyFileToDirectoryDownload(filePath: String): Result<String> =
        withContext(ioDispatcher) {
            try {
                val sourceFile = File(filePath)
                if (!sourceFile.exists()) {
                    return@withContext Result.failure(FileNotFoundException("Source file not found"))
                }

                val fileName = sourceFile.name
                val mimeType = "image/png"
                val contentResolver = context.contentResolver

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues().apply {
                        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                        put(MediaStore.Downloads.MIME_TYPE, mimeType)
                        put(MediaStore.Downloads.IS_PENDING, 1)
                    }

                    val collection =
                        MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                    val itemUri = contentResolver.insert(collection, values)
                        ?: return@withContext Result.failure(IOException("Failed to create MediaStore entry"))

                    val outputStream = contentResolver.openOutputStream(itemUri)
                        ?: return@withContext Result.failure(IOException("Failed to open output stream"))

                    FileInputStream(sourceFile).use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }

                    values.clear()
                    values.put(MediaStore.Downloads.IS_PENDING, 0)
                    contentResolver.update(itemUri, values, null, null)

                    Result.success(itemUri.toString())
                } else {
                    val downloadsDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    if (!downloadsDir.exists()) downloadsDir.mkdirs()

                    val destFile = File(downloadsDir, fileName)
                    sourceFile.inputStream().use { input ->
                        destFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    Result.success(destFile.absolutePath)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override fun shareFile(filePath: String): Result<Intent> {
        return try {
            val file = File(filePath)
            if (!file.exists()) {
                return Result.failure(FileNotFoundException("File not found at: $filePath"))
            }

            val uri: Uri =
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            Result.success(shareIntent)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getOutputFile(): File {
        val file = File(
            context.filesDir,
            Constants.FolderName.DRAWING_DATA
        )
        if (!file.exists()) {
            file.mkdirs()
        }
        return File(file, "${Constants.FolderName.DRAWING_DATA}_${System.currentTimeMillis()}.mp4")
    }
}