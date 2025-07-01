package com.paintcolor.drawing.paint.widget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.paintcolor.drawing.paint.firebase.ads.AdsHelper
import com.paintcolor.drawing.paint.firebase.event.AdmobEvent

fun Context.goToSetting(activity: Activity) {
    AdsHelper.disableResume(activity)
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts(
        "package",
        applicationContext.packageName,
        null
    )
    intent.data = uri
    startActivity(intent)
}

fun Context.goToWifiSetting(activity: Activity) {
    AdsHelper.disableResume(activity)
    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun Context.logEvent(nameEvent: String) {
    AdmobEvent.logEvent(this, nameEvent, Bundle())
}

fun Context.logEvent(nameEvent: String, bundle: Bundle) {
    AdmobEvent.logEvent(this, nameEvent, bundle)
}

fun Fragment.callMultiplePermissions(
    callbackPermission: (Boolean) -> Unit
): ActivityResultLauncher<Array<String>> {
    return registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { callback ->
        callbackPermission.invoke(!callback.containsValue(false))
    }
}

@ColorInt
fun String.toColorInt(defaultColor: Int = Color.WHITE): Int {
    return try {
        if (!isNullOrBlank()) Color.parseColor(this) else defaultColor
    } catch (e: Exception) {
        defaultColor
    }
}

fun Int.toColorHex(includeAlpha: Boolean = false): String {
    return if (includeAlpha) {
        String.format("#%08X", this)
    } else {
        String.format("#%06X", 0xFFFFFF and this)
    }
}