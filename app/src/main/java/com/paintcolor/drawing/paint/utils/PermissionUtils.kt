package com.paintcolor.drawing.paint.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.paintcolor.drawing.paint.firebase.ads.AdsHelper
import com.paintcolor.drawing.paint.constants.Constants

class PermissionUtils(val activity: Activity?) {
    fun isOverlayPermissionGranted(): Boolean {
        return Settings.canDrawOverlays(activity)
    }

    fun requestOverlayPermission(activity: Activity, requestCode: Int = 1000) {
        AdsHelper.disableResume(activity)
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:${activity.packageName}")
        activity.startActivityForResult(intent, requestCode)
    }

    fun isGrantPermission(permission: String): Boolean {
        activity?.let {
            return ActivityCompat.checkSelfPermission(
                activity,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
        return false
    }

    fun isGrantMultiplePermissions(permissions: Array<String>): Boolean {
        activity?.let {
            for (permission in permissions) {
                val allow = ActivityCompat.checkSelfPermission(
                    activity,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
                if (!allow) return false
            }
            return true
        }
        return false
    }

    fun canShowPermissionDialogSystem(permission: String): Boolean {
        return if (activity != null)
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        else
            true
    }

    fun canShowAllListPermissionDialogSystem(permissions: Array<String>): Boolean {
        if (activity != null) {
            permissions.forEach { permission ->
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    return true
                }
            }
            return false
        } else
            return true
    }

    fun shouldShowRequestPermissionRationale(permissions: String): Boolean {
        if (activity == null)
            return false
        if (!isGrantPermission(permissions)) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions)) {
                return false
            }
        }
        return true
    }
}