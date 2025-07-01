package com.paintcolor.drawing.paint.presentations.feature.screen_base.no_internet

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.OnBackPressedCallback
import com.paintcolor.drawing.paint.base.BaseActivity
import com.paintcolor.drawing.paint.databinding.ActivityNoInternetBinding
import com.paintcolor.drawing.paint.firebase.ads.AdsHelper
import com.paintcolor.drawing.paint.widget.tap

class NoInternetActivity: BaseActivity<ActivityNoInternetBinding>() {
    override fun setViewBinding(): ActivityNoInternetBinding {
        return ActivityNoInternetBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        binding.tvTryAgain.tap {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
                startActivity(panelIntent)
            } else {
                AdsHelper.disableResume(this)
                val wifiSettingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(wifiSettingsIntent)
            }
        }
    }

    override fun dataCollect() {

    }

}