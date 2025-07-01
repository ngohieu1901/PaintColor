package com.paintcolor.drawing.paint.presentations.feature.screen_base.language_start

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.amazic.library.Utils.EventTrackingHelper
import com.amazic.library.ads.admob.Admob
import com.amazic.library.ads.admob.AdmobApi
import com.amazic.library.ads.callback.BannerCallback
import com.amazic.library.ads.callback.NativeCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseActivity
import com.paintcolor.drawing.paint.databinding.ActivityLanguageStartBinding
import com.paintcolor.drawing.paint.firebase.ads.RemoteName
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.firebase.event.ParamName
import com.paintcolor.drawing.paint.presentations.feature.screen_base.intro.IntroActivity
import com.paintcolor.drawing.paint.presentations.feature.screen_base.splash.SplashActivity
import com.paintcolor.drawing.paint.presentations.feature.screen_base.splash.SplashActivity.Companion.isShowNativeLanguagePreloadAtSplash
import com.paintcolor.drawing.paint.presentations.feature.screen_base.splash.SplashActivity.Companion.nativeLanguagePreload
import com.paintcolor.drawing.paint.utils.SharePrefUtils
import com.paintcolor.drawing.paint.utils.SystemUtils
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.launchActivity
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.toast
import com.paintcolor.drawing.paint.widget.visible
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.Locale

class LanguageStartActivity : BaseActivity<ActivityLanguageStartBinding>() {
    private val viewModel : LanguageStartViewModel by viewModel()
    private lateinit var adapter: LanguageStartAdapter
    private var isoLanguage : String = ""
    private var nameLanguage : String = ""

    private var isSelectedLanguage = false
    private var isPause = false

    private val sharePref: SharePrefUtils by inject()

    companion object {
        var isLogEventLanguageUserView = false
        var nativeIntroPreload: NativeAd? = null
        var isShowNativeIntroPreloadAtSplash = false
    }

    override fun setViewBinding():ActivityLanguageStartBinding {
        return ActivityLanguageStartBinding.inflate(layoutInflater)
    }

    override fun onBackPressedSystem() {
        finishAffinity()
    }

    override fun initView() {
        logEvent(EventName.language_fo_open)
        if (sharePref.countOpenApp <= 10) {
            logEvent(EventName.language_fo_open + "_" + sharePref.countOpenApp)
        }

        Admob.getInstance().loadBannerAds(
            this,
            AdmobApi.getInstance().getListIDByName(RemoteName.BANNER_SETTING),
            binding.bannerSetting,
            object : BannerCallback() {},
            {},
            RemoteName.BANNER_SETTING
        )

        preloadANativeMainIntro()
        if (SystemUtils.getPreLanguage(this).isBlank()) {
            binding.ivSelect.gone()
        }
        viewModel.initListLanguage(this)

        adapter = LanguageStartAdapter(onClick = {
            logEvent(EventName.language_fo_choose)
            if (sharePref.countOpenApp <= 10 && !isSelectedLanguage) {
                logEvent(EventName.language_fo_choose + "_" + sharePref.countOpenApp)
            }
            isSelectedLanguage = true
            SystemUtils.setLocale(this)
            isoLanguage = it.isoLanguage
            nameLanguage = it.languageName
            viewModel.setSelectLanguage(it.isoLanguage)
            binding.tvSelectLanguage.text = getLocalizedString(this, isoLanguage, R.string.please_select_language_to_continue)
            binding.tvLanguage.text = getLocalizedString(this, isoLanguage, R.string.Language)
            binding.ivSelect.visible()
        })

        binding.recyclerView.adapter = adapter

        binding.ivSelect.tap {
            logEvent(EventName.language_fo_save_click, bundle = Bundle().apply {putString(ParamName.language_name, nameLanguage)})
            if (sharePref.countOpenApp <= 10) {
                logEvent(EventName.language_fo_save_click + "_" + sharePref.countOpenApp)
            }
            sharePref.isFirstSelectLanguage = false
            SystemUtils.setPreLanguage(this@LanguageStartActivity, isoLanguage)
            SystemUtils.setLocale(this)
            launchActivity(IntroActivity::class.java)
            finish()
        }
    }

    override fun dataCollect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateStore.collect {
                    when (it) {
                        is LanguageUiState.Idle -> {}

                        is LanguageUiState.Language -> {
                            dismissLoading()
                            adapter.submitList(it.listLanguage)
                        }
                        is LanguageUiState.Loading -> {
                            showLoading()
                        }

                        is LanguageUiState.Error -> {
                            toast(it.e.message.toString())
                        }
                    }
                }
            }
        }
    }

    private fun getLocalizedString(context: Context, languageCode: String, resId: Int): String {
        val localeParts = languageCode.split("-")
        val myLocale = if (localeParts.size > 1) {
            Locale(localeParts[0], localeParts[1])
        } else {
            Locale(languageCode)
        }
        val config = Configuration(context.resources.configuration)
        config.setLocale(myLocale)
        val localizedContext = context.createConfigurationContext(config)
        return localizedContext.resources.getString(resId)
    }

    override fun onResume() {
        super.onResume()
        isPause = false
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isPause) {
                showNativeLanguagePreloadAtSplash()
            }
            Timber.d("isShowSplashAds: ${SplashActivity.isShowSplashAds} - isCloseSplashAds: ${SplashActivity.isCloseSplashAds}")
            if (SplashActivity.isShowSplashAds) {
                if (SplashActivity.isCloseSplashAds) {
                    EventTrackingHelper.logEvent(this@LanguageStartActivity, "language_user_view")
                    if (sharePref.countOpenApp <= 10 && !isLogEventLanguageUserView && !isPause) {
                        Timber.d("logEventOnResume: ${sharePref.countOpenApp}")
                        EventTrackingHelper.logEvent(this@LanguageStartActivity, "language_user_view" + "_${sharePref.countOpenApp}")
                        isLogEventLanguageUserView = true
                    }
                }
            } else {
                EventTrackingHelper.logEvent(this@LanguageStartActivity, "language_user_view")
                if (sharePref.countOpenApp <= 10 && !isLogEventLanguageUserView && !isPause) {
                    Timber.d("logEventOnResume: ${sharePref.countOpenApp}")
                    EventTrackingHelper.logEvent(this@LanguageStartActivity, "language_user_view" + "_${sharePref.countOpenApp}")
                    isLogEventLanguageUserView = true
                }
            }
        }, 1000)
    }

    override fun onPause() {
        super.onPause()
        isPause = true
        Timber.d("onPause: ")
    }

    private fun showNativeLanguagePreloadAtSplash() {
        if (nativeLanguagePreload != null && !isShowNativeLanguagePreloadAtSplash) {
            val adView: NativeAdView = layoutInflater.inflate(R.layout.ads_native_large_button_above, binding.frAds, false) as NativeAdView
            binding.frAds.addView(adView)
            Admob.getInstance().populateNativeAdView(nativeLanguagePreload, adView)
        }
    }

    private fun preloadANativeMainIntro() {
        Admob.getInstance().loadNativeAds(
            this,
            AdmobApi.getInstance().getListIDByName(RemoteName.NATIVE_INTRO),
            object : NativeCallback() {
                override fun onNativeAdLoaded(nativeAd: NativeAd?) {
                    super.onNativeAdLoaded(nativeAd)
                    nativeIntroPreload = nativeAd
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    isShowNativeIntroPreloadAtSplash = true
                }
            }, RemoteName.NATIVE_INTRO
        )
    }
}