package com.paintcolor.drawing.paint.presentations.feature.screen_base.intro

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.amazic.library.Utils.EventTrackingHelper
import com.amazic.library.ads.admob.Admob
import com.google.android.gms.ads.nativead.NativeAdView
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseActivity
import com.paintcolor.drawing.paint.databinding.ActivityIntroBinding
import com.paintcolor.drawing.paint.domain.model.IntroModel
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.presentations.components.dialogs.RatingDialogFragment
import com.paintcolor.drawing.paint.presentations.feature.color_palette.start.ColorPaletteStartActivity
import com.paintcolor.drawing.paint.presentations.feature.container.ContainerActivity
import com.paintcolor.drawing.paint.presentations.feature.interest.InterestActivity
import com.paintcolor.drawing.paint.presentations.feature.screen_base.language_start.LanguageStartActivity.Companion.isShowNativeIntroPreloadAtSplash
import com.paintcolor.drawing.paint.presentations.feature.screen_base.language_start.LanguageStartActivity.Companion.nativeIntroPreload
import com.paintcolor.drawing.paint.presentations.feature.screen_base.permission.PermissionActivity
import com.paintcolor.drawing.paint.utils.SharePrefUtils
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.launchActivity
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.visible
import org.koin.android.ext.android.inject
import timber.log.Timber

class IntroActivity: BaseActivity<ActivityIntroBinding>() {
    private val listIntroModel = mutableListOf<IntroModel>()
    private lateinit var introAdapter: IntroAdapter
    private val sharePref: SharePrefUtils by inject()
    private var isFirst = true
    private var isPause = false

    private val myPageChangeCallback: ViewPager2.OnPageChangeCallback =
        object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onPageSelected(position: Int) {
                if (isFirst) {
                    isFirst = false
                    return
                }
                binding.apply {
                    if (listIntroModel[position].type == IntroType.ADS || listIntroModel[position].type == IntroType.ADS_1) {
                        listOf(frAds, linearDots, btnNextTutorial).forEach {
                            it.gone()
                        }
                    } else {
                        ivClose.gone()
                        animationView.gone()
                        listOf(frAds, linearDots, btnNextTutorial).forEach {
                            it.visible()
                        }
                    }
                }
                addBottomDots(position)

                when (listIntroModel[position].type.ordinal) {
                    IntroType.GUIDE_1.ordinal -> {
                        EventTrackingHelper.logEvent(this@IntroActivity, "Onboarding_1_view")
                    }

                    IntroType.ADS.ordinal -> {
                        EventTrackingHelper.logEvent(this@IntroActivity, "Onboarding_2_view")
                    }

                    IntroType.GUIDE_2.ordinal -> {
                        EventTrackingHelper.logEvent(this@IntroActivity, "Onboarding_3_view")
                    }

                    IntroType.GUIDE_3.ordinal -> {
                        EventTrackingHelper.logEvent(this@IntroActivity, "Onboarding_4_view")
                    }

                    IntroType.ADS_1.ordinal -> {
                        EventTrackingHelper.logEvent(this@IntroActivity, "Onboarding_5_view")
                    }

                    IntroType.GUIDE_4.ordinal -> {
                        EventTrackingHelper.logEvent(this@IntroActivity, "Onboarding_6_view")
                    }
                }
            }
        }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume: ")
        isPause = false
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isPause) {
                showNativeIntroPreloadAtSplash()
            }
        }, 1000)
    }

    override fun onPause() {
        super.onPause()
        isPause = true
        Timber.d( "onPause: ")
    }

    private fun showNativeIntroPreloadAtSplash() {
        if (nativeIntroPreload != null && !isShowNativeIntroPreloadAtSplash) {
            val adView: NativeAdView = layoutInflater.inflate(R.layout.ads_native_small_button_above, binding.frAds, false) as NativeAdView
            binding.frAds.addView(adView)
            Admob.getInstance().populateNativeAdView(nativeIntroPreload, adView)
        }
    }

    override fun setViewBinding(): ActivityIntroBinding {
        return ActivityIntroBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        logEvent(EventName.onboarding_1_view)
        logEvent(EventName.onboard_open)
        if (sharePref.countOpenApp <= 10) {
            logEvent(EventName.onboard_open + "_" + sharePref.countOpenApp)
        }

        initData()
        introAdapter = IntroAdapter(this, listIntroModel)
        binding.viewPager2.apply {
            adapter = introAdapter
            registerOnPageChangeCallback(myPageChangeCallback)
        }
        addBottomDots(0)
        binding.btnNextTutorial.setOnClickListener {
            if (binding.viewPager2.currentItem == listIntroModel.size - 1) {
                if (sharePref.countOpenHome in listOf(2, 5, 9)) {
                    RatingDialogFragment(onDismissCallback = {
                        logEvent(EventName.onboarding_next_click)
                        if (sharePref.countOpenApp <= 10) {
                            logEvent(EventName.onboarding_next_click + "_" + sharePref.countOpenApp)
                        }
                        startNextScreen()
                    }, isFinishActivity = false).show(
                        supportFragmentManager,
                        "RatingDialogFragment"
                    )
                } else {
                    logEvent(EventName.onboarding_next_click)
                    if (sharePref.countOpenApp <= 10) {
                        logEvent(EventName.onboarding_next_click + "_" + sharePref.countOpenApp)
                    }
                    startNextScreen()
                }
            } else {
                binding.viewPager2.currentItem += 1
            }
        }
    }

    override fun dataCollect() {

    }

    private fun initData() {
        addBottomDots(0)
        listIntroModel.apply {
            add(
                IntroModel(
                    R.drawable.img_intro_1,
                    R.string.title_intro_1,
                    R.string.content_intro_1,
                    IntroType.GUIDE_1
                )
            )
            add(
                IntroModel(
                    R.drawable.img_intro_2,
                    R.string.title_intro_2,
                    R.string.content_intro_2,
                    IntroType.GUIDE_2
                )
            )
            add(
                IntroModel(
                    R.drawable.img_intro_3,
                    R.string.title_intro_3,
                    R.string.content_intro_3,
                    IntroType.GUIDE_3
                )
            )
            add(
                IntroModel(
                    R.drawable.img_intro_4,
                    R.string.title_intro_4,
                    R.string.content_intro_4,
                    IntroType.GUIDE_4
                )
            )
        }
    }

    private fun startNextScreen() {
        if (sharePref.isPassPermission) {
            if (sharePref.isPassInterest) {
                if (sharePref.isPassColorPaletteStart) {
                    launchActivity(ContainerActivity::class.java)
                    finishAffinity()
                } else {
                    launchActivity(ColorPaletteStartActivity::class.java)
                    finishAffinity()
                }
            } else {
                launchActivity(InterestActivity::class.java)
                finishAffinity()
            }
        } else {
            launchActivity(PermissionActivity::class.java)
            finishAffinity()
        }
    }

    private fun addBottomDots(currentPage: Int) {
        binding.linearDots.removeAllViews()
        val dots = arrayOfNulls<ImageView>(listIntroModel.size)
        for (i in 0 until listIntroModel.size) {
            dots[i] = ImageView(this)
            if (i == currentPage)
                dots[i]!!.setImageResource(R.drawable.ic_intro_selected)
            else
                dots[i]!!.setImageResource(R.drawable.ic_intro_not_select)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            binding.linearDots.addView(dots[i], params)
        }
    }

    override fun onBackPressedSystem() {
        finishAffinity()
    }
}