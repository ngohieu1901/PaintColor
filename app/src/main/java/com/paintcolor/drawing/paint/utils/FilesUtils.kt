package com.paintcolor.drawing.paint.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.ContentProviderCompat.requireContext
import timber.log.Timber
import java.io.File
import java.io.IOException

class FilesUtils(private val context: Context) {
    fun createDataFiles(wallpaperName: String): File {
        val directory = File(context.getExternalFilesDir(null), "DataFolder")
        return File(directory, wallpaperName)
    }

    fun getFileNameFromUrl(url: String): String {
        return url.substringAfterLast("/")
    }

    fun getBitmapFromDataFolder(nameImage: String): Bitmap? {
        val directory = File(context.getExternalFilesDir(null), "DataFolder")
        val file = File(directory, nameImage)
        Timber.d("getBitmapFromDataFolder ${file.absolutePath}")
        return if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
    }

    fun getAbsolutePathDirectoryData(context: Context): String {
        val directory = File(context.getExternalFilesDir(null), "DataFolder")
        if (!directory.exists()) directory.mkdirs()
        return directory.absolutePath + "/"
    }

    fun getImagePathByImageUrl(imageUrl: String): String? {
        val imagePath = getAbsolutePathDirectoryData(context) + getFileNameFromUrl(imageUrl)
        return if (File(imagePath).exists()) imagePath else null
    }

    fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getBitmapFromPath(path: String): Bitmap? {
        return BitmapFactory.decodeFile(path)
    }

}