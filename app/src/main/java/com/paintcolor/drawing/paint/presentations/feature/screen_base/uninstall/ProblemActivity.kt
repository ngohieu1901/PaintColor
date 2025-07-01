package com.paintcolor.drawing.paint.presentations.feature.screen_base.uninstall

import android.os.Bundle
import com.paintcolor.drawing.paint.base.BaseActivity
import com.paintcolor.drawing.paint.constants.Constants.IntentKeys.SCREEN
import com.paintcolor.drawing.paint.constants.Constants.IntentKeys.SPLASH_ACTIVITY
import com.paintcolor.drawing.paint.databinding.ActivityProblemBinding
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.presentations.feature.container.ContainerActivity
import com.paintcolor.drawing.paint.presentations.feature.screen_base.no_internet.NoInternetActivity
import com.paintcolor.drawing.paint.utils.SystemUtils
import com.paintcolor.drawing.paint.widget.launchActivity
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.tap

class ProblemActivity : BaseActivity<ActivityProblemBinding>() {
    override fun setViewBinding(): ActivityProblemBinding {
        return ActivityProblemBinding.inflate(layoutInflater)
    }

    override fun initView() {

        binding.apply {
            listOf(tvExplore, tvTryAgain, noUninstall, ivBack).forEachIndexed { int, button ->
                button.tap {
                    when (int) {
                        0 -> logEvent(EventName.uninstall_explore_click)
                        1 -> logEvent(EventName.uninstall_try_again_click)
                        2 -> logEvent(EventName.dontuninstall_click)
                    }
                    if (SystemUtils.haveNetworkConnection(this@ProblemActivity)){
                        launchActivity(ContainerActivity::class.java)
                        finishAffinity()
                    } else {
                        launchActivity(Bundle().apply {
                            putString(SCREEN, SPLASH_ACTIVITY)
                        }, NoInternetActivity::class.java)
                    }
                }
            }
            stillUninstall.tap {
                logEvent(EventName.still_want_uninstall_click)
                launchActivity(UninstallActivity::class.java)
            }
        }
    }

    override fun dataCollect() {

    }

}