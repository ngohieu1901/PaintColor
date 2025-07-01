package com.paintcolor.drawing.paint.presentations.feature.screen_base.uninstall

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.lifecycle.lifecycleScope
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseActivity
import com.paintcolor.drawing.paint.constants.Constants.IntentKeys.SCREEN
import com.paintcolor.drawing.paint.constants.Constants.IntentKeys.SPLASH_ACTIVITY
import com.paintcolor.drawing.paint.databinding.ActivityUninstallBinding
import com.paintcolor.drawing.paint.firebase.ads.AdsHelper
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.presentations.feature.container.ContainerActivity
import com.paintcolor.drawing.paint.presentations.feature.screen_base.no_internet.NoInternetActivity
import com.paintcolor.drawing.paint.utils.SystemUtils
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.launchActivity
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.visible
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class UninstallActivity : BaseActivity<ActivityUninstallBinding>() {
    private val viewmodel: UninstallViewModel by viewModel()
    private lateinit var uninstallAdapter: UninstallAdapter

    private val ioDispatcher: CoroutineDispatcher by inject(qualifier = named("IO"))

    override fun setViewBinding(): ActivityUninstallBinding {
        return ActivityUninstallBinding.inflate(layoutInflater)
    }

    override fun initView() {

        binding.apply {
            uninstallAdapter = UninstallAdapter { data, pos ->
                viewmodel.updateListAnswer(data)
                if (pos == viewmodel.currentState.listAnswer.size - 1) {
                    edAnswer.visible()
                } else {
                    edAnswer.gone()
                }
            }

            rvAnswer.adapter = uninstallAdapter
            rvAnswer.itemAnimator = null

            tvUninstall.tap {
                lifecycleScope.launch(exceptionHandler + ioDispatcher) {
                    val answerResource = viewmodel.currentState.listAnswer.first{ answers -> answers.isSelected}.name
                    if (answerResource != R.string.others) {
                        logEvent(EventName.reason_uninstall, Bundle().apply { putString("reason", getString(answerResource))})
                    } else {
                        logEvent(EventName.reason_uninstall, Bundle().apply { putString("reason", edAnswer.text.toString())})
                    }
                }
                AdsHelper.disableResume(this@UninstallActivity)
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:$packageName")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            }
            tvCancel.tap {
                logEvent(EventName.uninstall_canel_click)
                if (SystemUtils.haveNetworkConnection(this@UninstallActivity)){
                    launchActivity(ContainerActivity::class.java)
                    finishAffinity()
                } else {
                    launchActivity(Bundle().apply {
                        putString(SCREEN, SPLASH_ACTIVITY)
                    }, NoInternetActivity::class.java)
                }
            }
            ivBack.tap {
                finish()
            }
        }
    }

    override fun dataCollect() {
        lifecycleScope.launch {
            viewmodel.uiStateStore.collect {
                uninstallAdapter.submitList(it.listAnswer)
            }
        }
    }

}