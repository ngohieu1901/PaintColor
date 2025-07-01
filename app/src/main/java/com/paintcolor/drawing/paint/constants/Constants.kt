package com.paintcolor.drawing.paint.constants

import android.Manifest
import android.content.res.Resources
import android.os.Build
import androidx.annotation.RequiresApi

object Constants {
    const val PRIVACY_POLICY = "https://amazic.net/Privacy-Policy-270pdf.html"

    const val BASE_URL = "http://51.79.177.162/api/"

    object IntentKeys {
        const val SCREEN = "SCREEN"
        const val SPLASH_ACTIVITY = "SplashActivity"
    }

    object FolderName {
        const val COLORING_DATA = "ColoringData"
        const val DRAWING_DATA = "DrawingData"
    }

    object Screen {
        val width: Int
            get() = Resources.getSystem().displayMetrics.widthPixels

        val height: Int
            get() = Resources.getSystem().displayMetrics.heightPixels
    }

    val STORAGE_PERMISSION_API_SMALLER_33 = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
    )

    val CAMERA_AND_MICROPHONE_PERMISSION = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    val STORAGE_PERMISSION_API_LARGER_32 = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO,
    )

    val STORAGE_PERMISSION = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) STORAGE_PERMISSION_API_LARGER_32 else STORAGE_PERMISSION_API_SMALLER_33

    val NOTIFICATION_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        Manifest.permission.POST_NOTIFICATIONS
    else ""

}