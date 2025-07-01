package com.paintcolor.drawing.paint.firebase.ads

import android.app.Activity
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.amazic.library.Utils.RemoteConfigHelper
import com.amazic.library.ads.admob.Admob
import com.amazic.library.ads.admob.AdmobApi
import com.amazic.library.ads.app_open_ads.AppOpenManager
import com.amazic.library.ads.callback.NativeCallback
import com.amazic.library.ads.native_ads.NativeBuilder
import com.amazic.library.ads.native_ads.NativeManager
import com.google.android.gms.ads.nativead.NativeAd
import com.paintcolor.drawing.paint.firebase.ads.RemoteName.RESUME_WB
import com.paintcolor.drawing.paint.widget.visible


object AdsHelper {
    fun turnOffAllAds() {
        Admob.getInstance().showAllAds = false
    }

    fun disableResume(activity: Activity) {
        AppOpenManager.getInstance().disableAppResumeWithActivity(activity.javaClass)
    }

    fun enableResume(activity: Activity) {
        if (Admob.getInstance().checkCondition(activity, RESUME_WB)) {
            AppOpenManager.getInstance().enableAppResumeWithActivity(activity.javaClass)
        }
    }

    fun loadNativeItem(
        activity: AppCompatActivity,
        frAds: FrameLayout,
        keyRemote: String,
        @LayoutRes idLayoutShimmer: Int,
        @LayoutRes idLayoutNative: Int,
        onNativeAdLoaded: () -> Unit
    ): NativeManager {
        frAds.visible()
        val nativeBuilder = NativeBuilder(activity, frAds, idLayoutShimmer, idLayoutNative, idLayoutNative, true)
        nativeBuilder.listIdAdMain = AdmobApi.getInstance().getListIDByName(keyRemote)
        nativeBuilder.listIdAdSecondary = AdmobApi.getInstance().getListIDByName(keyRemote)

        return NativeManager(activity, activity, nativeBuilder, keyRemote).apply {
            setIntervalReloadNative(RemoteConfigHelper.getInstance().get_config_long(activity, RemoteName.INTERVAL_RELOAD_NATIVE) * 1000)
            setAlwaysReloadOnResume(true)
            object : NativeCallback() {
                override fun onNativeAdLoaded(nativeAd: NativeAd?) {
                    super.onNativeAdLoaded(nativeAd)
                    onNativeAdLoaded()
                }
            }
        }
    }

}