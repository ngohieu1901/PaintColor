package com.paintcolor.drawing.paint.presentations.feature.screen_base.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.amazic.library.Utils.EventTrackingHelper.native_language
import com.amazic.library.Utils.NetworkUtil
import com.amazic.library.Utils.RemoteConfigHelper
import com.amazic.library.ads.admob.Admob
import com.amazic.library.ads.admob.AdmobApi
import com.amazic.library.ads.app_open_ads.AppOpenManager
import com.amazic.library.ads.callback.AppOpenCallback
import com.amazic.library.ads.callback.InterCallback
import com.amazic.library.ads.callback.NativeCallback
import com.amazic.library.ads.splash_ads.AsyncSplash
import com.amazic.library.update_app.UpdateApplicationManager
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseActivity
import com.paintcolor.drawing.paint.constants.Constants.IntentKeys.SCREEN
import com.paintcolor.drawing.paint.constants.Constants.IntentKeys.SPLASH_ACTIVITY
import com.paintcolor.drawing.paint.databinding.ActivitySplashBinding
import com.paintcolor.drawing.paint.firebase.ads.RemoteName
import com.paintcolor.drawing.paint.firebase.ads.RemoteName.REWARDED_ALL
import com.paintcolor.drawing.paint.firebase.ads.RemoteName.TURN_OFF_CONFIGS
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.presentations.feature.container.ContainerActivity
import com.paintcolor.drawing.paint.presentations.feature.screen_base.language_start.LanguageStartActivity
import com.paintcolor.drawing.paint.presentations.feature.screen_base.no_internet.NoInternetActivity
import com.paintcolor.drawing.paint.presentations.feature.screen_base.uninstall.ProblemActivity
import com.paintcolor.drawing.paint.presentations.feature.screen_base.welcome_back.WelcomeBackActivity
import com.paintcolor.drawing.paint.utils.SharePrefUtils
import com.paintcolor.drawing.paint.widget.launchActivity
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private val sharePref: SharePrefUtils by inject()
    private var isHandleAsyncSplash = false

    companion object {
        var isShowSplashAds = false
        var isCloseSplashAds = false
        var isShowNativeLanguagePreloadAtSplash = false
        var nativeLanguagePreload: NativeAd? = null
        var appUpdateManager: AppUpdateManager? = null
        var installStateUpdatedListener: InstallStateUpdatedListener? = null
    }

    private val openCallback = object : AppOpenCallback() {
        override fun onNextAction() {
            super.onNextAction()
            startNextScreen()
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            isShowSplashAds = true
        }

        override fun onAdImpression() {
            super.onAdImpression()
            AppOpenManager.getInstance().appOpenAdSplash = null
        }

        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            isCloseSplashAds = true
        }

        override fun onAdFailedToShowFullScreenContent() {
            super.onAdFailedToShowFullScreenContent()
            isCloseSplashAds = true
        }
    }

    private val interCallback = object : InterCallback() {
        override fun onNextAction() {
            super.onNextAction()
            startNextScreen()
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            isShowSplashAds = true
        }

        override fun onAdImpression() {
            super.onAdImpression()
            Admob.getInstance().interstitialAdSplash = null
        }

        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            isCloseSplashAds = true
        }

        override fun onAdFailedToShowFullScreenContent() {
            super.onAdFailedToShowFullScreenContent()
            isCloseSplashAds = true
        }
    }

    private fun startNextScreen() {
//        launchActivity(ContainerActivity::class.java)
        launchActivity(LanguageStartActivity::class.java)
        finishAffinity()
    }

    override fun setViewBinding(): ActivitySplashBinding =
        ActivitySplashBinding.inflate(layoutInflater)

    @SuppressLint("SetTextI18n")
    override fun initView() {
        if (!isTaskRoot
            && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
            && intent.action != null && intent.action.equals(Intent.ACTION_MAIN)
        ) {
            finish()
            return
        }
        logEvent(EventName.splash_open)
        sharePref.countOpenApp += 1
        if (sharePref.countOpenApp <= 10) {
            logEvent(EventName.splash_open + "_" + sharePref.countOpenApp)
        }

        //to log event time splash check in lib
        AsyncSplash.getInstance().setTimeSplashCheck()

        //initUpdateApplicationManager
        UpdateApplicationManager.getInstance().init(this,
            object : UpdateApplicationManager.IonUpdateApplication {
                override fun onUpdateApplicationFail() {
                    toast(getString(R.string.update_application_fail))
                    handleAsyncSplashJustOnce()
                }

                override fun onUpdateApplicationSuccess() {

                }

                override fun onMustNotUpdateApplication() {
                    handleAsyncSplashJustOnce()
                }

                override fun requestUpdateFail() {
                    handleAsyncSplashJustOnce()
                }
            })

        if (Intent.ACTION_VIEW == intent.action) {
            logEvent(EventName.uninstall_click)
            lifecycleScope.launch {
                for (i in 1..100) {
                    binding.progressBar.progress = i
                    binding.tvProgress.text = getString(R.string.loading) + " ($i)%"
                    delay(30)
                }
                launchActivity(ProblemActivity::class.java)
                finishAffinity()
            }
        } else {
            lifecycleScope.launch {
                for (i in 1..100) {
                    binding.progressBar.progress = i
                    binding.tvProgress.text = getString(R.string.loading) + " ($i)%"
                    delay(30)
                }
            }
            if (NetworkUtil.isNetworkActive(this)) {
                UpdateApplicationManager.getInstance().setUseFlexibleUpdate()
                appUpdateManager = UpdateApplicationManager.getInstance().checkVersionPlayStore(
                    this,
                    true,
                    false,
                    getString(R.string.new_update_available),
                    getString(R.string.upgrade_now_for_a_smoother_experience_bug_fixes_for_better_performance),
                    getString(R.string.update_now),
                    getString(R.string.no)
                )
                installStateUpdatedListener = InstallStateUpdatedListener { state ->
                    if (state.installStatus() == InstallStatus.DOWNLOADING ||
                        state.installStatus() == InstallStatus.FAILED ||
                        state.installStatus() == InstallStatus.CANCELED ||
                        state.installStatus() == InstallStatus.UNKNOWN
                    ) {
                        Timber.d("initView: appUpdateManage")
                        handleAsyncSplashJustOnce()
                    } else if (state.installStatus() == InstallStatus.DOWNLOADED) {
                        Timber.d("initView: appUpdateManager_$appUpdateManager")
                        Toast.makeText(applicationContext, applicationContext.getString(R.string.updated_and_ready_welcome_back), Toast.LENGTH_SHORT).show()
                        appUpdateManager?.completeUpdate()
                    } else {
                        Timber.d("initView: appUpdateManage else")
                    }
                }
                Timber.d("appUpdateManager register. ${appUpdateManager}_$installStateUpdatedListener")
                installStateUpdatedListener?.let { appUpdateManager?.registerListener(it) }
            } else {
                launchActivity(Bundle().apply {
                    putString(SCREEN, SPLASH_ACTIVITY)
                },NoInternetActivity::class.java)
            }
        }
    }

    private fun handleAsyncSplashJustOnce() {
        if (!isHandleAsyncSplash) { //important
            AsyncSplash.getInstance().init(
                activity = this@SplashActivity,
                appOpenCallback = openCallback,
                interCallback = interCallback,
                adjustKey = getString(R.string.adjust_key),
                linkServer = getString(R.string.link_server),
                appId = getString(R.string.app_id),
                jsonIdAdsDefault = ""
            )
            //Test TechManager
            AsyncSplash.getInstance().setDebug(true) //Production set to false
            AsyncSplash.getInstance().setAsyncSplashAds()
            //AsyncSplash.getInstance().setLoadAndShowIdInterAdSplashAsync() //Nếu request load đồng thời inter_splash va inter_splash_high
            AsyncSplash.getInstance().setListTurnOffRemoteKeys(TURN_OFF_CONFIGS)
            //Case without welcome back
            AsyncSplash.getInstance().setInitResumeAdsNormal()
            //Case welcome back above ads resume
            AsyncSplash.getInstance().setInitWelcomeBackAboveResumeAds(WelcomeBackActivity::class.java)
            //Case welcome back below ads resume
            //AsyncSplash.getInstance().setInitWelcomeBackBelowResumeAds(WelcomeBackActivity::class.java)
            //If app use billing IAP
            //AsyncSplash.getInstance().setUseBilling(arrayListOf())
            AsyncSplash.getInstance().handleAsync(this@SplashActivity, this@SplashActivity, lifecycleScope,
                onAsyncSplashDone = {
                    preloadANativeMainLanguage()
                    if (
                        !RemoteConfigHelper.getInstance().get_config(this@SplashActivity, RemoteName.REWARDED_ITEMS)
                        && !RemoteConfigHelper.getInstance().get_config(this@SplashActivity, RemoteName.REWARDED_EDIT)
                        && !RemoteConfigHelper.getInstance().get_config(this@SplashActivity, RemoteName.REWARDED_DOWNLOAD)
                    ) {
                        RemoteConfigHelper.getInstance().set_config(this@SplashActivity, REWARDED_ALL, false)
                    }
                },
                onNoInternetAction = {
                    interCallback.onNextAction()
                }
            )
            AsyncSplash.getInstance().setShowBannerSplash(
                true,
                binding.frBanner,
                arrayListOf("ca-app-pub-3940256099942544/6300978111"),
                RemoteConfigHelper.banner_splash
            )
            isHandleAsyncSplash = true
        }
    }

    private fun preloadANativeMainLanguage() {
        Admob.getInstance().loadNativeAds(
            this,
            AdmobApi.getInstance().getListIDByName(native_language),
            object : NativeCallback() {
                override fun onNativeAdLoaded(nativeAd: NativeAd?) {
                    super.onNativeAdLoaded(nativeAd)
                    nativeLanguagePreload = nativeAd
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    isShowNativeLanguagePreloadAtSplash = true
                }
            }, native_language
        )
    }

    override fun dataCollect() {

    }

    override fun onResume() {
        super.onResume()
        AsyncSplash.getInstance().checkShowSplashWhenFail()
    }

    override fun onBackPressedSystem() {

    }
}