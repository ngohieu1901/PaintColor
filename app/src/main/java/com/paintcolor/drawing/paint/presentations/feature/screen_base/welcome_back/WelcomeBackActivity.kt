package com.paintcolor.drawing.paint.presentations.feature.screen_base.welcome_back

import com.amazic.library.ads.admob.AdmobApi
import com.amazic.library.ads.app_open_ads.AppOpenManager
import com.amazic.library.ads.callback.AppOpenCallback
import com.paintcolor.drawing.paint.base.BaseActivity
import com.paintcolor.drawing.paint.databinding.ActivityWelcomeBackBinding
import com.paintcolor.drawing.paint.firebase.ads.RemoteName
import com.paintcolor.drawing.paint.widget.tap

class WelcomeBackActivity : BaseActivity<ActivityWelcomeBackBinding>() {

    override fun initView() {

        binding.tvContinue.tap {
            loadAndShowResumeAds()
        }
    }

    private fun loadAndShowResumeAds() {
        AppOpenManager.getInstance().loadAndShowResumeAds(this, AdmobApi.getInstance().getListIDByName(RemoteName.RESUME_WB), object : AppOpenCallback() {
            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                finish()
            }

            override fun onAdFailedToShowFullScreenContent() {
                super.onAdFailedToShowFullScreenContent()
                finish()
            }

            override fun onAdFailedToLoad() {
                super.onAdFailedToLoad()
                finish()
            }
        }, RemoteName.RESUME_WB)
    }

    override fun dataCollect() {

    }

    override fun onBackPressedSystem() {
    }

    override fun setViewBinding(): ActivityWelcomeBackBinding = ActivityWelcomeBackBinding.inflate(layoutInflater)
}